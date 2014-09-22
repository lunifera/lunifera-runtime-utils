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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.lunifera.runtime.utils.osgi.component.AbstractComponentWithCompendium;
import org.lunifera.runtime.utils.osgi.component.ExceptionComponentLifecycle;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * An general parent abstract class for implementations of the
 * {@link ContributionHandlerService} interface.
 * 
 * @author cvgaviao
 * @since 0.0.1
 */
public abstract class AbstractComponentContributionHandlerServiceFactory extends
        AbstractComponentWithCompendium implements ContributionHandlerService {

    private ContributionItemResourceType contributionItemResourceType;

    private ContributionHandlingStrategy extensionHandlingStrategy;

    /**
     * A default constructor is required by the OSGi Declarative Service.
     */
    public AbstractComponentContributionHandlerServiceFactory() {
    }

    /**
     * Constructor created just to help unit testing. It is not used by OSGi.
     * 
     * @param contributionItemResourceType
     * @param extensionHandlingStrategy
     */
    protected AbstractComponentContributionHandlerServiceFactory(
            ContributionItemResourceType contributionItemResourceType,
            ContributionHandlingStrategy extensionHandlingStrategy) {
        this.extensionHandlingStrategy = extensionHandlingStrategy;
        this.contributionItemResourceType = contributionItemResourceType;
    }

    @Override
    public ContributionItem createContributionItemFromHeaderProperties(
            ContributorBundle contributorBundleInstance, String pid,
            Map<String, Object> properties) {
        ContributionItem item = new ContributionItemDefaultImpl(
                contributorBundleInstance, null, properties);
        return item;
    }

    @Override
    public ContributionItem createContributionItemFromHeaderProperties(
            ContributorBundle contributorBundleTrackerObject, String pid,
            String[] keyValuePairProperties) {
        Map<String, Object> properties = Maps.newHashMap();
        for (int i = 0; i < keyValuePairProperties.length; i++) {
            String[] keyValuePair = keyValuePairProperties[i].split("=");
            properties.put(keyValuePair[0], keyValuePair[1]);
        }
        ContributionItem item = new ContributionItemDefaultImpl(
                contributorBundleTrackerObject, null, properties);
        return item;
    }

    @Override
    public ContributionItem createContributionItemFromResourceFile(
            ContributorBundle contributorBundleTrackerObject, URL resourceURL) {
        ContributionItem item = new ContributionItemDefaultImpl(
                contributorBundleTrackerObject, resourceURL, null);
        return item;
    }

    @Override
    public ContributionItem createContributionItemFromResourceFile(
            ContributorBundle contributorBundleTrackerObject,
            URL resourceFileURL, Map<String, Object> properties) {
        ContributionItem contributionItem = new ContributionItemDefaultImpl(
                contributorBundleTrackerObject, resourceFileURL);
        if (properties != null && !properties.isEmpty()) {
            contributionItem.propertiesMap().putAll(properties);
        }
        return contributionItem;
    }

    @Override
    public List<ContributionItem> createContributionItemsForDiscoveredResources(
            ContributorBundle contributorBundleInstance, String searchArgs) {
        List<ContributionItem> contributionItems = Lists.newArrayList();

        Collection<String> resourcePaths = discoverResourcesInsideBundle(
                contributorBundleInstance.bundleWrapped(), searchArgs);

        for (String foundResource : resourcePaths) {
            URL url = contributorBundleInstance.bundleWrapped().getEntry(
                    foundResource);
            if (url != null) {
                ContributionItem contributionItem = createContributionItemFromResourceFile(
                        contributorBundleInstance, url);
                contributionItems.add(contributionItem);
            } else {
                warn("The previouly found resource '{}' was not found anymore inside the bundle {}. "
                        + "Possible cause is out synch resources inside /target folder.",
                        foundResource, contributorBundleInstance
                                .bundleWrapped().getSymbolicName());
            }
        }
        return null;
    }

    @Override
    public ContributorBundle createContributorBundleInstance(
            Bundle contributorBundle) {
        return new ContributorBundleDefault(contributorBundle);
    }

    /**
     * 
     * 
     * @param contributorBundle
     * @param candidateItemPath
     * @return
     */
    protected Collection<String> discoverResourcesInsideBundle(
            Bundle contributorBundle, String candidateItemPath) {
        BundleWiring bundleWiring = contributorBundle.adapt(BundleWiring.class);

        String dir = candidateItemPath.substring(0,
                candidateItemPath.lastIndexOf('/'));
        if (dir == null || dir.isEmpty()) {
            dir = "/";
        }
        String pattern = candidateItemPath.substring(candidateItemPath
                .lastIndexOf('/') + 1);

        Collection<String> resourcePaths = bundleWiring.listResources(dir,
                pattern, BundleWiring.LISTRESOURCES_RECURSE);

        trace("Resources found: " + resourcePaths);

        return resourcePaths;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.lunifera.runtime.utils.osgi.component.AbstractComponentWithCompendium
     * #activate(org.osgi.service.component.ComponentContext)
     */
    @Override
    public void doBeforeActivateComponent() throws ExceptionComponentLifecycle {

        String contributorManifestHeaderItemTypeStr = (String) getProperties()
                .get(ConstantsExtender.EXTENDER_ATTR_CONTRIBUTION_ITEM_RESOURCE_TYPE);
        String extensionHandlingStrategyStr = (String) getProperties().get(
                ConstantsExtender.EXTENDER_ATTR_CONTRIBUTION_HANDLING_STRATEGY);

        this.extensionHandlingStrategy = ContributionHandlingStrategy
                .fromString(extensionHandlingStrategyStr);
        this.contributionItemResourceType = ContributionItemResourceType
                .fromString(contributorManifestHeaderItemTypeStr);
    }

    /**
     * Open the specified resource as a property file and put its values inside
     * a Map.
     * 
     * @param contributionItemURL
     * @return
     * @throws Exception
     */
    public Map<String, String> extractPropertiesFromResourceFile(
            URL contributionItemURL) throws ExceptionComponentExtenderSetup {
        Map<String, String> map;
        if (contributionItemURL == null) {
            throw new ExceptionComponentExtenderSetup(
                    "Can't process a null URL !");
        }
        // java7 feature
        try (InputStream inputStream = contributionItemURL.openStream()) {
            Properties properties = new Properties();
            properties.load(inputStream);
            map = Maps.fromProperties(properties);
        } catch (IOException e) {
            throw new ExceptionComponentExtenderSetup(
                    "Error reading properties file '"
                            + contributionItemURL.getFile() + "'.", e);
        }
        return map;
    }

    /**
     * @return the extensionHandlingStrategy
     */
    @Override
    public final ContributionHandlingStrategy getContributionHandlingStrategy() {
        return extensionHandlingStrategy;
    }

    /**
     * @return the contributionItemResourceType
     */
    @Override
    public final ContributionItemResourceType getContributionItemResourceType() {
        return contributionItemResourceType;
    }

    @Override
    public String getHandlerName() {
        return getName();
    }

    /**
     * Check whether the path pointing to a resource file contribution is a
     * valid URL.
     * 
     * @param candidateItem
     * @return
     */
    @Override
    public boolean isResourceUrlValid(String contributionItemPath) {
        URL url = null;

        try {
            url = new URL(contributionItemPath);
        } catch (MalformedURLException e) {
            return false;
        }

        try {
            url.toURI();
        } catch (URISyntaxException e) {
            return false;
        }
        return true;
    }

    @Override
    public void removeContributionItems(
            ContributorBundle contributorBundleTrackerObject) {
        // Do nothing...

    }

    protected Bundle searchForContributorBundle(String bundleSymbolicName,
            String bundleVersion) {
        Bundle[] bundles = getBundleContext().getBundles();
        for (Bundle bundle : bundles) {
            if (bundle.getSymbolicName().equals(bundleSymbolicName)
                    && bundle.getVersion().toString().equals(bundleVersion)) {
                return bundle;
            }
        }
        return null;
    }
}
