package org.lunifera.runtime.utils.osgi.itests.samples.extender;

/*
 * #%L
 * org.lunifera.runtime.utils.osgi.itests.samples.extender
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

import org.lunifera.runtime.utils.osgi.component.extender.ContributionHandlerService;
import org.lunifera.runtime.utils.osgi.configuration.ComponentContributionHandlerServiceFactoryPropertiesFilePerItemWithConfigAdmin;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(
        enabled = true,
        servicefactory = true,
        service = ContributionHandlerService.class,
        properties = { "/OSGI-INF/AnComponentContributionHandlerServiceFactoryForTest.properties" })
public class AnComponentContributionHandlerServiceFactoryForTest extends
        ComponentContributionHandlerServiceFactoryPropertiesFilePerItemWithConfigAdmin
        implements ContributionHandlerService {

    /**
     * 
     * @param configurationAdmin
     */
    @Reference(policy = ReferencePolicy.STATIC,
            cardinality = ReferenceCardinality.MANDATORY)
    protected void bindConfigurationAdminService(
            ConfigurationAdmin configurationAdmin) {
        super.bindConfigurationAdminService(configurationAdmin);
    }
}
