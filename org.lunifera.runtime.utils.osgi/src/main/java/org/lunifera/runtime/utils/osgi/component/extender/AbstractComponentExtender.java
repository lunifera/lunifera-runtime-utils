package org.lunifera.runtime.utils.osgi.component.extender;

/*
 * #%L
 * Lunifera Subsystems - Runtime Utilities for OSGi
 * %%
 * Copyright (C) 2012 - 2014 C4biz Softwares ME, Loetz KG
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * #L%
 */

import java.util.Dictionary;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.lunifera.runtime.utils.osgi.component.AbstractComponentWithCompendium;
import org.lunifera.runtime.utils.osgi.component.ExceptionComponentLifecycle;
import org.lunifera.runtime.utils.osgi.component.ExceptionComponentUnrecoveredActivationError;
import org.lunifera.runtime.utils.osgi.services.PluggableEventTracker;
import org.lunifera.runtime.utils.osgi.services.PluggableServiceTracker;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * An abstract class for extender components.
 * <p>
 * This class plus the annotation {@link ComponentExtenderSetup} aims to provide
 * an easier way to configure an extender component.
 * <p>
 * An extender component can receive contributions from one or more bundles
 * (contributors) that contains a specific defined head in its manifest file.
 * 
 * @author Cristiano Gavião
 * @since 0.0.1
 */
public abstract class AbstractComponentExtender extends
        AbstractComponentWithCompendium implements ComponentExtenderService {

    /**
     * This class is used to track 'extendee' bundles.
     * 
     * @author Cristiano Gavião
     * @since 0.0.1
     */
    protected class ContributorBundleTracker extends
            BundleTracker<ContributorBundleTrackerObject> {

        /**
         * 
         * @param abstractComponentExtender
         * @param context
         * @param stateMask
         * @param customizer
         * @throws ExceptionComponentLifecycle
         */
        protected ContributorBundleTracker(
                BundleContext context,
                int stateMask,
                BundleTrackerCustomizer<ContributorBundleTrackerObject> customizer)
                throws ExceptionComponentUnrecoveredActivationError {
            super(context, stateMask, customizer);
            // this.contributionHandlerService = contributionHandlerService;
            // if (this.contributionHandlerService == null) {
            // throw new ExceptionComponentUnrecoveredActivationError(
            // "An instance of ContributionHandlerService wasn't set.");
            // }
        }

        @Override
        public ContributorBundleTrackerObject addingBundle(
                Bundle contributorBundle, BundleEvent event) {

            String header = contributorBundle.getHeaders().get(
                    getExtenderContributorManifestHeader());

            if (header == null) {
                trace("Ignored contributor bundle '{}' for '{}' extender.",
                        contributorBundle.getSymbolicName(),
                        getExtenderContributorManifestHeader());
                return null;
            }
            if (header.isEmpty()) {
                warn("Header values wasn't informed, so contributor bundle '{}' for '{}' extender was ignored.",
                        contributorBundle.getSymbolicName(),
                        getExtenderContributorManifestHeader());
                return null;
            }

            ContributorBundleTrackerObject contributorBundleTrackerObject = null;
            try {
                debug("Processing contributor bundle '{}' for '{}' extender using {}.",
                        contributorBundle.getSymbolicName(),
                        getExtenderContributorManifestHeader(),
                        getContributionHandlerService().getHandlerName());
                String headerName = getExtenderContributorManifestHeader();
                String headerValue = contributorBundle.getHeaders().get(
                        headerName);

                contributorBundleTrackerObject = getContributionHandlerService()
                        .whenContributorBundleActivated(contributorBundle,
                                headerName, headerValue);
            } catch (Exception e) {
                error("Problems occurred while evaluating contribution of bundle '{}'.",
                        contributorBundle.getSymbolicName(), e);
                return null;
            }

            return contributorBundleTrackerObject;

        }

        @Override
        public void modifiedBundle(Bundle bundle, BundleEvent event,
                ContributorBundleTrackerObject contributorBundleTrackerObject) {

            try {

                getContributionHandlerService().whenContributorBundleModified(
                        contributorBundleTrackerObject);
                debug("Contributor bundle '{}' for '{}' was modified.",
                        bundle.getSymbolicName(),
                        getExtenderContributorManifestHeader());
            } catch (Exception e) {
                warn("Problem occurred on mofification of contributor bundle '{}'.",
                        bundle.getSymbolicName(), e);
            }

        }

        @Override
        public void removedBundle(Bundle bundle, BundleEvent event,
                ContributorBundleTrackerObject contributorBundleTrackerObject) {
            try {
                getContributionHandlerService().whenContributorBundleRemoved(
                        contributorBundleTrackerObject);
                debug("Contributor bundle '{}' for '{}' was removed.",
                        bundle.getSymbolicName(),
                        getExtenderContributorManifestHeader());
            } catch (Exception e) {
                warn("Problem occurred on removal of contributor bundle '{}'.",
                        bundle.getSymbolicName(), e);
            }
        }
    }

    private final AtomicReference<ContributionHandlerService> contributionHandlerServiceRef = new AtomicReference<>();

    /**
     * The {@link BundleTracker} used to track a contributor bundle.
     */
    private volatile ContributorBundleTracker contributorBundleTracker = null;

    /**
     * The manifest header used to identify that a bundle is a contributor for
     * an extender.
     */
    private String extenderContributorManifestHeader = null;

    /**
     * Defines which bundle states should tracked.
     */
    private int stateMask;

    /**
     * A default constructor is required by the OSGi Declarative Service.
     */
    public AbstractComponentExtender() {
        super();
    }

    /**
     * Constructor created just to help unit testing. It is not used by OSGi.
     * 
     * @param compendiumServices
     */
    @SuppressWarnings("rawtypes")
    protected AbstractComponentExtender(BundleContext bundleContext,
            ComponentContext componentContext,
            Map<Class<?>, PluggableServiceTracker> serviceTrackers,
            Set<PluggableEventTracker> eventTrackers) {
        super(bundleContext, componentContext, serviceTrackers, eventTrackers);
    }

    @Reference(policy = ReferencePolicy.DYNAMIC)
    protected void bindContributionHandlerService(
            ContributionHandlerService contributionHandlerService) {
        this.contributionHandlerServiceRef.set(contributionHandlerService);
        getContributorBundleTracker().open();
    }

    @Override
    protected void defineEventsToBeTracked() {

    }

    /**
     * 
     * @param properties
     */
    protected void doRuntimePropertiesProcessing(
            Dictionary<String, Object> properties) {

        Object extenderContributorManifestHeaderLoc = properties
                .get(ConstantsExtender.EXTENDER_ATTR_MANIFEST_HEADER_NAME);

        Object stateMaskLoc = properties
                .get(ConstantsExtender.EXTENDER_ATTR_BUNDLE_STATE_MASK);

        // used by the bundle tracker
        if (extenderContributorManifestHeaderLoc instanceof String) {
            extenderContributorManifestHeader = (String) extenderContributorManifestHeaderLoc;
        }

        if (extenderContributorManifestHeader == null
                || extenderContributorManifestHeader.isEmpty()) {
            throw new ExceptionComponentExtenderSetup(
                    "The manifest header name service property must be set for the extender component.");
        }

        if (stateMaskLoc instanceof Integer) {
            stateMask = (int) stateMaskLoc;
        }
    }

    protected final ContributionHandlerService getContributionHandlerService() {

        return contributionHandlerServiceRef.get();
    }

    /**
     * 
     * @return the contributorBundleTracker
     */
    protected final ContributorBundleTracker getContributorBundleTracker() {

        if (contributorBundleTracker == null) {
            contributorBundleTracker = this.new ContributorBundleTracker(
                    getBundleContext(), stateMask, null);
        }
        return contributorBundleTracker;
    }

    /**
     * The manifest header used to track for contributions.
     * 
     * @return the header name.
     */
    public String getExtenderContributorManifestHeader() {
        return extenderContributorManifestHeader;
    }

    public int getStateMask() {
        return stateMask;
    }

    // TODO must decide what to do in this case. for now just ignoring...
    @Override
    protected final void modified(ComponentContext context)
            throws ExceptionComponentLifecycle {
        super.modified(context);
    }

    protected final void unbindContributionHandlerService(
            ContributionHandlerService contributionHandlerService) {
        // closed the bundle tracker
        getContributorBundleTracker().close();
        contributionHandlerServiceRef.compareAndSet(contributionHandlerService,
                null);
    }

}
