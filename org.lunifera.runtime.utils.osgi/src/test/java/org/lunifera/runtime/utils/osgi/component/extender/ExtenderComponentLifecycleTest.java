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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;
import java.net.URL;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lunifera.runtime.utils.osgi.component.AbstractComponentBasic;
import org.lunifera.runtime.utils.osgi.services.PluggableEventTracker;
import org.lunifera.runtime.utils.osgi.services.PluggableServiceTracker;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Filter;
import org.osgi.framework.Version;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.prefs.PreferencesService;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author cvgaviao
 * @since 0.1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class ExtenderComponentLifecycleTest {

    static class ConcreteExtenderComponent extends AbstractComponentExtender {

        @SuppressWarnings("rawtypes")
        public ConcreteExtenderComponent(BundleContext bundleContext,
                ComponentContext componentContext,
                ContributionHandlerService contributionHandlerService,
                Map<Class<?>, PluggableServiceTracker> serviceTrackers,
                Set<PluggableEventTracker> eventTrackers,
                Filter contributionHandlerServiceFilter) {
            super(bundleContext, componentContext, contributionHandlerService,
                    serviceTrackers, eventTrackers);
        }

        protected void myInternalActivate() throws Exception {
            super.activate(getComponentContext());
        }

        protected void myInternalDeactivate() throws Exception {
            super.deactivate(getComponentContext());
        }

        protected Dictionary<String, Object> myGetProperties() {
            return super.getProperties();
        }
    }

    static class ConcreteExtenderComponentWithoutAnnotation extends
            AbstractComponentExtender {

        @SuppressWarnings("rawtypes")
        public ConcreteExtenderComponentWithoutAnnotation(
                BundleContext bundleContext, ComponentContext componentContext,
                ContributionHandlerService contributionHandlerService,
                Map<Class<?>, PluggableServiceTracker> serviceTrackers,
                Set<PluggableEventTracker> eventTrackers,
                Filter contributionHandlerServiceFilter,
                ContributorBundleTracker bundleTracker) {
            super(bundleContext, componentContext, contributionHandlerService,
                    serviceTrackers, eventTrackers);
        }

        protected void myInternalActivate() throws Exception {
            super.activate(getComponentContext());
        }
    }

    @Component(property = { "lunifera.extender.manifest.header.name = " })
    static class ConcreteExtenderComponentWithoutHeader extends
            AbstractComponentExtender {

        public ConcreteExtenderComponentWithoutHeader(
                BundleContext bundleContext,
                ComponentContext componentContext,
                ContributionHandlerService contributionHandlerService,
                @SuppressWarnings("rawtypes") Map<Class<?>, PluggableServiceTracker> serviceTrackers,
                Set<PluggableEventTracker> eventTrackers,
                Filter contributionHandlerServiceFilter) {
            super(bundleContext, componentContext, contributionHandlerService,
                    serviceTrackers, eventTrackers);
        }

        protected void myInternalActivate() throws Exception {
            super.activate(getComponentContext());
        }

        @Override
        protected void defineEventsToBeTracked() {

        }
    }

    @Mock
    static ConfigurationAdmin configurationAdmin;

    @Mock
    static EventAdmin eventAdmin;

    static Logger logger = LoggerFactory
            .getLogger(ExtenderComponentLifecycleTest.class);

    @Mock
    static PreferencesService preferencesService;

    @Mock
    ServiceTracker<ContributionHandlerService, ContributionHandlerService> contributionHandlerServiceTracker;

    @Mock
    Bundle bundleExtender;

    @Mock
    Bundle bundleExtendee;

    @Mock
    BundleContext bundleContextExtender;

    @Mock
    BundleContext bundleContextExtendee;

    @Mock
    ComponentContext componentContextExtender;

    @Mock
    ComponentContext componentContextExtendee;

    @Mock
    ContributionHandlerService contributionHandlerService;

    @Mock
    ContributorBundle contributorBundleInstance;

    @Mock
    Filter contributionHandlerServiceFilter;

    @Mock
    ContributionItem contributionItem1;

    @Mock
    PluggableServiceTracker<ContributionHandlerService> serviceTrackerContributionHandlerService;

    @SuppressWarnings("rawtypes")
    Map<Class<?>, PluggableServiceTracker> serviceTrackers = new HashMap<>();

    Set<PluggableEventTracker> eventTrackers = new HashSet<>();

    @SuppressWarnings("unchecked")
    @Before
    public void before() throws ExceptionComponentExtenderSetup {

        serviceTrackers.clear();

        eventTrackers.clear();

        when(serviceTrackerContributionHandlerService.getService()).thenReturn(
                contributionHandlerService);
        serviceTrackers
                .put(Logger.class,
                        (PluggableServiceTracker<Logger>) mock(PluggableServiceTracker.class));
        serviceTrackers
                .put(PreferencesService.class,
                        (PluggableServiceTracker<PreferencesService>) mock(PluggableServiceTracker.class));
        serviceTrackers
                .put(EventAdmin.class,
                        (PluggableServiceTracker<EventAdmin>) mock(PluggableServiceTracker.class));

        when(contributorBundleInstance.bundleWrapped()).thenReturn(
                bundleExtendee);
        when(
                contributionHandlerService
                        .createContributionItemFromResourceFile(
                                any(ContributorBundle.class), any(URL.class)))
                .thenReturn(contributionItem1);
        when(
                contributionHandlerService
                        .createContributorBundleInstance(bundleExtendee))
                .thenReturn(contributorBundleInstance);
        when(componentContextExtender.getBundleContext()).thenReturn(
                bundleContextExtender);
        when(componentContextExtender.getProperties()).thenReturn(
                getMockedPropertiesForExtender());
        when(bundleContextExtender.getBundle()).thenReturn(bundleExtender);
        when(bundleExtender.getBundleContext()).thenReturn(
                bundleContextExtender);
        when(bundleExtender.getSymbolicName()).thenReturn(
                "bundle.test.extender");
        when(bundleExtender.getVersion()).thenReturn(new Version("1.0.0"));

        when(componentContextExtendee.getBundleContext()).thenReturn(
                bundleContextExtendee);
        when(componentContextExtendee.getProperties()).thenReturn(
                getMockedPropertiesForExtendee());
        when(bundleContextExtendee.getBundle()).thenReturn(bundleExtendee);
        when(bundleExtendee.getBundleContext()).thenReturn(
                bundleContextExtendee);
        when(bundleExtendee.getSymbolicName()).thenReturn(
                "bundle.test.extendee");
        when(bundleExtendee.getVersion()).thenReturn(new Version("1.0.0"));
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void ensureContributorTrackerIsAddingContributorBundle()
            throws Exception {
        BundleEvent event = mock(BundleEvent.class);
        ContributionHandlerService contributionHandlerService = mock(ContributionHandlerService.class);
        ContributionItem contributionItemResource1 = mock(ContributionItem.class);
        ContributionItem contributionItemResource2 = mock(ContributionItem.class);

        when(contributionHandlerService.getHandlerName()).thenReturn(
                "ContribuitionHandler");
        when(contributionHandlerService.isResourceUrlValid("resource1"))
                .thenReturn(false);
        when(contributionHandlerService.isResourceUrlValid("resource2"))
                .thenReturn(false);
        when(event.getBundle()).thenReturn(bundleExtendee);
        when(bundleExtendee.getHeaders()).thenReturn(getMockHeader());
        when(contributionHandlerService.getContributionHandlingStrategy())
                .thenReturn(ContributionHandlingStrategy.PER_ITEM);
        when(
                contributionHandlerService
                        .createContributorBundleInstance(bundleExtendee))
                .thenReturn(contributorBundleInstance);
        when(
                contributionHandlerService
                        .createContributionItemsForDiscoveredResources(
                                contributorBundleInstance, "resource1"))
                .thenReturn(Arrays.asList(contributionItemResource1));
        when(
                contributionHandlerService
                        .createContributionItemsForDiscoveredResources(
                                contributorBundleInstance, "resource2"))
                .thenReturn(Arrays.asList(contributionItemResource2));
        when(contributionHandlerService.getContributionItemResourceType())
                .thenReturn(
                        ContributionItemResourceType.PROPERTIES_FILE_RESOURCE);
        ConcreteExtenderComponent concreteExtenderComponent = new ConcreteExtenderComponent(
                bundleContextExtender, componentContextExtender,
                contributionHandlerService, serviceTrackers, eventTrackers,
                contributionHandlerServiceFilter);

        assertThat(concreteExtenderComponent.getContributorBundleTracker(),
                notNullValue());

        // simulating DS activating the extender bundle
        concreteExtenderComponent.myInternalActivate();

        assertThat(bundleExtendee.getSymbolicName(), is("bundle.test.extendee"));
        assertThat(bundleExtendee.getVersion().toString(), is("1.0.0"));

        assertThat(concreteExtenderComponent.getContributionHandlerService(),
                notNullValue());
        assertThat(concreteExtenderComponent.getContributionHandlerService()
                .getContributionHandlingStrategy(),
                equalTo(ContributionHandlingStrategy.PER_ITEM));
        assertThat(concreteExtenderComponent.getContributionHandlerService()
                .getContributionItemResourceType(),
                equalTo(ContributionItemResourceType.PROPERTIES_FILE_RESOURCE));

        // simulating a container identifying a bundle being added
        ContributorBundle contributorBundle = concreteExtenderComponent
                .getContributorBundleTracker().addingBundle(bundleExtendee,
                        event);
        assertThat(contributorBundle, notNullValue());

        verify(contributionHandlerService).createContributorBundleInstance(
                any(Bundle.class));
        verify(contributionHandlerService, times(2)).isResourceUrlValid(
                any(String.class));
        verify(contributionHandlerService, times(2))
                .createContributionItemsForDiscoveredResources(
                        any(ContributorBundle.class), any(String.class));
    }

    @Test
    public void ensureContributorTrackerIsRefusingContributorBundleWithoutHeaderItems()
            throws Exception {
        BundleEvent event = mock(BundleEvent.class);
        when(event.getBundle()).thenReturn(bundleExtendee);
        when(bundleExtendee.getHeaders()).thenReturn(getEmptyMockHeader());
        ContributionHandlerService contributionHandlerService = mock(ContributionHandlerService.class);
        ConcreteExtenderComponent concreteExtenderComponent = new ConcreteExtenderComponent(
                bundleContextExtender, componentContextExtender,
                contributionHandlerService, serviceTrackers, eventTrackers,
                contributionHandlerServiceFilter);

        // simulating a container activating the extender bundle
        concreteExtenderComponent.myInternalActivate();

        // simulating a container identifying a bundle being added
        ContributorBundleTracker bundleTracker = concreteExtenderComponent
                .getContributorBundleTracker();
        ContributorBundle contributorBundleTrackerObject = bundleTracker
                .addingBundle(bundleExtendee, event);

        assertThat(contributorBundleTrackerObject, nullValue());
    }

    @Test
    public void ensureFailureForContributorBundleWithoutManifestHeader()
            throws Exception {
        ContributionHandlerService contributionHandlerService = mock(ContributionHandlerService.class);
        BundleEvent event = mock(BundleEvent.class);
        when(bundleExtendee.getHeaders()).thenReturn(getEmptyMockHeader());
        ConcreteExtenderComponentWithoutHeader concreteExtenderComponent = new ConcreteExtenderComponentWithoutHeader(
                bundleContextExtender, componentContextExtender,
                contributionHandlerService, serviceTrackers, eventTrackers,
                contributionHandlerServiceFilter);

        concreteExtenderComponent.myInternalActivate();

        ContributorBundle result = concreteExtenderComponent
                .getContributorBundleTracker().addingBundle(bundleExtendee,
                        event);
        assertThat(result, nullValue());
    }

    @Test(expected = ExceptionComponentExtenderSetup.class)
    public void ensureFailureForContributorWithoutServiceProperties()
            throws Exception {
        ContributionHandlerService contributionHandlerService = mock(ContributionHandlerService.class);
        when(componentContextExtender.getProperties()).thenReturn(
                getMockFailProperties());
        ConcreteExtenderComponentWithoutAnnotation concreteExtenderComponent = new ConcreteExtenderComponentWithoutAnnotation(
                bundleContextExtender, componentContextExtender,
                contributionHandlerService, serviceTrackers, eventTrackers,
                contributionHandlerServiceFilter, null);

        try {
            // call a method that wraps the internalActivate method.
            concreteExtenderComponent.myInternalActivate();
        } catch (Exception e) {
            assertThat(
                    e.getMessage(),
                    equalTo("The manifest header name service property must be set for the extender component."));
            throw e;
        }

    }

    private Dictionary<String, String> getEmptyMockHeader() {
        Dictionary<String, String> dict = new Hashtable<>();
        return dict;
    }

    private Dictionary<String, String> getMockHeader() {
        Dictionary<String, String> dict = new Hashtable<>();
        dict.put("MyHeader", "resource1,resource2");
        return dict;
    }

    private Dictionary<String, Object> getMockedPropertiesForExtendee() {
        Dictionary<String, Object> dict = new Hashtable<>();
        dict.put(ComponentConstants.COMPONENT_ID, 333333l);
        dict.put(ComponentConstants.COMPONENT_NAME, "Mocked Extendee Component");
        dict.put(AbstractComponentBasic.COMPONENT_DESCRIPTION,
                "Mocked Extendee Component Description");
        return dict;
    }

    private Dictionary<String, Object> getMockedPropertiesForExtender() {
        Dictionary<String, Object> dict = new Hashtable<>();
        dict.put(ComponentConstants.COMPONENT_ID, 121212l);
        dict.put(ComponentConstants.COMPONENT_NAME, "Mocked Extender Component");
        dict.put(ConstantsExtender.EXTENDER_ATTR_MANIFEST_HEADER_NAME,
                "MyHeader");
        dict.put(AbstractComponentBasic.COMPONENT_DESCRIPTION,
                "Mocked Component Description");
        dict.put(ConstantsExtender.EXTENDER_ATTR_BUNDLE_STATE_MASK,
                Bundle.ACTIVE);
        return dict;
    }

    private Dictionary<String, Object> getMockFailProperties() {
        Dictionary<String, Object> dict = new Hashtable<>();
        dict.put(ComponentConstants.COMPONENT_ID, 22222l);
        dict.put(ComponentConstants.COMPONENT_NAME, "Fail Mocked Component");
        return dict;
    }
}
