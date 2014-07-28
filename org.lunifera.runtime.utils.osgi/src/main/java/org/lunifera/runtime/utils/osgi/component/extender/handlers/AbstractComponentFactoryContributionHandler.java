package org.lunifera.runtime.utils.osgi.component.extender.handlers;

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

import org.lunifera.runtime.utils.osgi.component.AbstractComponentWithCompendium;
import org.lunifera.runtime.utils.osgi.component.ExceptionComponentLifecycle;
import org.lunifera.runtime.utils.osgi.component.extender.ConstantsExtender;
import org.lunifera.runtime.utils.osgi.component.extender.ContributionHandlerService;
import org.lunifera.runtime.utils.osgi.component.extender.ContributionItemResourceType;
import org.lunifera.runtime.utils.osgi.component.extender.ContributionHandlingStrategy;

/**
 * An parent abstract class for implementations of the
 * {@link ContributionHandlerService} interface.
 * 
 * @author cvgaviao
 * @since 0.0.1
 */
public abstract class AbstractComponentFactoryContributionHandler extends
        AbstractComponentWithCompendium implements ContributionHandlerService {

    private ContributionItemResourceType contributionItemResourceType;
    private ContributionHandlingStrategy extensionHandlingStrategy;

    /**
     * A default constructor is required by the OSGi Declarative Service.
     */
    public AbstractComponentFactoryContributionHandler() {
    }

    /**
     * Constructor created just to help unit testing. It is not used by OSGi.
     * 
     * @param contributionItemResourceType
     * @param extensionHandlingStrategy
     */
    protected AbstractComponentFactoryContributionHandler(
            ContributionItemResourceType contributionItemResourceType,
            ContributionHandlingStrategy extensionHandlingStrategy) {
        this.extensionHandlingStrategy = extensionHandlingStrategy;
        this.contributionItemResourceType = contributionItemResourceType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.lunifera.runtime.utils.osgi.component.AbstractComponentWithCompendium
     * #activate(org.osgi.service.component.ComponentContext)
     */
    @Override
    public void doBeforeActivateComponent() throws ExceptionComponentLifecycle {

        String contributorManifestHeaderItemTypeStr = (String) getProperties()
                .get(ConstantsExtender.EXTENDER_ATTR_CONTRIBUTION_ITEM_RESOURCE_TYPE);
        String extensionHandlingStrategyStr = (String) getProperties().get(
                ConstantsExtender.EXTENDER_ATTR_CONTRIBUTION_HANDLING_STRATEGY);

        this.extensionHandlingStrategy = ContributionHandlingStrategy
                .fromString(extensionHandlingStrategyStr);
        this.contributionItemResourceType = ContributionItemResourceType
                .fromString(contributorManifestHeaderItemTypeStr);
    }

    /**
     * @return the contributionItemResourceType
     */
    @Override
    public final ContributionItemResourceType getContributionItemResourceType() {
        return contributionItemResourceType;
    }

    /**
     * @return the extensionHandlingStrategy
     */
    @Override
    public final ContributionHandlingStrategy getContributionHandlingStrategy() {
        return extensionHandlingStrategy;
    }

    @Override
    public String getHandlerName() {
        return getName();
    }
}
