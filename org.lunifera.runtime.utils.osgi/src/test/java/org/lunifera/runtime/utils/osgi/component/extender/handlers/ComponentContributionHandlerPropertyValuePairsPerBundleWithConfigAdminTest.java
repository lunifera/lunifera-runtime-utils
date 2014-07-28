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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Dictionary;

import org.junit.Before;
import org.lunifera.runtime.utils.osgi.component.extender.ContributionItemResourceType;
import org.lunifera.runtime.utils.osgi.component.extender.ContributorBundleTrackerObject;
import org.lunifera.runtime.utils.osgi.component.extender.ContributionHandlingStrategy;
import org.lunifera.runtime.utils.osgi.component.extender.handlers.AbstractComponentFactoryContributionHandlerPropertiesFilePerItem;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

//@RunWith(MockitoJUnitRunner.class)
public class ComponentContributionHandlerPropertyValuePairsPerBundleWithConfigAdminTest {

    static class ConcreteHandler extends
            AbstractComponentFactoryContributionHandlerPropertiesFilePerItem {

        public ConcreteHandler() {
            super();
        }

        public ConcreteHandler(
                ContributionItemResourceType contributionItemResourceType,
                ContributionHandlingStrategy extensionHandlingStrategy) {
            super(contributionItemResourceType, extensionHandlingStrategy);
        }

    }

    ConcreteHandler handler;

    @Before
    public void setup() {
        handler = new ConcreteHandler();
    }

    @SuppressWarnings("unchecked")
    // @Test
    public void testOnContributorBundleAddition_PROPERTY_VALUE_PAIR_PER_BUNDLE()
            throws Exception {
        Bundle mockBundle = mock(Bundle.class);
        ContributorBundleTrackerObject contributorBundleTrackerObject;
        ConcreteHandler spyHandler = spy(new ConcreteHandler(
                ContributionItemResourceType.PROPERTY_VALUE_PAIRS,
                ContributionHandlingStrategy.PER_BUNDLE));

        when(mockBundle.getHeaders()).thenReturn(mock(Dictionary.class));
        when(mockBundle.getHeaders().get("My_Header")).thenReturn(
                "key0=value0,key1=value1,key2=value2");
        when(mockBundle.getSymbolicName()).thenReturn("my.bundle");
        when(mockBundle.getVersion()).thenReturn(new Version("1.0.0"));

        contributorBundleTrackerObject = spyHandler
                .whenContributorBundleActivated(mockBundle, "My_Header",
                        "pro1=value1");

        assertNotNull(contributorBundleTrackerObject);
        assertThat(contributorBundleTrackerObject.getContributionItemList(),
                hasSize(1));
    }
}
