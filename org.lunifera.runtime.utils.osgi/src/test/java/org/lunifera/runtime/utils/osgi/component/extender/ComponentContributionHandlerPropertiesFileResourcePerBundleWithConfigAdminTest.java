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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.Dictionary;
import java.util.List;

import org.junit.Before;
import org.lunifera.runtime.utils.osgi.component.extender.AbstractComponentFactoryContributionHandlerPropertiesFileResourcePerItem;
import org.lunifera.runtime.utils.osgi.component.extender.ContributionItemResourceType;
import org.lunifera.runtime.utils.osgi.component.extender.ContributorBundleTrackerObject;
import org.lunifera.runtime.utils.osgi.component.extender.ExtensionHandlingStrategy;
import org.lunifera.runtime.utils.osgi.component.extender.ContributorBundleTrackerObject.ContributionItem;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import com.google.common.collect.Lists;

//@RunWith(MockitoJUnitRunner.class)
public class ComponentContributionHandlerPropertiesFileResourcePerBundleWithConfigAdminTest {

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

    @SuppressWarnings("unchecked")
    //@Test
    public void testOnContributorBundleAddition_PROPERTIES_FILE_RESOURCE_PER_BUNDLE()
            throws Exception {
        Bundle mockBundle = mock(Bundle.class);
        ContributorBundleTrackerObject contributorBundleTrackerObject;
        ConcreteHandler spyHandler = spy(new ConcreteHandler(
                ContributionItemResourceType.PROPERTIES_FILE_RESOURCE,
                ExtensionHandlingStrategy.PER_BUNDLE));

        URL url = this.getClass().getResource("/test.config");
        when(mockBundle.getHeaders()).thenReturn(mock(Dictionary.class));
        when(mockBundle.getHeaders().get("My_Header")).thenReturn(
                url.toString() + ",test.config");
        when(mockBundle.getSymbolicName()).thenReturn("my.bundle");
        when(mockBundle.getVersion()).thenReturn(new Version("1.0.0"));
        doReturn(url).when(mockBundle).getEntry("test.config");

        List<String> foundInBundle = Lists.newArrayList("test.config");
        doReturn(foundInBundle).when(spyHandler).discoverResourcesInBundle(
                mockBundle, "test.config");
        contributorBundleTrackerObject = spyHandler
                .onContributorBundleAddition(mockBundle, "My_Header",
                        "pro1=value1");

        assertNotNull(contributorBundleTrackerObject);
        assertThat(contributorBundleTrackerObject.getContributionItemList(),
                hasSize(1));
        ContributionItem item = contributorBundleTrackerObject
                .getContributionItemList().get(0);
        assertThat(item.getPropertiesMap().keySet(), hasSize(4));
    }

}
