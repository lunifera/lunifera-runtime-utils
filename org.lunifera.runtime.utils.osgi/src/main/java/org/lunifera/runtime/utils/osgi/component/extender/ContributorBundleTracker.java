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

import org.lunifera.runtime.utils.osgi.component.ExceptionComponentLifecycle;
import org.lunifera.runtime.utils.osgi.component.ExceptionComponentUnrecoveredActivationError;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * This class is used to track contributor bundles.
 * 
 * @author Cristiano Gavião
 * @since 0.0.1
 */
public class ContributorBundleTracker extends
        BundleTracker<ContributorBundle> {

    /**
     * 
     */
    private final AbstractComponentExtender abstractComponentExtender;

    /**
     * 
     * @param abstractComponentExtender
     * @param context
     * @param stateMask
     * @param customizer
     * @throws ExceptionComponentLifecycle
     */
    protected ContributorBundleTracker(
            AbstractComponentExtender abstractComponentExtender,
            BundleContext context, int stateMask,
            BundleTrackerCustomizer<ContributorBundle> customizer)
            throws ExceptionComponentUnrecoveredActivationError {
        super(context, stateMask, customizer);
        this.abstractComponentExtender = abstractComponentExtender;
    }

    @Override
    public ContributorBundle addingBundle(
            Bundle contributorBundle, BundleEvent event) {

        String header = contributorBundle.getHeaders().get(
                this.abstractComponentExtender
                        .getExtenderContributorManifestHeader());

        if (header == null) {
            this.abstractComponentExtender.trace(
                    "Ignored contributor bundle '{}' for '{}' extender.",
                    contributorBundle.getSymbolicName(),
                    this.abstractComponentExtender
                            .getExtenderContributorManifestHeader());
            return null;
        }
        if (header.isEmpty()) {
            this.abstractComponentExtender
                    .warn("Header values wasn't informed, so contributor bundle '{}' for '{}' extender was ignored.",
                            contributorBundle.getSymbolicName(),
                            this.abstractComponentExtender
                                    .getExtenderContributorManifestHeader());
            return null;
        }

        ContributorBundle contributorBundleInstance = null;
        try {
            this.abstractComponentExtender
                    .debug("Processing contributor bundle '{}' for '{}' extender using {}.",
                            contributorBundle.getSymbolicName(),
                            this.abstractComponentExtender
                                    .getExtenderContributorManifestHeader(),
                            this.abstractComponentExtender
                                    .getContributionHandlerService()
                                    .getHandlerName());
            String headerName = this.abstractComponentExtender
                    .getExtenderContributorManifestHeader();
            String headerValue = contributorBundle.getHeaders().get(headerName);

            contributorBundleInstance = this.abstractComponentExtender
                    .whenContributorBundleIsActivated(contributorBundle,
                            headerName, headerValue);
        } catch (Exception e) {
            this.abstractComponentExtender
                    .error("An error occurred while evaluating contribution of bundle.",
                            e);
            return null;
        }
        return contributorBundleInstance;
    }

    @Override
    public void modifiedBundle(Bundle bundle, BundleEvent event,
            ContributorBundle contributorBundleInstance) {

        try {
            this.abstractComponentExtender
                    .whenContributorBundleIsModified(contributorBundleInstance);
            this.abstractComponentExtender.debug(
                    "Contributor bundle '{}' for '{}' was modified.", bundle
                            .getSymbolicName(), this.abstractComponentExtender
                            .getExtenderContributorManifestHeader());
        } catch (Exception e) {
            this.abstractComponentExtender
                    .warn("Problem occurred on mofification of contributor bundle '{}'.",
                            bundle.getSymbolicName(), e);
        }
    }

    @Override
    public void removedBundle(Bundle bundle, BundleEvent event,
            ContributorBundle contributorBundleInstance) {
        try {
            this.abstractComponentExtender
                    .whenContributorBundleIsRemoved(contributorBundleInstance);
            this.abstractComponentExtender.debug(
                    "Contributor bundle '{}' for '{}' was removed.", bundle
                            .getSymbolicName(), this.abstractComponentExtender
                            .getExtenderContributorManifestHeader());
        } catch (Exception e) {
            this.abstractComponentExtender
                    .warn("Problem occurred when removing the contributor bundle '{}' from container.",
                            bundle.getSymbolicName(), e);
        }
    }
}
