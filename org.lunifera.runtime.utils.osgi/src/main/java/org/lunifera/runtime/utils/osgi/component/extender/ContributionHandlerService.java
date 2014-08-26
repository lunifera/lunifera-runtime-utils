package org.lunifera.runtime.utils.osgi.component.extender;

import java.net.URL;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;

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

/**
 * A ContributionHandlerService is responsible for processing the contribution
 * resources coming from ContributorBundle instances.
 * <p>
 * An extender component will bind to one registered ContributionHandlerService
 * instance and will delegate to it the resource processing job whenever some
 * bundle matching the extender manifest header is added, modified or removed
 * from the container.
 * <p>
 * A contributor bundle represents the <i>'extendee'</i> bundle that is
 * contributing something (using resource files) to other <i>'extendible'</i>
 * bundle through the <i>'extender'</i> component.
 * 
 * @author Cristiano Gavião
 * @since 0.0.1
 */
public interface ContributionHandlerService {

    String EXTENSION_STRATEGY = "lunifera.extension.strategy";

    String MANIFEST_HEADER_ITEM_TYPE = "lunifera.extender.contribution.item.resource.type";

    /**
     * 
     * @param contributorBundleInstance
     * @param pid
     * @param properties
     * @return
     */
    ContributionItem createContributionItemFromHeaderProperties(
            ContributorBundle contributorBundleInstance, String pid,
            Map<String, Object> properties);

    /**
     * 
     * @param contributorBundleInstance
     * @param resourceFileURL
     * @param properties
     * @return
     */
    ContributionItem createContributionItemFromHeaderProperties(
            ContributorBundle contributorBundleInstance, String pid,
            String[] keyValuePairProperties);

    /**
     * The extender delegates to an implementation of
     * {@link ContributionHandlerService} the task to create a
     * {@link ContributionItem} in accordance with it own rules.
     * 
     * @param contributorBundleTrackerObject
     * @param resourceFileURL
     * @return
     */
    ContributionItem createContributionItemFromResourceFile(
            ContributorBundle contributorBundleTrackerObject,
            URL resourceFileURL);

    /**
     * The extender delegates to an implementation of
     * {@link ContributionHandlerService} the task to create a
     * {@link ContributionItem} from a resource file being contributed by a
     * {@link ContributorBundle}.
     * 
     * @param contributorBundleTrackerObject
     * @param resourceFileURL
     * @param properties
     * @return
     */
    ContributionItem createContributionItemFromResourceFile(
            ContributorBundle contributorBundleTrackerObject,
            URL resourceFileURL, Map<String, Object> properties);

    /**
     * 
     * 
     * @param contributorBundleInstance
     * @param searchArgs
     * @return
     */
    List<ContributionItem> createContributionItemsForDiscoveredResources(
            ContributorBundle contributorBundleInstance, String searchArgs);

    /**
     * Creates a {@link ContributorBundle} instance. 
     * 
     * @param contributorBundle
     * @return
     */
    ContributorBundle createContributorBundleInstance(Bundle contributorBundle);

    /**
     * 
     * @param contributionItemURL
     * @return
     * @throws ExceptionComponentExtenderSetup
     */
    Map<String, String> extractPropertiesFromResourceFile(
            URL contributionItemURL) throws ExceptionComponentExtenderSetup;

    /**
     * An extender implementation can process its contributions resources using
     * different strategies.
     * <p>
     * The most common ways can be selected in
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
     * 
     * @param contributionItemPath
     * @return
     */
    boolean isResourceUrlValid(String contributionItemPath);

    /**
     * Used to ensure that everything done when the {@link ContributionItem} was
     * create are properly undone when its host contributor bundle is removed
     * from the container.
     * 
     * @param contributorBundleTrackerObject
     */
    void removeContributionItems(
            ContributorBundle contributorBundleTrackerObject);
}
