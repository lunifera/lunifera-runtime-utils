package org.lunifera.runtime.utils.osgi.configuration;

/*
 * #%L
 * Lunifera Runtime Utilities - for OSGi
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

import java.io.IOException;
import java.net.URL;

import org.apache.felix.service.command.Descriptor;
import org.apache.felix.service.command.Parameter;
import org.lunifera.runtime.utils.osgi.component.AbstractComponentWithCompendium;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @since 0.0.1
 * @author Cristiano Gavião
 */
@Component(enabled = true, immediate = true,
        service = ComponentCommandConfigurationManagement.class, property = {
                "osgi.command.scope=lunifera.config",
                "osgi.command.function=list",
                "osgi.command.function=create",
                "osgi.command.function=delete" })
public class ComponentCommandConfigurationManagement extends
        AbstractComponentWithCompendium {

    @Reference
    @Override
    protected void bindConfigurationAdminService(
            ConfigurationAdmin configurationAdmin) {
        super.bindConfigurationAdminService(configurationAdmin);
    }

    @Descriptor("List configurations registered using ConfigurationAdmin service.")
    public void list(
            @Descriptor("show detailed info.") @Parameter(
                    absentValue = "false", presentValue = "true", names = {
                            "-v", "--verbose" }) boolean verbose){
        this.list(verbose, null);
    }

    @Descriptor("List configurations registered using ConfigurationAdmin service.")
    public void list(
            @Descriptor("show detailed info.") @Parameter(
                    absentValue = "false", presentValue = "true", names = {
                            "-v", "--verbose" }) boolean verbose,
            @Descriptor("filter used to search for configurations. all must be returned if no filter was informed.") String filter) {

        listConfigurations(filter, verbose);
    }

    @Descriptor("Create a configuration using ConfigurationAdmin service.")
    public void create(
            @Descriptor("whether the configuration is for a service factory.") @Parameter(
                    absentValue = "false", presentValue = "true", names = {
                            "-f", "--factory" }) boolean verbose,
            @Descriptor("The target PID.") String targetPID,
            @Descriptor("An URL pointing to a property file.") URL propertyFile) {

    }

    @Descriptor("Delete a configuration registered using ConfigurationAdmin service.")
    public void delete(
            @Descriptor("whether the configuration is for a service factory.") @Parameter(
                    absentValue = "false", presentValue = "true", names = {
                            "-f", "--factory" }) boolean verbose,
            @Descriptor("The target PID.") String targetPID) {

    }

    private void listConfigurations(String filter, boolean verbose) {
        try {
            Configuration[] configurations = getConfigurationAdminService()
                    .listConfigurations(filter);
            if (configurations != null && configurations.length > 0) {
                System.out.println("Configurations:");
                for (int i = 0; i < configurations.length; i++) {
                    System.out.println(i + ") Factory PID= '"
                            + configurations[i].getFactoryPid() + "', PID = '"
                            + configurations[i].getPid() + "', Location= '"
                            + configurations[i].getBundleLocation() + "'");
                    if (verbose) {
                        System.out.println("  Properties:"
                                + configurations[i].getProperties().toString());
                    }
                }
            } else {
                System.out.println("no configuration was found.");
            }
        } catch (IOException | InvalidSyntaxException e) {
            System.out
                    .println("a problem occurred when searching for configurations.");
            ;
        }
    }
}
