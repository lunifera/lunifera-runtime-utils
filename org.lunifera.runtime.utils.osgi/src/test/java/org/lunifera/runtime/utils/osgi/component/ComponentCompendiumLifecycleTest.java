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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.lunifera.runtime.utils.osgi.component.AbstractComponentBasic;
import org.lunifera.runtime.utils.osgi.component.AbstractComponentWithCompendium;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.prefs.PreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(MockitoJUnitRunner.class)
public class ComponentCompendiumLifecycleTest {

    static class AnComponent extends AbstractComponentWithCompendium {

        public AnComponent(ComponentContext componentContext) {
            super(componentContext);
        }

    }

    static Logger logger = LoggerFactory
            .getLogger(ComponentCompendiumLifecycleTest.class);

    @Mock
    ComponentContext componentContext;

    AbstractComponentWithCompendium concreteComponent;

    @Mock
    ConfigurationAdmin configurationAdmin;

    @Mock
    EventAdmin eventAdmin;

    Logger loggerService;

    @Mock
    PreferencesService preferencesService;

    @Test
    public void ensureCompendiumTrackersAreBeingUsedWhenThereIsNoDsInjectingThingsBeforeActivate()
            throws Exception {

        assertThat(componentContext, notNullValue());

        when(componentContext.getProperties()).thenReturn(getMockProperties());

        concreteComponent = spy(new AnComponent(componentContext));
        doReturn(true).when(concreteComponent).openTrackerForLoggingService();
        doReturn(true).when(concreteComponent)
                .openTrackerForEventAdminService();
        doReturn(true).when(concreteComponent)
                .openTrackerForPreferenceService();

        assertThat(concreteComponent, notNullValue());

        concreteComponent.bindEventAdmin(eventAdmin);
        concreteComponent.bindLoggerService(logger);
        concreteComponent.bindPreferencesService(preferencesService);

        concreteComponent.activate(componentContext);

        InOrder inOrder = Mockito.inOrder(concreteComponent);

        inOrder.verify(concreteComponent).bindEventAdmin(any(EventAdmin.class));
        inOrder.verify(concreteComponent).bindLoggerService(any(Logger.class));
        inOrder.verify(concreteComponent).bindPreferencesService(
                any(PreferencesService.class));
        inOrder.verify(concreteComponent).activate(componentContext);

        assertThat(concreteComponent.getEventAdminService(), notNullValue());
        assertThat(concreteComponent.getPreferencesService(), notNullValue());
        assertThat(concreteComponent.getLoggerService(), notNullValue());
    }

    @Test
    public void ensureCompendiumTrackersAreNotUsedWhenDsBindsBefore()
            throws Exception {

        assertThat(componentContext, notNullValue());

        when(componentContext.getProperties()).thenReturn(getMockProperties());

        concreteComponent = spy(new AnComponent(componentContext));
        assertThat(concreteComponent, notNullValue());

        concreteComponent.bindEventAdmin(eventAdmin);
        concreteComponent.bindLoggerService(logger);
        concreteComponent.bindPreferencesService(preferencesService);

        assertThat(concreteComponent.getEventAdminService(), notNullValue());
        assertThat(concreteComponent.getPreferencesService(), notNullValue());
        assertThat(concreteComponent.getLoggerService(), notNullValue());

        concreteComponent.activate(componentContext);

        verify(concreteComponent).activate(componentContext);
        verify(concreteComponent).bindEventAdmin(eventAdmin);
        verify(concreteComponent).bindLoggerService(logger);
        verify(concreteComponent).bindPreferencesService(preferencesService);

    }

    public void ensureComponentWasActivatedProperly() throws Exception {
        assertThat(concreteComponent.getEventAdminService(), notNullValue());
        assertThat(concreteComponent.getLoggerService(), notNullValue());
        assertThat(concreteComponent.getPreferencesService(), notNullValue());
        concreteComponent.activate(componentContext);

        assertThat(concreteComponent.getId(), equalTo(121212l));
        assertThat(concreteComponent.getName(), equalTo("Mocked Component"));
        assertThat(concreteComponent.getDescription(),
                equalTo("Mocked Component Description"));

    }

    public void ensureComponentWasDeactivateProperly() throws Exception {
        concreteComponent.deactivate(componentContext);
        assertThat(concreteComponent.getComponentContext(), nullValue());
        concreteComponent.unbindEventAdmin(eventAdmin);
        assertThat(concreteComponent.getEventAdminService(), nullValue());
    }

    private Dictionary<String, Object> getMockProperties() {
        Dictionary<String, Object> dict = new Hashtable<>();
        dict.put(ComponentConstants.COMPONENT_ID, 121212l);
        dict.put(ComponentConstants.COMPONENT_NAME, "Mocked Component");
        dict.put(AbstractComponentBasic.COMPONENT_DESCRIPTION,
                "Mocked Component Description");

        return dict;
    }

}
