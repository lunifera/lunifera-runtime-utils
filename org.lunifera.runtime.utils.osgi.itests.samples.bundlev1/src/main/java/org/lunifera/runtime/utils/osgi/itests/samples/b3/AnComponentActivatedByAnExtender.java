package org.lunifera.runtime.utils.osgi.itests.samples.b3;

/*
 * #%L
 * org.lunifera.runtime.utils.osgi.itests.samples.bundlev1
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

import java.util.Dictionary;

import org.lunifera.runtime.utils.osgi.component.AbstractComponentWithCompendium;
import org.lunifera.runtime.utils.osgi.component.ExceptionComponentLifecycle;
import org.lunifera.runtime.utils.osgi.itests.samples.b2.ServiceActivatedByExtender;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

@Component(enabled = true, service = ServiceActivatedByExtender.class,
        configurationPid = "service1",
        configurationPolicy = ConfigurationPolicy.REQUIRE)
public class AnComponentActivatedByAnExtender extends
        AbstractComponentWithCompendium implements ServiceActivatedByExtender {

    @Override
    protected void doAfterActivateComponent()
            throws ExceptionComponentLifecycle {
        super.doAfterActivateComponent();
        System.out.println("ACTIVATED !!!");
    }
    
    @Override
    public String getServiceID() {

        return (String) getComponentContext().getProperties().get(
                COMPONENT_DESCRIPTION);
    }

    @Override
    public Dictionary<String, Object> getPropertiesFromComponent() {
        return getProperties();
    }

}
