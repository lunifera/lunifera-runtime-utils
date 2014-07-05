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

import org.junit.Before;
import org.lunifera.runtime.utils.osgi.component.extender.AbstractComponentFactoryContributionHandlerPropertiesFileResourcePerItem;
import org.lunifera.runtime.utils.osgi.component.extender.ContributionItemResourceType;
import org.lunifera.runtime.utils.osgi.component.extender.ExtensionHandlingStrategy;

//@RunWith(MockitoJUnitRunner.class)
public class ComponentContributionHandlerPropertiesFileResourcePerCategoryWithConfigAdminTest {

    static class ConcreteHandler extends
            AbstractComponentFactoryContributionHandlerPropertiesFileResourcePerItem {

        public ConcreteHandler() {
            super();
        }

        public ConcreteHandler(
                ContributionItemResourceType contributionItemResourceType,
                ExtensionHandlingStrategy extensionHandlingStrategy) {
            super(contributionItemResourceType, extensionHandlingStrategy);
        }

    }

    ConcreteHandler handler;

    @Before
    public void setup() {
        handler = new ConcreteHandler();
    }

 }
