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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import org.osgi.framework.Bundle;

import com.google.common.base.Splitter;

/**
 * @author Cristiano Gavião
 * @since 0.0.1
 */
public abstract class AbstractComponentFactoryContributionHandlerPropertiesFileResourcePerItem
        extends
        AbstractComponentFactoryContributionHandlerPropertiesFileResource {

    /**
     * A default constructor is required by the OSGi Declarative Service.
     */
    public AbstractComponentFactoryContributionHandlerPropertiesFileResourcePerItem() {
        super();
    }

    /**
     * Constructor created just to help unit testing. It is not used by OSGi.
     * 
     * @param contributionItemResourceType
     * @param extensionHandlingStrategy
     */
    protected AbstractComponentFactoryContributionHandlerPropertiesFileResourcePerItem(
            ContributionItemResourceType contributionItemResourceType,
            ExtensionHandlingStrategy extensionHandlingStrategy) {
        super(contributionItemResourceType, extensionHandlingStrategy);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.lunifera.runtime.utils.osgi.component.extender.ContributionHandlerService#
     * onContributorBundleAddition(org.osgi.framework.Bundle, java.lang.String,
     * java.lang.String)
     */
    @Override
    public ContributorBundleTrackerObject onContributorBundleAddition(
            Bundle contributorBundle, String headerName, String headerValue)
            throws ExceptionComponentExtenderSetup {

        ContributorBundleTrackerObject contributorBundleTrackerObject = new ContributorBundleTrackerObject(
                contributorBundle);

        Iterable<String> candidates = Splitter.on(",").trimResults()
                .omitEmptyStrings().split(headerValue);

        for (String candidateItem : candidates) {
            if (isValidForeignURL(candidateItem)) {
                URL u;
                try {
                    u = new URL(candidateItem);
                } catch (MalformedURLException e) {
                    throw new ExceptionComponentExtenderSetup(e);
                }
                contributorBundleTrackerObject.addContributionItem(u,
                        extractMapFromPropertiesResourceFile(u));
            } else {
                Collection<String> resourcePaths = discoverResourcesInBundle(
                        contributorBundleTrackerObject.getContributorBundle(),
                        candidateItem);

                for (String foundResource : resourcePaths) {
                    URL url = contributorBundleTrackerObject
                            .getContributorBundle().getEntry(foundResource);
                    contributorBundleTrackerObject.addContributionItem(url,
                            extractMapFromPropertiesResourceFile(url));
                }
            }
        }
        return contributorBundleTrackerObject;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.lunifera.runtime.utils.osgi.component.extender.ContributionHandlerService#
     * onContributorBundleModified
     * (org.lunifera.runtime.utils.osgi.component.extender.ContributorBundleTrackerObject
     * )
     */
    @Override
    public void onContributorBundleModified(
            ContributorBundleTrackerObject contributorBundleTrackerObject)
            throws ExceptionComponentExtenderSetup {

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.lunifera.runtime.utils.osgi.component.extender.ContributionHandlerService#
     * onContributorBundleRemoval
     * (org.lunifera.runtime.utils.osgi.component.extender.ContributorBundleTrackerObject
     * )
     */
    @Override
    public void onContributorBundleRemoval(
            ContributorBundleTrackerObject contributorBundleTrackerObject)
            throws ExceptionComponentExtenderSetup {
    }
}
