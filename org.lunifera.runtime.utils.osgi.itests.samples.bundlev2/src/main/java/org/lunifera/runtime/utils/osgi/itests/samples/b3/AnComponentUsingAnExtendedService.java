package org.lunifera.runtime.utils.osgi.itests.samples.b3;

/*
 * #%L
 * org.lunifera.runtime.utils.osgi.itests.samples.bundlev2
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

import java.util.concurrent.atomic.AtomicReference;

import org.lunifera.runtime.utils.osgi.component.AbstractComponentWithCompendium;
import org.lunifera.runtime.utils.osgi.itests.samples.b2.ServiceActivatedByExtender;
import org.lunifera.runtime.utils.osgi.itests.samples.b2.ServiceFactoryTest;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(enabled = true, service = AnComponentUsingAnExtendedService.class,
        configurationPid = "service2",
        configurationPolicy = ConfigurationPolicy.REQUIRE)
public class AnComponentUsingAnExtendedService extends
        AbstractComponentWithCompendium {

    private final AtomicReference<ServiceActivatedByExtender> serviceActivatedByExtenderRef = new AtomicReference<>();

    private final AtomicReference<ServiceFactoryTest> serviceFactoryTestRef = new AtomicReference<>();

    @Reference(policy = ReferencePolicy.DYNAMIC)
    protected void bindServiceActivatedByExtender(
            ServiceActivatedByExtender serviceActivatedByExtender) {
        this.serviceActivatedByExtenderRef.set(serviceActivatedByExtender);
    }

    @Reference(policy = ReferencePolicy.DYNAMIC,
            cardinality = ReferenceCardinality.MANDATORY)
    protected void bindServiceFactoryTest(ServiceFactoryTest serviceFactoryTest) {
        this.serviceFactoryTestRef.set(serviceFactoryTest);
    }

    protected final void unbindServiceActivatedByExtender(
            ServiceActivatedByExtender serviceActivatedByExtender) {
        serviceActivatedByExtenderRef.compareAndSet(serviceActivatedByExtender,
                null);
    }

    protected final void unbindServiceFactoryTest(
            ServiceFactoryTest serviceFactoryTest) {
        serviceFactoryTestRef.compareAndSet(serviceFactoryTest, null);
    }

}
