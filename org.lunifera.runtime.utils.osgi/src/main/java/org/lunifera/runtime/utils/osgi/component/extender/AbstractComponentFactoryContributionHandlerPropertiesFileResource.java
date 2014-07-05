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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;

/**
 * @author Cristiano Gavião
 * @since 0.0.1
 */
public abstract class AbstractComponentFactoryContributionHandlerPropertiesFileResource
        extends AbstractComponentFactoryContributionHandler {

    /**
     * A default constructor is required by the OSGi Declarative Service.
     */
    public AbstractComponentFactoryContributionHandlerPropertiesFileResource() {
        super();
    }

    /**
     * Constructor created just to help unit testing. It is not used by OSGi.
     * 
     * @param contributionItemResourceType
     * @param extensionHandlingStrategy
     */
    protected AbstractComponentFactoryContributionHandlerPropertiesFileResource(
            ContributionItemResourceType contributionItemResourceType,
            ExtensionHandlingStrategy extensionHandlingStrategy) {
        super(contributionItemResourceType, extensionHandlingStrategy);
    }

    /**
     * 
     * 
     * @param header
     * @return
     */
    protected Map<String, String> createPropertiesMapFromHeaderValues(
            String header) {
        Map<String, String> propertiesMap = Splitter.on(",").trimResults()
                .omitEmptyStrings().withKeyValueSeparator("=").split(header);

        return propertiesMap;
    }

    /**
     * 
     * 
     * @param contributorBundle
     * @param candidateItemPath
     * @return
     */
    protected Collection<String> discoverResourcesInBundle(
            Bundle contributorBundle, String candidateItemPath) {
        BundleWiring bundleWiring = contributorBundle.adapt(BundleWiring.class);
        
        String dir = candidateItemPath.substring(0, candidateItemPath.lastIndexOf('/'));
        if (dir == null || dir.isEmpty()){
            dir = "/";
        }
        String pattern = candidateItemPath.substring(candidateItemPath.lastIndexOf('/')+1);
                
        Collection<String> resourcePaths = bundleWiring.listResources(dir,
                pattern, BundleWiring.LISTRESOURCES_RECURSE);
        
        trace("Resources that was found: " + resourcePaths);

        return resourcePaths;
    }




    /**
     * 
     * 
     * @param contributionItemURL
     * @return
     * @throws Exception
     */
    protected Map<String, String> extractMapFromPropertiesResourceFile(
            URL contributionItemURL) throws ExceptionComponentExtenderSetup {
        Map<String, String> map;
        if (contributionItemURL == null) {
            throw new ExceptionComponentExtenderSetup(
                    "Can't process a null URL !");
        }
        InputStream inputStream;
        try {
            inputStream = contributionItemURL.openStream();
        } catch (IOException e) {
            throw new ExceptionComponentExtenderSetup(
                    "Error reading properties file '"
                            + contributionItemURL.getFile() + "'.", e);
        }
        // Read properties file.
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
            map = Maps.fromProperties(properties);

        } catch (IOException e) {
            throw new ExceptionComponentExtenderSetup(e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new ExceptionComponentExtenderSetup(e);
            }
        }
        return map;
    }

    /**
     * Check whether the URL pointing to a file contribution is a valid one.
     * 
     * @param candidateItem
     * @return
     */
    protected boolean isValidForeignURL(String candidateItem) {
        URL u = null;

        try {
            u = new URL(candidateItem);
        } catch (MalformedURLException e) {
            return false;
        }

        try {
            u.toURI();
        } catch (URISyntaxException e) {
            return false;
        }
        return true;
    }
}
