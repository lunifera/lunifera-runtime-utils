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
 * A ContributionHandlerService is a common way to deal with contribution for
 * specific ContributorBundle components.
 * <p>
 * It works like a call back class that will be called by the Framework when
 * Bundles that match the filter (manifest header) is added, modified or removed
 * from the container.
 * <p>
 * A Contributor Bundle represents a bundle (an 'extendee') that is contributing
 * something to some other extender bundle.
 * 
 * @author Cristiano Gavião
 * @since 0.0.1
 */
public interface ContributionHandlerService {

    String EXTENSION_STRATEGY = "lunifera.extension.strategy";
    String MANIFEST_HEADER_ITEM_TYPE = "lunifera.extender.contribution.item.resource.type";

    /**
     * 
     * @return The handling strategy provided by this contribution handler.
     */
    ExtensionHandlingStrategy getExtensionHandlingStrategy();

    /**
     * 
     * @return The type of the header item supported this contribution handler.
     */
    ContributionItemResourceType getManifestHeaderItemType();

    /**
     * This method is called by the BundleTracker when a specific filtered
     * bundle is arrived.
     * 
     * @param contributorBundle
     * @param headerName
     * @return
     * @throws ExceptionComponentExtenderSetup
     */
    ContributorBundleTrackerObject onContributorBundleAddition(
            Bundle contributorBundle, String headerName, String headerValue) throws ExceptionComponentExtenderSetup;

    /**
     * This method is called by the BundleTracker when a specific filtered
     * bundle is modified.
     * 
     * @param contributorBundleTrackerObject
     * @throws ExceptionComponentExtenderSetup
     */
    void onContributorBundleModified(
            ContributorBundleTrackerObject contributorBundleTrackerObject)
            throws ExceptionComponentExtenderSetup;

    /**
     * This method is called by the BundleTracker when a specific filtered
     * bundle is removed.
     * 
     * @param contributorBundleTrackerObject
     * @throws ExceptionComponentExtenderSetup
     */
    void onContributorBundleRemoval(
            ContributorBundleTrackerObject contributorBundleTrackerObject)
            throws ExceptionComponentExtenderSetup;

}
