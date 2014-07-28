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
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lunifera.runtime.utils.osgi.component.AbstractComponentBasic;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(MockitoJUnitRunner.class)
public class ComponentBasicLifecycleTest {

    static class AnComponent extends AbstractComponentBasic {

        public AnComponent() {
            super();
        }

        public AnComponent(BundleContext bundleContext,
                ComponentContext componentContext) {
            super(bundleContext, componentContext);
        }
    }

    static Logger logger = LoggerFactory
            .getLogger(ComponentBasicLifecycleTest.class);

    @Mock
    ComponentContext componentContext;
    
    @Mock
    BundleContext bundleContext;

    AnComponent concreteComponent;

    @Test
    public void ensureComponentLifecycleIsOk() throws Exception {

        ensureComponentWasActivatedProperly();
        ensureComponentWasModifiedProperly();
        ensureComponentWasDeactivateProperly();
    }

    public void ensureComponentWasActivatedProperly() throws Exception {
        assertThat(concreteComponent, notNullValue());
        assertThat(concreteComponent.getComponentContext(), notNullValue());

        concreteComponent.activate(componentContext);
        assertThat(concreteComponent.getId(), equalTo(121212l));
        assertThat(concreteComponent.getName(), equalTo("Mocked Component"));
        assertThat(concreteComponent.getDescription(),
                equalTo("Mocked Component Description"));
    }

    public void ensureComponentWasDeactivateProperly() throws Exception {
        concreteComponent.deactivate(componentContext);
        assertThat(concreteComponent.getComponentContext(), nullValue());
        assertThat(concreteComponent.getId(), equalTo(0L));
        assertThat(concreteComponent.getName(), equalTo(""));
        assertThat(concreteComponent.getDescription(), equalTo(""));
    }

    public void ensureComponentWasModifiedProperly() throws Exception {

        Dictionary<String, Object> dict = new Hashtable<>();
        dict.put(ComponentConstants.COMPONENT_ID, 121212l);
        dict.put(ComponentConstants.COMPONENT_NAME, "Mocked modified component");
        dict.put(AbstractComponentBasic.COMPONENT_DESCRIPTION,
                "Mocked modified Component Description");

        when(componentContext.getProperties()).thenReturn(dict);

        concreteComponent.modified(componentContext);
        assertThat(concreteComponent.getId(), equalTo(121212l));
        assertThat(concreteComponent.getName(),
                equalTo("Mocked modified component"));
        assertThat(concreteComponent.getDescription(),
                equalTo("Mocked modified Component Description"));
    }

    private Dictionary<String, Object> getMockProperties() {
        Dictionary<String, Object> dict = new Hashtable<>();
        dict.put(ComponentConstants.COMPONENT_ID, 121212l);
        dict.put(ComponentConstants.COMPONENT_NAME, "Mocked Component");
        dict.put(AbstractComponentBasic.COMPONENT_DESCRIPTION,
                "Mocked Component Description");

        return dict;
    }

    @Before
    public void setup() {
        concreteComponent = spy(new AnComponent(bundleContext, componentContext));
        when(componentContext.getProperties()).thenReturn(getMockProperties());
    }
}
