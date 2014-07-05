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

import static org.osgi.framework.Constants.SERVICE_PID;
import static org.osgi.service.cm.ConfigurationAdmin.SERVICE_FACTORYPID;

import java.io.IOException;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicReference;

import org.lunifera.runtime.utils.osgi.component.extender.ContributorBundleTrackerObject.ContributionItem;
import org.osgi.framework.Bundle;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * 
 * @author cvgaviao
 * @since 0.0.1
 */
@Component(
        enabled = false,
        servicefactory = true,
        service = ContributionHandlerService.class,
        property = {
                "lunifera.extender.contribution.item.resource.type=properties_file_resource",
                "lunifera.extender.handling.strategy=per_item" })
public class ComponentContributionHandlerPropertiesFileResourcePerItemWithConfigAdmin
        extends
        AbstractComponentFactoryContributionHandlerPropertiesFileResourcePerItem {

    /**
     *
     */
    private AtomicReference<ConfigurationAdmin> configAdminServiceRef = new AtomicReference<ConfigurationAdmin>();;

    public ComponentContributionHandlerPropertiesFileResourcePerItemWithConfigAdmin() {
        super();
    }

    /**
     * Constructor created just to help unit testing. It is not used by OSGi.
     * 
     * @param contributionItemResourceType
     * @param extensionHandlingStrategy
     */
    protected ComponentContributionHandlerPropertiesFileResourcePerItemWithConfigAdmin(
            ContributionItemResourceType contributionItemResourceType,
            ExtensionHandlingStrategy extensionHandlingStrategy) {
        super(contributionItemResourceType, extensionHandlingStrategy);
    }

    /**
     * 
     * @param configurationAdmin
     */
    @Reference(policy = ReferencePolicy.STATIC)
    protected final void bindConfigAdminService(
            ConfigurationAdmin configurationAdmin) {
        this.configAdminServiceRef.set(configurationAdmin);
        trace("({}) - Bound ConfigAdmin Service for component '{}'.", getId(),
                getName());
    }

    protected final void createConfigurationForContributionItem(
            ContributionItem contributionItem) {
        Configuration configuration;

        // must check if the contribution is a factory or not

        try {
            configuration = getConfigurationAdmin().getConfiguration(
                    contributionItem.getPid(), null);
            // Ensure update is called, when properties are null; otherwise
            // configuration will not
            // be returned when listConfigurations is called (see specification
            // 104.15.3.7)
            if (configuration.getProperties() == null) {
                configuration.update(new Hashtable<>(contributionItem
                        .getPropertiesMap()));
                debug("Initialized store under PID: '{}', with this properties: {}",
                        contributionItem.getPid(), contributionItem
                                .getPropertiesMap().toString());
            } else {
                warn("Configuration for PID was already initialized: '{}'.",
                        contributionItem.getPid());
            }
        } catch (IOException e) {
            error("Error on setup Configuration Service", e);
        }
    }

    protected final void createConfigurationForContributorBundle(
            ContributorBundleTrackerObject contributorBundleTrackerObject) {
        // Configuration configuration;

    }

    private void createFactoryConfigurationForContributionItem(
            ContributionItem contributionItem) {
        Configuration configuration;

        // try to find an existing configuration
        configuration = findFactoryConfigurationForContributionItem(contributionItem);
        if (configuration == null) {
            // create a new configuration
            try {
                configuration = getConfigurationAdmin()
                        .createFactoryConfiguration(
                                contributionItem.getFactoryPid(), null);
            } catch (IOException e) {
                error("Error creating a Factory Config", e);
            }
        }

        try {
            configuration.update(new Hashtable<>(contributionItem
                    .getPropertiesMap()));

        } catch (IOException e) {
            error("Error updating a Factory Config", e);
        }
        debug("Created configuration for FactoryPID: '{}' and PID: '{}', with this properties:\n{}",
                configuration.getFactoryPid(), configuration.getPid(),
                contributionItem.getPropertiesMap().toString());

    }

    protected final void deleteConfigurationsForContributorBundle(
            ContributorBundleTrackerObject contributorBundleTrackerObject) {
        // TODO Auto-generated method stub

    }

    protected void deleteFactoryConfigurationForExtendedBundle(
            String extenderBundle) {
        Configuration[] configurations;
        try {
            configurations = findFactoryConfigurationForExtendedBundle(extenderBundle);
            if (configurations != null) {
                for (Configuration configuration : configurations) {
                    try {
                        configuration.delete();
                    } catch (IOException e) {
                        error("Error on setup Configuration Service", e);
                    }
                }
            } else {
                debug("no configuration for factoryPid '{}' and extenderBundle '{}'",
                        extenderBundle);
            }
        } catch (Exception e1) {
            error("no configuration for factoryPid '{}' and extenderBundle '{}'",
                    extenderBundle, e1);
        }

    }

    @SuppressWarnings("unused")
    // TODO need to handle this...
    private void deleteProperties(String pid) {
        Configuration configuration;
        try {
            configuration = findConfiguration(pid);
            if (configuration != null) {
                try {
                    configuration.delete();
                } catch (IOException e) {
                    error("Error on setup Configuration Service", e);
                }
            } else {
                debug("no configuration for pid '{}'", pid);
            }
        } catch (IOException e1) {
            error("no configuration for pid '" + pid + "'", e1);
        }

    }

    private void extractPidsFromResourceFileName(
            ContributionItem contributionItem) {
        String separator = "/";
        String filename;
        String configFile = contributionItem.getResourceLocation().getFile();

        // Remove the path up to the filename.
        int lastSeparatorIndex = configFile.lastIndexOf(separator);
        if (lastSeparatorIndex == -1) {
            filename = configFile;
        } else {
            filename = configFile.substring(lastSeparatorIndex + 1);
        }

        // Factory (if it exists)
        int qualifierIndex = filename.lastIndexOf('\u005f');
        int extensionIndex = filename.lastIndexOf('\u002e');
        if (qualifierIndex != -1) {
            contributionItem.getPropertiesMap().put(SERVICE_FACTORYPID,
                    filename.substring(0, qualifierIndex));
            if (extensionIndex != -1) {
                String pid = filename.substring(qualifierIndex + 1,
                        extensionIndex);
                if (!pid.isEmpty()) {
                    contributionItem.getPropertiesMap().put(
                            SERVICE_PID,
                            filename.substring(qualifierIndex + 1,
                                    extensionIndex));
                }
            } else {
                contributionItem.getPropertiesMap().put(SERVICE_PID,
                        filename.substring(qualifierIndex));
            }
        } else {
            if (extensionIndex == -1) {
                contributionItem.getPropertiesMap().put(SERVICE_PID, filename);
            } else {
                contributionItem.getPropertiesMap().put(SERVICE_PID,
                        filename.substring(0, extensionIndex));
            }
        }
    }

    protected Configuration findConfiguration(String pid) throws IOException {
        // As ConfigurationAdmin.getConfiguration creates the configuration if
        // it is not yet there, we check its existence first
        try {
            Configuration[] configurations = getConfigurationAdmin()
                    .listConfigurations("(service.pid=" + pid + ")");
            if (configurations != null && configurations.length > 0) {
                return configurations[0];
            }
        } catch (InvalidSyntaxException e) {
            error("Error on findConfiguration", e);
        }

        return null;
    }

    protected final Configuration findFactoryConfigurationForContributionItem(
            ContributionItem contributionItem) {
        try {
            Configuration[] configurations = getConfigurationAdmin()
                    .listConfigurations(contributionItem.getFilter());
            if (configurations != null && configurations.length > 0) {
                return configurations[0];
            }
        } catch (Exception e) {
            error("Error on findFactoryConfigurationByContributionItem", e);
        }
        return null;
    }

    private Configuration[] findFactoryConfigurationForExtendedBundle(
            String extenderBundle) {
        // TODO Auto-generated method stub
        return null;
    }

    public final ConfigurationAdmin getConfigurationAdmin() {
        return configAdminServiceRef.get();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.lunifera.runtime.utils.osgi.component.extender.handlers.
     * AbstractComponentFactoryContributionHandlerPropertiesFileResourcePerItem
     * #onContributorBundleAddition(org.osgi.framework.Bundle, java.lang.String,
     * java.lang.String)
     */
    @Override
    public ContributorBundleTrackerObject onContributorBundleAddition(
            Bundle contributorBundle, String headerName, String headerValue)
            throws ExceptionComponentExtenderSetup {
        ContributorBundleTrackerObject result = super
                .onContributorBundleAddition(contributorBundle, headerName,
                        headerValue);
        // process every ContributionItem created and create a proper
        // Configuration for them
        for (ContributionItem contributionItem : result
                .getContributionItemList()) {

            extractPidsFromResourceFileName(contributionItem);

            if (contributionItem.getFactoryPid() != null) {
                createFactoryConfigurationForContributionItem(contributionItem);
            } else {
                createConfigurationForContributionItem(contributionItem);
            }
        }
        return result;
    }

    @Override
    public void onContributorBundleModified(
            ContributorBundleTrackerObject contributorBundleTrackerObject)
            throws ExceptionComponentExtenderSetup {
        warn("Stated modified: {}", contributorBundleTrackerObject);
    }

    @Override
    public void onContributorBundleRemoval(
            ContributorBundleTrackerObject contributorBundleTrackerObject)
            throws ExceptionComponentExtenderSetup {

        deleteConfigurationsForContributorBundle(contributorBundleTrackerObject);
    }

    protected final void unbindConfigAdminService(
            ConfigurationAdmin configurationAdmin) {
        this.configAdminServiceRef.compareAndSet(configurationAdmin, null);
        this.configAdminServiceRef = null;
        trace("Unbound ConfigAdmin Service for component '{}'.", this
                .getClass().getName());
    }
}
