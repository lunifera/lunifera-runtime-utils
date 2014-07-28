package org.lunifera.runtime.utils.osgi.component.extender.handlers;

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
import java.util.HashMap;
import java.util.Map;

import org.lunifera.runtime.utils.osgi.component.extender.ContributionItemResourceType;
import org.lunifera.runtime.utils.osgi.component.extender.ContributorBundleTrackerObject;
import org.lunifera.runtime.utils.osgi.component.extender.ExceptionComponentExtenderSetup;
import org.lunifera.runtime.utils.osgi.component.extender.ContributionHandlingStrategy;
import org.osgi.framework.Bundle;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;

/**
 * @since 0.0.1
 * @author Cristiano Gavião
 * 
 */
public abstract class AbstractComponentFactoryContributionHandlerPropertiesFilePerBundle
        extends
        AbstractComponentFactoryContributionHandlerPropertiesFile {

    /**
     * A default constructor is required by the OSGi Declarative Service.
     */
    public AbstractComponentFactoryContributionHandlerPropertiesFilePerBundle() {
    }

    /**
     * Constructor created just to help unit testing. It is not used by OSGi.
     *
     * @param contributionItemResourceType
     * @param extensionHandlingStrategy
     */
    protected AbstractComponentFactoryContributionHandlerPropertiesFilePerBundle(
            ContributionItemResourceType contributionItemResourceType,
            ContributionHandlingStrategy extensionHandlingStrategy) {
        super(contributionItemResourceType, extensionHandlingStrategy);
    }
    
    @Override
    public ContributorBundleTrackerObject whenContributorBundleActivated(
            Bundle contributorBundle, String headerName, String headerValue)
            throws ExceptionComponentExtenderSetup {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 
     * 
     * @param contributorBundleTrackerObject
     * @param candidates
     * @return
     * @throws Exception
     */
    protected ContributorBundleTrackerObject buildPerBundleContribution(
            ContributorBundleTrackerObject contributorBundleTrackerObject,
            Iterable<String> candidates) throws ExceptionComponentExtenderSetup {

        Map<String, Object> bundleProperties = Maps.newHashMap();

        for (String candidateItem : candidates) {

            if (isValidForeignURL(candidateItem)) {
                URL u;
                try {
                    u = new URL(candidateItem);
                } catch (MalformedURLException e) {
                    throw new ExceptionComponentExtenderSetup(e);
                }
                bundleProperties
                        .putAll(extractMapFromPropertiesResourceFile(u));

            } else {

                Collection<String> resourcePaths = discoverResourcesInsideBundle(
                        contributorBundleTrackerObject.getContributorBundle(),
                        candidateItem);

                for (String foundResource : resourcePaths) {
                    URL url = contributorBundleTrackerObject
                            .getContributorBundle().getEntry(foundResource);
                    bundleProperties
                            .putAll(extractMapFromPropertiesResourceFile(url));
                }
            }
        }
        contributorBundleTrackerObject.addContributionItem(bundleProperties);

        return contributorBundleTrackerObject;
    }

    /**
     * PerBundle
     * 
     * @param contributorBundle
     * @return
     * @throws ExceptionComponentExtenderSetup
     */
    protected ContributorBundleTrackerObject doProcessPropertiesFileResource(
            Bundle contributorBundle, String headerValue)
            throws ExceptionComponentExtenderSetup {
        ContributorBundleTrackerObject contributorBundleTrackerObject = new ContributorBundleTrackerObject(
                contributorBundle);

        Iterable<String> candidates = Splitter.on(",").trimResults()
                .omitEmptyStrings().split(headerValue);

        return buildPerBundleContribution(contributorBundleTrackerObject,
                candidates);

    }

    /**
     * @param contributorBundle
     * @param headerValue
     * @return
     * @throws ExceptionComponentExtenderSetup
     */
    protected ContributorBundleTrackerObject doProcessPropertyValuPairsContribution(
            Bundle contributorBundle, String headerValue)
            throws ExceptionComponentExtenderSetup {
        if (getContributionHandlingStrategy() != ContributionHandlingStrategy.PER_BUNDLE) {
            throw new ExceptionComponentExtenderSetup(
                    "ContributionItemResourceType.PROPERTY_VALUE_PAIRS must work only with ContributionHandlingStrategy.PER_BUNDLE.");
        }

        Map<String, String> properties = createPropertiesMapFromHeaderValues(headerValue);
        ContributorBundleTrackerObject contributorBundleTrackerObject = new ContributorBundleTrackerObject(
                contributorBundle);
        contributorBundleTrackerObject
                .addContributionItem(new HashMap<String, Object>(properties));

        return contributorBundleTrackerObject;
    }
}
