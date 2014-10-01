package org.lunifera.runtime.utils.osgi.component.extender;

/*
 * #%L
 * Lunifera Runtime Utilities - for OSGi
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.lunifera.runtime.utils.osgi.component.AbstractComponentWithCompendium;
import org.lunifera.runtime.utils.osgi.component.ExceptionComponentLifecycle;
import org.lunifera.runtime.utils.osgi.services.PluggableEventTracker;
import org.lunifera.runtime.utils.osgi.services.PluggableServiceTracker;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.tracker.BundleTracker;

import com.google.common.base.Splitter;

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
        AbstractComponentWithCompendium implements ExtenderService {

    private final AtomicReference<ContributionHandlerService> contributionHandlerServiceRef = new AtomicReference<>();

    /**
     * The {@link BundleTracker} used to track a contributor bundle.
     */
    private volatile ContributorBundleTracker contributorBundleTracker = null;

    /**
     * The manifest header used to identify that a bundle is a contributor for
     * an extender.
     */
    private volatile String extenderContributorManifestHeader = null;

    /**
     * Defines which bundle states should tracked.
     */
    private volatile int stateMask;

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
            ContributionHandlerService contributionHandlerService,
            Map<Class<?>, PluggableServiceTracker> serviceTrackers,
            Set<PluggableEventTracker> eventTrackers) {
        super(bundleContext, componentContext, serviceTrackers, eventTrackers);
        bindContributionHandlerService(contributionHandlerService);
    }

    protected void bindContributionHandlerService(
            ContributionHandlerService contributionHandlerService) {
        this.contributionHandlerServiceRef.set(contributionHandlerService);
    }

    @Override
    protected void defineEventsToBeTracked() {

    }

    @Override
    protected void doAfterActivateComponent()
            throws ExceptionComponentLifecycle {
        super.doAfterActivateComponent();
        if (getContributionHandlerService() == null) {
            throw new ExceptionComponentExtenderSetup(
                    "A ContributionHandlerService instance was not properly bound to this component. "
                            + "Please, override the bindContributionHandlerService() method adding a proper @Reference annotation to it.");

        } else { // start tracking for bundles
            getContributorBundleTracker().open();
        }
    }

    @Override
    protected void doBeforeDeactivateComponent()
            throws ExceptionComponentLifecycle {
        super.doBeforeDeactivateComponent();
        // close the bundle tracker
        getContributorBundleTracker().close();
    }

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

        if (stateMaskLoc instanceof Integer && (Integer) stateMaskLoc != 0) {
            stateMask = (int) stateMaskLoc;
        } else {
            stateMask = Bundle.ACTIVE;
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
            contributorBundleTracker = new ContributorBundleTracker(this,
                    getBundleContext(), getStateMask(), null);
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

    // for now just ignoring...
    @Override
    protected void modified(ComponentContext context)
            throws ExceptionComponentLifecycle {
    }

    protected final void unbindContributionHandlerService(
            ContributionHandlerService contributionHandlerService) {
        contributionHandlerServiceRef.compareAndSet(contributionHandlerService,
                null);
    }

    /**
     * This method is called by the BundleTracker when a specific filtered
     * bundle is arrived.
     */
    public ContributorBundle whenContributorBundleIsActivated(
            Bundle contributorBundle, String headerName, String headerValue) {
        ContributorBundle contributorBundleInstance = getContributionHandlerService()
                .createContributorBundleInstance(contributorBundle);

        Iterable<String> candidates = Splitter.on(",").trimResults()
                .omitEmptyStrings().split(headerValue);

        for (String candidateItem : candidates) {
            if (getContributionHandlerService().isResourceUrlValid(
                    candidateItem)) {
                URL resourceURL;
                try {
                    resourceURL = new URL(candidateItem);
                } catch (MalformedURLException e) {
                    throw new ExceptionComponentExtenderSetup(e);
                }
                contributorBundleInstance
                        .addContributionItem(getContributionHandlerService()
                                .createContributionItemFromResourceFile(
                                        contributorBundleInstance, resourceURL));
            } else {
                List<ContributionItem> items = getContributionHandlerService()
                        .createContributionItemsForDiscoveredResources(
                                contributorBundleInstance, candidateItem);
                if (items != null && !items.isEmpty()) {
                    contributorBundleInstance.contributionItems().addAll(items);
                }
            }
        }
        return contributorBundleInstance;
    }

    public void whenContributorBundleIsModified(
            ContributorBundle contributorBundleInstance) {
        // Ignoring this because no use case was found
    }

    public void whenContributorBundleIsRemoved(
            ContributorBundle contributorBundleInstance) {
        getContributionHandlerService().removeContributionItems(
                contributorBundleInstance);
    }
}
