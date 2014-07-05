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
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lunifera.runtime.utils.osgi.component.AbstractComponentBasic;
import org.lunifera.runtime.utils.osgi.component.extender.AbstractComponentExtender;
import org.lunifera.runtime.utils.osgi.component.extender.ComponentExtenderSetup;
import org.lunifera.runtime.utils.osgi.component.extender.ContributionHandlerService;
import org.lunifera.runtime.utils.osgi.component.extender.ContributionItemResourceType;
import org.lunifera.runtime.utils.osgi.component.extender.ContributorBundleTrackerObject;
import org.lunifera.runtime.utils.osgi.component.extender.ExtensionHandlingStrategy;
import org.lunifera.runtime.utils.osgi.component.extender.AbstractComponentExtender.ContributorBundleTracker;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Filter;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.prefs.PreferencesService;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(MockitoJUnitRunner.class)
public class ExtenderComponentLifecycleTest {

    @ComponentExtenderSetup(manifestHeaderName = "MyHeader")
    static class ConcreteExtenderComponent extends AbstractComponentExtender {

        public ConcreteExtenderComponent(
                ComponentContext componentContext,
                ContributionHandlerService contributionHandlerService,
                Filter contributionHandlerServiceFilter,
                ServiceTracker<ContributionHandlerService, ContributionHandlerService> contributionHandlerServiceTracker) {
            super(componentContext, contributionHandlerService,
                    contributionHandlerServiceFilter,
                    contributionHandlerServiceTracker);
        }

        protected void myInternalActivate() throws Exception {
            super.activate(getComponentContext());
        }

        protected void myInternalDeactivate() throws Exception {
            super.doFirstLevelDeactivation();
        }

        protected Dictionary<String, Object> myGetProperties() {
            return super.getProperties();
        }

    }

    static class ConcreteExtenderComponentWithoutAnnotation extends
            AbstractComponentExtender {

        public ConcreteExtenderComponentWithoutAnnotation(
                ComponentContext componentContext,
                ContributionHandlerService contributionHandlerService,
                Filter contributionHandlerServiceFilter,
                ServiceTracker<ContributionHandlerService, ContributionHandlerService> contributionHandlerServiceTracker) {
            super(componentContext, contributionHandlerService,
                    contributionHandlerServiceFilter,
                    contributionHandlerServiceTracker);
        }

        protected void myInternalActivate() throws Exception {
            super.activate(getComponentContext());
        }

    }

    @ComponentExtenderSetup(manifestHeaderName = "")
    static class ConcreteExtenderComponentWithoutHeader extends
            AbstractComponentExtender {

        public ConcreteExtenderComponentWithoutHeader(
                ComponentContext componentContext,
                ContributionHandlerService contributionHandlerService,
                Filter contributionHandlerServiceFilter,
                ServiceTracker<ContributionHandlerService, ContributionHandlerService> contributionHandlerServiceTracker) {
            super(componentContext, contributionHandlerService,
                    contributionHandlerServiceFilter,
                    contributionHandlerServiceTracker);
        }

        protected void myInternalActivate() throws Exception {
            super.activate(getComponentContext());
        }

    }

    @Mock
    static ConfigurationAdmin configurationAdmin;

    @Mock
    static ContributorBundleTracker contributorBundleTracker;

    @Mock
    static EventAdmin eventAdmin;

    static Logger logger = LoggerFactory
            .getLogger(ExtenderComponentLifecycleTest.class);

    @Mock
    static PreferencesService preferencesService;

    @BeforeClass
    public static void classSetup() {
        // compendiumServices = new CompendiumServicesComponent(
        // configurationAdmin, eventAdmin, logger, preferencesService);
        //
    }

    @Mock
    ServiceTracker<ContributionHandlerService, ContributionHandlerService> contributionHandlerServiceTracker;

    @Mock
    BundleContext bundleContext;

    @Mock
    ComponentContext componentContext;

    @Mock
    ContributionHandlerService contributionHandlerService;

    @Mock
    ContributorBundleTrackerObject contributorBundleTrackerObject;

    @Mock
    Filter contributionHandlerServiceFilter;

    @Before
    public void before() {

        when(componentContext.getProperties()).thenReturn(getMockProperties());

        when(componentContext.getBundleContext()).thenReturn(bundleContext);
    }

    /**
     * 
     * @throws Exception
     */
    // @Test
    public void ensureContributorTrackerIsAddingContributorBundle()
            throws Exception {
        Bundle contributorBundle = mock(Bundle.class);
        BundleEvent event = mock(BundleEvent.class);
        when(event.getBundle()).thenReturn(contributorBundle);
        when(contributorBundle.getBundleContext()).thenReturn(bundleContext);
        when(contributorBundle.getHeaders()).thenReturn(getMockHeader());

        ConcreteExtenderComponent concreteExtenderComponent = new ConcreteExtenderComponent(
                componentContext, contributionHandlerService,
                contributionHandlerServiceFilter,
                contributionHandlerServiceTracker);
        // concreteExtenderComponent.mySetComponentContext(componentContext);
        ContributorBundleTracker bundleTracker = new ContributorBundleTracker(
                concreteExtenderComponent, Bundle.ACTIVE);
        concreteExtenderComponent.setContributorBundleTracker(bundleTracker);

        // simulating a container activating the extender bundle
        concreteExtenderComponent.myInternalActivate();

        when(
                contributionHandlerService.onContributorBundleAddition(
                        any(Bundle.class), "My_Header", "pro1=value1"))
                .thenReturn(contributorBundleTrackerObject);

        // simulating a container identifying a bundle being added
        ContributorBundleTrackerObject contributorBundleTrackerObject = bundleTracker
                .addingBundle(contributorBundle, event);

        assertThat(contributorBundleTrackerObject, notNullValue());

    }

    // @Test
    public void ensureContributorTrackerIsRefusingContributorBundleWithoutHeaderItems()
            throws Exception {
        Bundle contributorBundle = mock(Bundle.class);
        BundleEvent event = mock(BundleEvent.class);
        when(event.getBundle()).thenReturn(contributorBundle);
        when(contributorBundle.getBundleContext()).thenReturn(bundleContext);
        when(contributorBundle.getHeaders()).thenReturn(getEmptyMockHeader());

        ConcreteExtenderComponent concreteExtenderComponent = new ConcreteExtenderComponent(
                componentContext, contributionHandlerService,
                contributionHandlerServiceFilter,
                contributionHandlerServiceTracker);
        // concreteExtenderComponent.mySetComponentContext(componentContext);
        ContributorBundleTracker bundleTracker = new ContributorBundleTracker(
                concreteExtenderComponent, Bundle.ACTIVE);
        concreteExtenderComponent.setContributorBundleTracker(bundleTracker);

        // simulating a container activating the extender bundle
        concreteExtenderComponent.myInternalActivate();

        when(
                contributionHandlerService.onContributorBundleAddition(
                        any(Bundle.class), "My_Header", "pro1=value1"))
                .thenReturn(contributorBundleTrackerObject);

        // simulating a container identifying a bundle being added
        ContributorBundleTrackerObject contributorBundleTrackerObject = bundleTracker
                .addingBundle(contributorBundle, event);

        assertThat(contributorBundleTrackerObject, nullValue());

    }

    @Test
    public void ensureExtenderConfigAnnotationIsBeingProcessed()
            throws Exception {
        ConcreteExtenderComponent concreteExtenderComponent = spy(new ConcreteExtenderComponent(
                componentContext, contributionHandlerService,
                contributionHandlerServiceFilter,
                contributionHandlerServiceTracker));
        // when(concreteExtenderComponent.getContributionHandlerServiceFilter())
        // .thenReturn(contributionHandlerServiceFilter);
        when(concreteExtenderComponent.myGetProperties()).thenReturn(
                getMockProperties());
        doCallRealMethod().when(concreteExtenderComponent).myInternalActivate();
        doCallRealMethod().when(concreteExtenderComponent)
                .onActivate();
        concreteExtenderComponent.myInternalActivate();
        assertThat(
                concreteExtenderComponent
                        .getExtenderContributorManifestHeader(),
                equalTo("MyHeader"));
        assertThat(concreteExtenderComponent.getExtensionStrategy(),
                equalTo(ExtensionHandlingStrategy.PER_ITEM));
        assertThat(concreteExtenderComponent.getManifestHeaderItemType(),
                equalTo(ContributionItemResourceType.PROPERTIES_FILE_RESOURCE));
    }

    // @Test
    public void ensureExtensionComponentLifecycleIsOk() throws Exception {

        ConcreteExtenderComponent concreteExtenderComponent = new ConcreteExtenderComponent(
                componentContext, contributionHandlerService,
                contributionHandlerServiceFilter,
                contributionHandlerServiceTracker);
        ContributorBundleTracker bundleTracker = spy(new ContributorBundleTracker(
                concreteExtenderComponent, Bundle.ACTIVE));
        concreteExtenderComponent.setContributorBundleTracker(bundleTracker);
        concreteExtenderComponent.myInternalActivate();

        assertThat(
                concreteExtenderComponent
                        .getExtenderContributorManifestHeader(),
                equalTo("MyHeader"));

        verify(bundleTracker).open();

        // deactivate
        concreteExtenderComponent.myInternalDeactivate();

        assertThat(
                concreteExtenderComponent
                        .getExtenderContributorManifestHeader(),
                nullValue());

        verify(bundleTracker).close();
    }

    // @Test(expected = ExceptionComponentUnrecoveredActivationError.class)
    public void ensureFailureForContributorBundleWithoutManifestHeader()
            throws Exception {
        ConcreteExtenderComponentWithoutHeader concreteExtenderComponent = new ConcreteExtenderComponentWithoutHeader(
                componentContext, contributionHandlerService,
                contributionHandlerServiceFilter,
                contributionHandlerServiceTracker);

        try {
            // call a method that wraps the internalActivate method.
            concreteExtenderComponent.myInternalActivate();
        } catch (Exception e) {
            assertThat(
                    e.getMessage(),
                    equalTo("The extender class hasn't a valid declared manifest header in the @ComponentExtenderSetup annotation."));
            throw e;
        }

    }

    // @Test(expected = ExceptionComponentUnrecoveredActivationError.class)
    public void ensureFailureForContributorWithoutExtenderConfigAnnotation()
            throws Exception {
        ConcreteExtenderComponentWithoutAnnotation concreteExtenderComponent = new ConcreteExtenderComponentWithoutAnnotation(
                componentContext, contributionHandlerService,
                contributionHandlerServiceFilter,
                contributionHandlerServiceTracker);

        try {
            // call a method that wraps the internalActivate method.
            concreteExtenderComponent.myInternalActivate();
        } catch (Exception e) {
            assertThat(
                    e.getMessage(),
                    equalTo("The extender class requires a proper configuration of the @ComponentExtenderSetup annotation."));
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
        dict.put(AbstractComponentBasic.EXTENDER_SERVICE_ATTR_MANIFEST_HEADER_NAME,
                "MyHeader");
        dict.put(AbstractComponentBasic.COMPONENT_DESCRIPTION,
                "Mocked Component Description");
        dict.put(
        		AbstractComponentBasic.EXTENDER_SERVICE_ATTR_EXTENSION_HANDLING_STRATEGY,
                ExtensionHandlingStrategy.PER_ITEM.getIdent());
        dict.put(AbstractComponentBasic.EXTENDER_SERVICE_LOOKUP_STATE_MASK,
                Bundle.ACTIVE);
        dict.put(
        		AbstractComponentBasic.EXTENDER_SERVICE_ATTR_CONTRIBUTION_ITEM_RESOURCE_TYPE,
                ContributionItemResourceType.PROPERTIES_FILE_RESOURCE
                        .getIdent());
        dict.put(
        		AbstractComponentBasic.EXTENDER_SERVICE_ATTR_CONTRIBUTION_HANDLER_SERVICE,
                "org.lunifera.runtime.utils.component.extender.handlers.ComponentContributionHandlerPropertiesFileResourcePerItemWithConfigAdmin");

        return dict;
    }
}
