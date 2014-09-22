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

import java.net.URL;
import java.util.Map;

/**
 * An abstract class that provides special methods to deal with contributions
 * madden through properties files.
 * <p>
 * An contributor bundle can provide one or several files
 * 
 * @author Cristiano Gavião
 * @since 0.0.1
 */
public abstract class AbstractComponentContributionHandlerServiceFactoryPropertiesFile
        extends AbstractComponentContributionHandlerServiceFactory {

    /**
     * A default constructor is required by the OSGi Declarative Service.
     */
    public AbstractComponentContributionHandlerServiceFactoryPropertiesFile() {
        super();
    }

    /**
     * Constructor created just to help unit testing. It is not used by OSGi.
     * 
     * @param contributionItemResourceType
     * @param extensionHandlingStrategy
     */
    protected AbstractComponentContributionHandlerServiceFactoryPropertiesFile(
            ContributionItemResourceType contributionItemResourceType,
            ContributionHandlingStrategy extensionHandlingStrategy) {
        super(contributionItemResourceType, extensionHandlingStrategy);
    }

    @Override
    public ContributionItem createContributionItemFromResourceFile(
            ContributorBundle contributorBundleTrackerObject,
            URL resourceFileURL) {
        return this.createContributionItemFromResourceFile(
                contributorBundleTrackerObject, resourceFileURL, null);
    }

    @Override
    public ContributionItem createContributionItemFromResourceFile(
            ContributorBundle contributorBundleTrackerObject,
            URL resourceFileURL, Map<String, Object> properties) {
        ContributionItem contributionItem = new ContributionItemDefaultImpl(
                contributorBundleTrackerObject, resourceFileURL);

        contributionItem.propertiesMap().putAll(
                extractPropertiesFromResourceFile(resourceFileURL));
        if (properties != null && !properties.isEmpty()) {
            contributionItem.propertiesMap().putAll(properties);
        }
        return contributionItem;
    }
}
