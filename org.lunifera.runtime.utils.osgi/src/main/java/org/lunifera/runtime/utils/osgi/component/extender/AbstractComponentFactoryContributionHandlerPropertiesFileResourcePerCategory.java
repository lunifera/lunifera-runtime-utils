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

//import com.google.common.base.Splitter;

/**
 *
 * @author Cristiano Gavião
 * @since 0.0.1
 */
public class AbstractComponentFactoryContributionHandlerPropertiesFileResourcePerCategory
        extends AbstractComponentFactoryContributionHandlerPropertiesFileResource {

    /**
     * A default constructor is required by the OSGi Declarative Service.
     */
    public AbstractComponentFactoryContributionHandlerPropertiesFileResourcePerCategory() {
    }

    /**
     * Constructor created just to help unit testing. It is not used by OSGi.
     * 
     * @param contributionItemResourceType
     * @param extensionHandlingStrategy
     */
    protected AbstractComponentFactoryContributionHandlerPropertiesFileResourcePerCategory(
            ContributionItemResourceType contributionItemResourceType,
            ExtensionHandlingStrategy extensionHandlingStrategy) {
        super(contributionItemResourceType, extensionHandlingStrategy);
    }

    /* (non-Javadoc)
     * @see org.lunifera.runtime.utils.osgi.component.extender.ContributionHandlerService#onContributorBundleAddition(org.osgi.framework.Bundle, java.lang.String, java.lang.String)
     */
    @Override
    public ContributorBundleTrackerObject onContributorBundleAddition(
            Bundle contributorBundle, String headerName, String headerValue)
            throws ExceptionComponentExtenderSetup {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.lunifera.runtime.utils.osgi.component.extender.ContributionHandlerService#onContributorBundleModified(org.lunifera.runtime.utils.osgi.component.extender.ContributorBundleTrackerObject)
     */
    @Override
    public void onContributorBundleModified(
            ContributorBundleTrackerObject contributorBundleTrackerObject)
            throws ExceptionComponentExtenderSetup {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.lunifera.runtime.utils.osgi.component.extender.ContributionHandlerService#onContributorBundleRemoval(org.lunifera.runtime.utils.osgi.component.extender.ContributorBundleTrackerObject)
     */
    @Override
    public void onContributorBundleRemoval(
            ContributorBundleTrackerObject contributorBundleTrackerObject)
            throws ExceptionComponentExtenderSetup {
        // TODO Auto-generated method stub
        
    }


}
