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

import org.osgi.framework.Bundle;

/**
 * A ContributionHandlerService is responsible to process the contribution
 * resources coming from ContributorBundle components.
 * <p>
 * The extender utility will bind to one registered ContributionHandlerService
 * instance and will delegate to it the resource processing job whenever some
 * bundle matching the extender manifest header is added, modified or removed
 * from the container.
 * <p>
 * A Contributor Bundle represents the <i>'extendee'</i> bundle that is
 * contributing something (using resource files) to other <i>'extendible'</i>
 * bundle through the <i>'extender'</i> bundle.
 * 
 * @author Cristiano Gavião
 * @since 0.0.1
 */
public interface ContributionHandlerService {

    String EXTENSION_STRATEGY = "lunifera.extension.strategy";
    String MANIFEST_HEADER_ITEM_TYPE = "lunifera.extender.contribution.item.resource.type";

    /**
     * An extender implementation can process its contributions using different
     * paths. The most commons are specified in
     * {@link ContributionHandlerService}.
     * 
     * @return The handling strategy provided by this contribution handler.
     */
    ContributionHandlingStrategy getContributionHandlingStrategy();

    /**
     * Each extender can support one type of resource.
     * 
     * @return The type of the header item supported this contribution handler.
     */
    ContributionItemResourceType getContributionItemResourceType();

    /**
     * The name of this {@code ContributionHandlerService}.
     * 
     * @return
     */
    String getHandlerName();

    /**
     * This method is called by the BundleTracker when a specific filtered
     * bundle is arrived.
     * 
     * @param contributorBundle
     * @param headerName
     * @return
     * @throws ExceptionComponentExtenderSetup
     */
    ContributorBundleTrackerObject whenContributorBundleActivated(
            Bundle contributorBundle, String headerName, String headerValue)
            throws ExceptionComponentExtenderSetup;

    /**
     * This method is called by the BundleTracker when a specific filtered
     * bundle is modified.
     * 
     * @param contributorBundleTrackerObject
     * @throws ExceptionComponentExtenderSetup
     */
    void whenContributorBundleModified(
            ContributorBundleTrackerObject contributorBundleTrackerObject)
            throws ExceptionComponentExtenderSetup;

    /**
     * This method is called by the BundleTracker when a specific filtered
     * bundle is removed from container.
     * 
     * @param contributorBundleTrackerObject
     * @throws ExceptionComponentExtenderSetup
     */
    void whenContributorBundleRemoved(
            ContributorBundleTrackerObject contributorBundleTrackerObject)
            throws ExceptionComponentExtenderSetup;

}
