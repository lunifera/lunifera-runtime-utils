package org.lunifera.runtime.utils.osgi.component;

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

import org.lunifera.runtime.utils.osgi.component.extender.ContributionItemResourceType;
import org.lunifera.runtime.utils.osgi.component.extender.ContributionHandlingStrategy;
import org.lunifera.runtime.utils.osgi.component.extender.handlers.AbstractComponentFactoryContributionHandlerPropertiesFile;

//import com.google.common.base.Splitter;

/**
 *
 * @author Cristiano Gavião
 * @since 0.0.1
 */
public abstract class AbstractComponentFactoryContributionHandlerPropertiesFileResourcePerCategory
        extends
        AbstractComponentFactoryContributionHandlerPropertiesFile {

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
            ContributionHandlingStrategy extensionHandlingStrategy) {
        super(contributionItemResourceType, extensionHandlingStrategy);
    }

}
