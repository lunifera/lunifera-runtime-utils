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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.typeCompatibleWith;
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
import org.lunifera.runtime.utils.osgi.services.PluggableEventTracker;
import org.lunifera.runtime.utils.osgi.services.PluggableServiceTracker;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.coordinator.Coordinator;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.prefs.PreferencesService;
import org.slf4j.Logger;

@RunWith(MockitoJUnitRunner.class)
public class ComponentCompendiumLifecycleTest {

    static class AnComponent extends AbstractComponentWithCompendium {

        @SuppressWarnings("rawtypes")
        public AnComponent(BundleContext bundleContext,
                ComponentContext componentContext,
                Map<Class<?>, PluggableServiceTracker> serviceTrackers,
                Set<PluggableEventTracker> eventTrackers) {
            super(bundleContext, componentContext, serviceTrackers,
                    eventTrackers);
        }

    }
    AbstractComponentWithCompendium concreteComponent;

    @Mock
    ComponentContext componentContext;

    @Mock
    Bundle bundle;

    @Mock
    BundleContext bundleContext;

    @SuppressWarnings("rawtypes")
    Map<Class<?>, PluggableServiceTracker> serviceTrackers = new HashMap<>();

    Set<PluggableEventTracker> eventTrackers = new HashSet<>();

    @SuppressWarnings("unchecked")
    @Before
    public void before() {
        serviceTrackers.clear();

        eventTrackers.clear();

        when(componentContext.getProperties()).thenReturn(getMockProperties());

        when(componentContext.getBundleContext()).thenReturn(bundleContext);

        when(bundleContext.getBundle()).thenReturn(bundle);
        when(bundle.getSymbolicName()).thenReturn("bundle.test.component");
        when(bundle.getVersion()).thenReturn(new Version("1.0.0"));

        serviceTrackers
                .put(Coordinator.class,
                        (PluggableServiceTracker<Coordinator>) mock(PluggableServiceTracker.class));
        serviceTrackers
                .put(Logger.class,
                        (PluggableServiceTracker<Logger>) mock(PluggableServiceTracker.class));
        serviceTrackers
                .put(PreferencesService.class,
                        (PluggableServiceTracker<PreferencesService>) mock(PluggableServiceTracker.class));
        serviceTrackers
                .put(EventAdmin.class,
                        (PluggableServiceTracker<EventAdmin>) mock(PluggableServiceTracker.class));
    }

    @Test
    public void ensureCustomEventTrackersAreBeingSet() throws Exception {
        assertThat(componentContext, notNullValue());
        when(componentContext.getProperties()).thenReturn(getMockProperties());
        final PluggableEventTracker pl = mock(PluggableEventTracker.class);

        concreteComponent = new AnComponent(bundleContext, componentContext,
                serviceTrackers, eventTrackers) {
            @Override
            protected void defineEventsToBeTracked() {
                trackEvent(pl);
            }

            @Override
            protected void defineInnerServicesToBeTracked() {
            }
        };
        assertThat(concreteComponent, notNullValue());
        assertThat(concreteComponent.getEventTrackers().isEmpty(), is(true));

        concreteComponent.activate(componentContext);
        assertThat(concreteComponent.getEventTrackers().size(), is(1));
        verify(pl).registerEventHandler();

        concreteComponent.deactivate(componentContext);
        verify(pl).unregisterEventHandler();
        assertThat(concreteComponent.getInnerPluggableServiceTrackers().size(),
                is(0));
    }

    @Test
    public void ensureDefaultServiceTrackersAreBeingSet() throws Exception {
        assertThat(componentContext, notNullValue());
        when(componentContext.getProperties()).thenReturn(getMockProperties());

        concreteComponent = new AnComponent(bundleContext, componentContext,
                null, null) {
        };
        assertThat(concreteComponent, notNullValue());
        concreteComponent.defineInnerServicesToBeTracked();
        assertThat(concreteComponent.getInnerPluggableServiceTrackers().values(),
                is(empty()));
        assertThat(concreteComponent.getEventTrackers(), is(empty()));

        concreteComponent = new AnComponent(bundleContext, componentContext,
                serviceTrackers, eventTrackers) {
        };
        assertThat(concreteComponent, notNullValue());
        assertThat(concreteComponent.getInnerPluggableServiceTrackers().size(),
                is(4));
        assertThat(concreteComponent.getEventTrackers(), is(empty()));

        concreteComponent.activate(componentContext);
        assertThat(concreteComponent.getInnerPluggableServiceTrackers().size(),
                is(4));

        PluggableServiceTracker<Logger> stl = new PluggableServiceTracker<Logger>(
                Logger.class, bundleContext) {
        };

        PluggableServiceTracker<Logger> stl2 = concreteComponent
                .getInnerPluggableServiceTracker(Logger.class);
        assertThat(stl.getServiceType(), typeCompatibleWith(Logger.class));
        assertThat(concreteComponent.getPreferencesService(), nullValue());
        assertThat(concreteComponent.getEventAdminService(), nullValue());
        assertThat(concreteComponent.getLoggerService(), notNullValue());

        verify(stl2).open();

        concreteComponent.deactivate(componentContext);
        verify(stl2).close();
        assertThat(concreteComponent.getInnerPluggableServiceTrackers().size(),
                is(0));

    }

    @SuppressWarnings("unchecked")
    @Test
    public void ensureCustomServiceTrackersAreBeingSet() throws Exception {
        assertThat(componentContext, notNullValue());
        when(componentContext.getProperties()).thenReturn(getMockProperties());

        final PluggableServiceTracker<Logger> pl = (PluggableServiceTracker<Logger>) mock(PluggableServiceTracker.class);
        when(pl.getServiceType()).thenReturn(Logger.class);

        concreteComponent = new AnComponent(bundleContext, componentContext,
                null, eventTrackers) {
            @Override
            protected void defineInnerServicesToBeTracked()
                    throws ExceptionComponentLifecycle {
                trackServicesWith(pl);
            }
        };
        assertThat(concreteComponent, notNullValue());
        assertThat(concreteComponent.getEventTrackers(), is(empty()));
        assertThat(concreteComponent.getInnerPluggableServiceTrackers()
                .values(), is(empty()));

        concreteComponent.activate(componentContext);

        assertThat(concreteComponent.getInnerPluggableServiceTrackers().size(),
                is(1));
        assertThat(pl.getServiceType(), typeCompatibleWith(Logger.class));

        verify(pl).open();

        concreteComponent.deactivate(componentContext);
        verify(pl).close();
        assertThat(concreteComponent.getInnerPluggableServiceTrackers().size(),
                is(0));

    }

    private Dictionary<String, Object> getMockProperties() {
        Dictionary<String, Object> dict = new Hashtable<>();
        dict.put(ComponentConstants.COMPONENT_ID, 121212l);
        dict.put(
                AbstractComponentWithCompendium.PROPERTY_ENABLE_INNER_SERVICE_TRACKERS,
                true);
        dict.put(ComponentConstants.COMPONENT_NAME, "Mocked Component");
        dict.put(AbstractComponentBasic.COMPONENT_DESCRIPTION,
                "Mocked Component Description");
        return dict;
    }
}
