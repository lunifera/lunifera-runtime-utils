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
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.lunifera.runtime.utils.osgi.component.extender.AbstractComponentExtender.ContributorBundleTracker;
import org.lunifera.runtime.utils.osgi.services.PluggableEventTracker;
import org.lunifera.runtime.utils.osgi.services.PluggableServiceTracker;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Filter;
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
                Map<Class<?>, PluggableServiceTracker> serviceTrackers,
                Set<PluggableEventTracker> eventTrackers,
                Filter contributionHandlerServiceFilter) {
            super(bundleContext, componentContext, serviceTrackers,
                    eventTrackers);
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
                Map<Class<?>, PluggableServiceTracker> serviceTrackers,
                Set<PluggableEventTracker> eventTrackers,
                Filter contributionHandlerServiceFilter,
                ContributorBundleTracker bundleTracker) {
            super(bundleContext, componentContext, serviceTrackers,
                    eventTrackers);
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
                @SuppressWarnings("rawtypes") Map<Class<?>, PluggableServiceTracker> serviceTrackers,
                Set<PluggableEventTracker> eventTrackers,
                Filter contributionHandlerServiceFilter) {
            super(bundleContext, componentContext, serviceTrackers,
                    eventTrackers);
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
    BundleContext bundleContext;

    @Mock
    ContributionHandlerService contributionHandlerService;

    @Mock
    ContributorBundleTrackerObject contributorBundleTrackerObject;

    @Mock
    Filter contributionHandlerServiceFilter;

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

        when(
                contributionHandlerService.whenContributorBundleActivated(
                        any(Bundle.class), eq("MyHeader"),
                        eq("resource1,resource2"))).thenReturn(
                contributorBundleTrackerObject);
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void ensureContributorTrackerIsAddingContributorBundle()
            throws Exception {
        ComponentContext componentContext = mock(ComponentContext.class);
        when(componentContext.getProperties()).thenReturn(getMockProperties());
        Bundle contributorBundle = mock(Bundle.class);
        BundleEvent event = mock(BundleEvent.class);
        when(contributorBundle.getHeaders()).thenReturn(getEmptyMockHeader());

        ContributorBundleTrackerObject contributorBundleTrackerObject = mock(ContributorBundleTrackerObject.class);

        ContributionHandlerService contributionHandlerService = mock(ContributionHandlerService.class);
        when(event.getBundle()).thenReturn(contributorBundle);
        when(contributorBundle.getBundleContext()).thenReturn(bundleContext);
        when(contributorBundle.getHeaders()).thenReturn(getMockHeader());
        when(contributorBundle.getSymbolicName()).thenReturn("bundle.test");
        when(contributionHandlerService.getContributionHandlingStrategy())
                .thenReturn(ContributionHandlingStrategy.PER_ITEM);
        when(
                contributionHandlerService
                        .whenContributorBundleActivated(any(Bundle.class),
                                any(String.class), any(String.class)))
                .thenReturn(contributorBundleTrackerObject);
        when(contributionHandlerService.getContributionItemResourceType())
                .thenReturn(
                        ContributionItemResourceType.PROPERTIES_FILE_RESOURCE);
        ConcreteExtenderComponent concreteExtenderComponent = new ConcreteExtenderComponent(
                bundleContext, componentContext, serviceTrackers,
                eventTrackers, contributionHandlerServiceFilter);
        concreteExtenderComponent
                .bindContributionHandlerService(contributionHandlerService);

        assertThat(concreteExtenderComponent.getContributorBundleTracker(),
                notNullValue());

        // simulating a container activating the extender bundle
        concreteExtenderComponent.myInternalActivate();

        assertThat(concreteExtenderComponent.getContributorBundleTracker(),
                notNullValue());
        assertThat(concreteExtenderComponent.getContributionHandlerService(),
                notNullValue());
        assertThat(concreteExtenderComponent.getContributionHandlerService()
                .getContributionHandlingStrategy(),
                equalTo(ContributionHandlingStrategy.PER_ITEM));
        assertThat(concreteExtenderComponent.getContributionHandlerService()
                .getContributionItemResourceType(),
                equalTo(ContributionItemResourceType.PROPERTIES_FILE_RESOURCE));
        // simulating a container identifying a bundle being added
        ContributorBundleTrackerObject contributorBundleTrackerObjectR = concreteExtenderComponent
                .getContributorBundleTracker().addingBundle(contributorBundle,
                        event);

        verify(contributionHandlerService).whenContributorBundleActivated(
                any(Bundle.class), any(String.class), any(String.class));

        assertThat(contributorBundleTrackerObjectR, notNullValue());
    }

    @Test
    public void ensureContributorTrackerIsRefusingContributorBundleWithoutHeaderItems()
            throws Exception {
        ComponentContext componentContext = mock(ComponentContext.class);
        when(componentContext.getProperties()).thenReturn(getMockProperties());
        Bundle contributorBundle = mock(Bundle.class);
        BundleEvent event = mock(BundleEvent.class);
        when(event.getBundle()).thenReturn(contributorBundle);
        when(contributorBundle.getBundleContext()).thenReturn(bundleContext);
        when(contributorBundle.getHeaders()).thenReturn(getEmptyMockHeader());
        when(contributorBundle.getSymbolicName()).thenReturn("bundle.test");

        ConcreteExtenderComponent concreteExtenderComponent = new ConcreteExtenderComponent(
                bundleContext, componentContext, serviceTrackers,
                eventTrackers, contributionHandlerServiceFilter);

        // simulating a container activating the extender bundle
        concreteExtenderComponent.myInternalActivate();

        // simulating a container identifying a bundle being added
        ContributorBundleTracker bundleTracker = concreteExtenderComponent
                .getContributorBundleTracker();
        ContributorBundleTrackerObject contributorBundleTrackerObject = bundleTracker
                .addingBundle(contributorBundle, event);

        assertThat(contributorBundleTrackerObject, nullValue());
    }

    @Test
    public void ensureFailureForContributorBundleWithoutManifestHeader()
            throws Exception {
        ComponentContext componentContext = mock(ComponentContext.class);
        when(componentContext.getProperties()).thenReturn(getMockProperties());
        Bundle contributorBundle = mock(Bundle.class);
        BundleEvent event = mock(BundleEvent.class);
        when(contributorBundle.getHeaders()).thenReturn(getEmptyMockHeader());
        ConcreteExtenderComponentWithoutHeader concreteExtenderComponent = new ConcreteExtenderComponentWithoutHeader(
                bundleContext, componentContext, serviceTrackers,
                eventTrackers, contributionHandlerServiceFilter);

        concreteExtenderComponent.myInternalActivate();

        ContributorBundleTrackerObject result = concreteExtenderComponent
                .getContributorBundleTracker().addingBundle(contributorBundle,
                        event);
        assertThat(result, nullValue());
    }

    @Test(expected = ExceptionComponentExtenderSetup.class)
    public void ensureFailureForContributorWithoutServiceProperties()
            throws Exception {
        ComponentContext componentContext = mock(ComponentContext.class);
        when(componentContext.getProperties()).thenReturn(
                getMockFailProperties());
        ConcreteExtenderComponentWithoutAnnotation concreteExtenderComponent = new ConcreteExtenderComponentWithoutAnnotation(
                bundleContext, componentContext, serviceTrackers,
                eventTrackers, contributionHandlerServiceFilter, null);

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

    private Dictionary<String, Object> getMockProperties() {
        Dictionary<String, Object> dict = new Hashtable<>();
        dict.put(ComponentConstants.COMPONENT_ID, 121212l);
        dict.put(ComponentConstants.COMPONENT_NAME, "Mocked Component");
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
