package org.lunifera.runtime.utils.osgi.itests.samples.extender;

/*
 * #%L
 * Lunifera Subsystems - Runtime Kernel - Integration Tests
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

import static org.lunifera.runtime.utils.osgi.component.extender.ConstantsExtender.CONTRIBUTION_HANDLING_STRATEGY_PER_ITEM;
import static org.lunifera.runtime.utils.osgi.component.extender.ConstantsExtender.EXTENDER_ATTR_CONTRIBUTION_HANDLER_ALIAS;
import static org.lunifera.runtime.utils.osgi.component.extender.ConstantsExtender.EXTENDER_ATTR_CONTRIBUTION_HANDLING_STRATEGY;
import static org.lunifera.runtime.utils.osgi.component.extender.ConstantsExtender.EXTENDER_ATTR_CONTRIBUTION_ITEM_RESOURCE_TYPE;
import static org.lunifera.runtime.utils.osgi.component.extender.ConstantsExtender.RESOURCE_TYPE_PROPERTIES_FILE_RESOURCE;

import org.lunifera.runtime.utils.osgi.component.extender.AbstractComponentExtender;
import org.lunifera.runtime.utils.osgi.component.extender.ContributionHandlerService;
import org.lunifera.runtime.utils.osgi.component.extender.ExtenderService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(enabled = true, immediate = true,
        service = ExtenderService.class, property={"lunifera.extender.manifest.header.name=XFIRE","lunifera.extender.bundle.state.mask=32"})
public class AnComponentExtenderForTest extends AbstractComponentExtender
        implements ExtenderService {

    @Reference(policy = ReferencePolicy.STATIC, target = "(&("
            + EXTENDER_ATTR_CONTRIBUTION_ITEM_RESOURCE_TYPE + "="
            + RESOURCE_TYPE_PROPERTIES_FILE_RESOURCE + ")("
            + EXTENDER_ATTR_CONTRIBUTION_HANDLING_STRATEGY + "="
            + CONTRIBUTION_HANDLING_STRATEGY_PER_ITEM + ")("
            + EXTENDER_ATTR_CONTRIBUTION_HANDLER_ALIAS
            + "=XFIRE))")
    @Override
    protected void bindContributionHandlerService(
            ContributionHandlerService contributionHandlerService) {
        super.bindContributionHandlerService(contributionHandlerService);
    }
}
