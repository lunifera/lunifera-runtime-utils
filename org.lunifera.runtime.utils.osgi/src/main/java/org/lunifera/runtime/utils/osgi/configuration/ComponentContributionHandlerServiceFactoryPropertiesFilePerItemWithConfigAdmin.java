package org.lunifera.runtime.utils.osgi.configuration;

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

import static org.lunifera.runtime.utils.osgi.component.extender.ConstantsExtender.EXTENDER_ATTR_CONTRIBUTION_ITEM_RESOURCE_EXTENSIONS;

import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;

import org.lunifera.runtime.utils.osgi.component.ExceptionComponentLifecycle;
import org.lunifera.runtime.utils.osgi.component.extender.AbstractComponentContributionHandlerServiceFactoryPropertiesFile;
import org.lunifera.runtime.utils.osgi.component.extender.ConstantsExtender;
import org.lunifera.runtime.utils.osgi.component.extender.ContributionHandlerService;
import org.lunifera.runtime.utils.osgi.component.extender.ContributionHandlingStrategy;
import org.lunifera.runtime.utils.osgi.component.extender.ContributionItem;
import org.lunifera.runtime.utils.osgi.component.extender.ContributionItemConfigAdmin;
import org.lunifera.runtime.utils.osgi.component.extender.ContributionItemConfigAdminImpl;
import org.lunifera.runtime.utils.osgi.component.extender.ContributionItemResourceType;
import org.lunifera.runtime.utils.osgi.component.extender.ContributorBundle;
import org.lunifera.runtime.utils.osgi.component.extender.ExceptionComponentExtenderSetup;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * A component that implements {@link ContributionHandlerService}.
 * <p>
 * It uses {@link ConfigurationAdmin} service to create a configuration based on
 * the values extracted from the contribution resource files specified in the
 * manifest header.
 * <p>
 * * TODO - this component should have a prototype and not servicefactory. just
 * waiting for r6;
 * 
 * @author cvgaviao
 * @since 0.0.1
 */
@Component(
        enabled = true,
        servicefactory = true,
        service = ContributionHandlerService.class,
        property = {
                EXTENDER_ATTR_CONTRIBUTION_ITEM_RESOURCE_EXTENSIONS + "=config",
                EXTENDER_ATTR_CONTRIBUTION_ITEM_RESOURCE_EXTENSIONS + "=conf",
                EXTENDER_ATTR_CONTRIBUTION_ITEM_RESOURCE_EXTENSIONS + "=cfg",
                "lunifera.extender.contribution.handler.alias=ConfigAdmin",
                "lunifera.extender.contribution.item.resource.type=properties_file_resource",
                "lunifera.extender.contribution.handling.strategy=per_item" })
public class ComponentContributionHandlerServiceFactoryPropertiesFilePerItemWithConfigAdmin
        extends
        AbstractComponentContributionHandlerServiceFactoryPropertiesFile
        implements ContributionHandlerService {

    public ComponentContributionHandlerServiceFactoryPropertiesFilePerItemWithConfigAdmin() {
        super();
    }

    /**
     * Constructor created just to help unit testing. It is not used by OSGi.
     * 
     * @param configurationAdmin
     * @param contributionItemResourceType
     * @param extensionHandlingStrategy
     */
    protected ComponentContributionHandlerServiceFactoryPropertiesFilePerItemWithConfigAdmin(
            ConfigurationAdmin configurationAdmin,
            ContributionItemResourceType contributionItemResourceType,
            ContributionHandlingStrategy extensionHandlingStrategy) {
        super(contributionItemResourceType, extensionHandlingStrategy);
        bindConfigurationAdminService(configurationAdmin);
    }

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

    private final void createConfigurationForContributionItem(
            ContributionItemConfigAdmin contributionItem, String location) {
        Configuration configuration;

        try {
            // must check if the contribution is a factory or not
            configuration = getConfigurationAdminService().getConfiguration(
                    contributionItem.targetPID(), location);
            // Ensure update is called, when properties are null; otherwise
            // configuration will not
            // be returned when listConfigurations is called (see specification
            // 104.15.3.7)
            if (configuration.getProperties() == null) {
                configuration.update(new Hashtable<>(contributionItem
                        .propertiesMap()));
                debug("Initialized store under PID: '{}', with these properties: {} and location: {}.",
                        contributionItem.targetPID(), contributionItem
                                .propertiesMap().toString(), location);
            } else {
                warn("Configuration for PID was already initialized: '{}'.",
                        contributionItem.targetPID());
            }
        } catch (IOException e) {
            error("Error on setup Configuration Service", e);
        }
    }

    @Override
    public ContributionItem createContributionItemFromResourceFile(
            ContributorBundle contributorBundleTrackerObject,
            URL resourceFileURL) {
        ContributionItemConfigAdmin contributionItem = new ContributionItemConfigAdminImpl(
                contributorBundleTrackerObject, resourceFileURL);

        contributionItem.propertiesMap().putAll(
                extractPropertiesFromResourceFile(resourceFileURL));

        if (contributionItem.usingFactoryConfiguration()) {
            createFactoryConfigurationForContributionItem(contributionItem,
                    null);
        } else {
            createConfigurationForContributionItem(contributionItem, null);
        }
        return contributionItem;
    }

    private void createFactoryConfigurationForContributionItem(
            ContributionItemConfigAdmin contributionItem, String location) {
        Configuration configuration;

        // try to find an existing configuration
        configuration = findFactoryConfigurationForContributionItem(contributionItem);
        if (configuration == null) {
            // create a new configuration
            try {
                configuration = getConfigurationAdminService()
                        .createFactoryConfiguration(
                                contributionItem.targetPID(), location);
            } catch (IOException e) {
                error("Error creating a factory configuration for bundle.", e);
            }
        }

        try {
            configuration.update(new Hashtable<>(contributionItem
                    .propertiesMap()));

        } catch (IOException e) {
            error("Error updating a factory configuration for bundle.", e);
        }
        debug("Created configuration in name of bundle '{}'({}) using FactoryPID: '{}' and PID: '{}' and these properties:\n{}",
                contributionItem.contributorBundle().symbolicName(),
                contributionItem.contributorBundle().version(),
                configuration.getFactoryPid(), configuration.getPid(),
                contributionItem.propertiesMap().toString());

    }

    private final void deleteAllConfigurationsForContributorBundle(
            ContributorBundle contributorBundleTrackerObject) {
        Configuration[] configurations;
        try {
            configurations = findAllConfigurationsForContributorBundle(contributorBundleTrackerObject);
            if (configurations != null) {
                for (Configuration configuration : configurations) {
                    try {
                        configuration.delete();
                    } catch (IOException e) {
                        error("Error deleting a factory configuration for bundle.",
                                e);
                    }
                }
            } else {
                debug("any configuration was found for contributor bundle '{}'({})",
                        contributorBundleTrackerObject.symbolicName(),
                        contributorBundleTrackerObject.version());
            }
        } catch (Exception e1) {
            error("problem occurred while deleting configurations for contributor bundle.",
                    e1);
        }
    }

    @Override
    public void doBeforeActivateComponent() throws ExceptionComponentLifecycle {
        super.doBeforeActivateComponent();
        if (getConfigurationAdminService() == null) {
            throw new ExceptionComponentExtenderSetup(
                    "ConfigurationAdmin service was not properly bound to this component. "
                            + "Please, override the bindConfigurationAdminService() method adding a proper @Reference annotation to it.");
        }
    }

    private Configuration[] findAllConfigurationsForContributorBundle(
            ContributorBundle contributorBundleInstance) throws IOException {
        try {
            Configuration[] configurations = getConfigurationAdminService()
                    .listConfigurations(
                            createConfigurationFilterForContributorBundle(contributorBundleInstance));
            if (configurations != null && configurations.length > 0) {
                return configurations;
            }
        } catch (InvalidSyntaxException e) {
            error("problem occurred while search for configurations for contributor bundle.",
                    e);
        }
        return null;
    }

    protected String createConfigurationFilterForContributorBundle(
            ContributorBundle contributorBundleInstance) {
        StringBuilder filter = new StringBuilder("(&");
        filter.append('(')
                .append(ConstantsExtender.EXTENDER_CONTRIBUTOR_BUNDLE_NAME)
                .append('=').append(contributorBundleInstance.symbolicName())
                .append(')');
        filter.append('(')
                .append(ConstantsExtender.EXTENDER_CONTRIBUTOR_BUNDLE_VERSION)
                .append('=').append(contributorBundleInstance.version())
                .append(')');
        filter.append(')');
        return filter.toString();
    }

    protected String createConfigurationFilterForContributionItem(
            ContributionItemConfigAdmin contributionItem) {
        StringBuilder filter = new StringBuilder("(&");
        filter.append('(').append(ConstantsExtender.EXTENDER_SERVICE_PID)
                .append('=').append(contributionItem.targetPID()).append(')');
        filter.append('(')
                .append(ConstantsExtender.EXTENDER_CONTRIBUTOR_BUNDLE_NAME)
                .append('=')
                .append(contributionItem.contributorBundle().symbolicName())
                .append(')');
        filter.append('(')
                .append(ConstantsExtender.EXTENDER_CONTRIBUTOR_BUNDLE_VERSION)
                .append('=')
                .append(contributionItem.contributorBundle().version())
                .append(')');
        filter.append('(')
                .append(ConstantsExtender.EXTENDER_CONTRIBUTOR_ITEM)
                .append('=')
                .append(contributionItem.contributionResourceFileLocation()
                        .getFile()).append(')');
        filter.append(')');
        return filter.toString();

    }

    // public String getFilter() {
    // String filter = "";
    // if (getFactoryPid() != null) {
    // filter = "(&("
    // + ConfigurationAdmin.SERVICE_FACTORYPID
    // + "="
    // + getFactoryPid()
    // + ") ("
    // + EXTENDER_CONTRIBUTOR_BUNDLE
    // + "="
    // + contributorBundleTrackerObject.getContributorBundle()
    // .getSymbolicName() + ")("
    // + EXTENDER_CONTRIBUTOR_ITEM + "="
    // + resourceLocation.getFile() + "))";
    // } else {
    // filter = "(&("
    // + EXTENDER_CONTRIBUTOR_BUNDLE
    // + "="
    // + contributorBundleTrackerObject.getContributorBundle()
    // .getSymbolicName() + ")("
    // + EXTENDER_CONTRIBUTOR_ITEM + "="
    // + resourceLocation.getFile() + "))";
    // }
    // return filter;
    // }

    private final Configuration findFactoryConfigurationForContributionItem(
            ContributionItemConfigAdmin contributionItem) {
        try {
            Configuration[] configurations = getConfigurationAdminService()
                    .listConfigurations(
                            createConfigurationFilterForContributionItem(contributionItem));
            if (configurations != null && configurations.length > 0) {
                return configurations[0];
            }
        } catch (Exception e) {
            error("Error on findFactoryConfigurationByContributionItem", e);
        }
        return null;
    }

    @Override
    public void removeContributionItems(
            ContributorBundle contributorBundleTrackerObject) {
        debug("Going to delete configuration for removed bundle: {}",
                contributorBundleTrackerObject.bundleWrapped()
                        .getSymbolicName());
        deleteAllConfigurationsForContributorBundle(contributorBundleTrackerObject);
    }
}
