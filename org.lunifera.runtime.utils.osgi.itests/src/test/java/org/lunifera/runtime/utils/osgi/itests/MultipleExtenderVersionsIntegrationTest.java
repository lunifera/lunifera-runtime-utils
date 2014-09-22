package org.lunifera.runtime.utils.osgi.itests;

/*
 * #%L
 * Lunifera Runtime Utilities - for OSGi - Integration Tests
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
import static org.hamcrest.Matchers.notNullValue;
import static org.knowhowlab.osgi.testing.assertions.BundleAssert.assertBundleAvailable;
import static org.knowhowlab.osgi.testing.assertions.ServiceAssert.assertServiceAvailable;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.osgi.framework.Constants.REQUIRE_BUNDLE;

import javax.inject.Inject;

import org.apache.felix.scr.Component;
import org.apache.felix.scr.ScrService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.knowhowlab.osgi.testing.assertions.cmpn.ConfigurationAdminAssert;
import org.knowhowlab.osgi.testing.utils.ServiceUtils;
import org.knowhowlab.osgi.testing.utils.cmpn.ConfigurationAdminUtils;
import org.lunifera.runtime.utils.osgi.component.extender.ContributionHandlerService;
import org.lunifera.runtime.utils.osgi.component.extender.ExtenderService;
import org.lunifera.runtime.utils.osgi.itests.samples.b2.ServiceActivatedByExtender;
import org.lunifera.runtime.utils.osgi.itests.samples.b2.ServiceFactoryTest;
import org.lunifera.runtime.utils.paxexam.PaxexamDefaultOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.ProbeBuilder;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.options.UrlProvisionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.PathUtils;
import org.osgi.framework.ServiceEvent;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class MultipleExtenderVersionsIntegrationTest extends
        AbstractUtilsIntegrationTest {

    public static UrlProvisionOption workspaceBundle(String artifactId) {
        String url = String.format("reference:file:%s/../%s/target/classes",
                PathUtils.getBaseDir(), artifactId);
        return bundle(url);
    }

    @Inject
    ScrService scrManagedService;

    @Override
    protected DefaultCompositeOption bundlesToBeInstalled() {
        assertThat(PaxexamDefaultOptions.PROJECT_VERSION, notNullValue());
        return super
                .bundlesToBeInstalled()
                .add(mavenBundle("org.lunifera.runtime.utils",
                        "org.lunifera.runtime.utils.osgi",
                        PaxexamDefaultOptions.PROJECT_VERSION).startLevel(3))
                .add(mavenBundle("org.lunifera.runtime.utils",
                        "org.lunifera.runtime.utils.osgi.logging",
                        PaxexamDefaultOptions.PROJECT_VERSION).startLevel(4))
                .add(mavenBundle(
                        "org.lunifera.runtime.utils",
                        "org.lunifera.runtime.utils.osgi.itests.samples.extender",
                        PaxexamDefaultOptions.PROJECT_VERSION).startLevel(4))
                .add(mavenBundle(
                        "org.lunifera.runtime.utils",
                        "org.lunifera.runtime.utils.osgi.itests.samples.config",
                        PaxexamDefaultOptions.PROJECT_VERSION).startLevel(4))
//                .add(mavenBundle(
//                        "org.lunifera.runtime.utils",
//                        "org.lunifera.runtime.utils.osgi.itests.samples.bundlev1",
//                        PaxexamDefaultOptions.PROJECT_VERSION).startLevel(4))
                .add(mavenBundle(
                        "org.lunifera.runtime.utils",
                        "org.lunifera.runtime.utils.osgi.itests.samples.bundlev2",
                        PaxexamDefaultOptions.PROJECT_VERSION).startLevel(4));
    }

    /**
     * Unfortunately we have to do this as the children class don't inherit
     * 
     * @Configuration method annotation.
     */
    @org.ops4j.pax.exam.Configuration
    public Option[] configureIntegrationTest() {
        return super.configureIntegrationTest();
    }

    @Test
    public void ensureExtenderComponentIsFunctional() {

        System.out.println("LOCAL test: " + bc.getBundle().getLocation());

        ServiceUtils.waitForServiceEvent(bc,
                org.apache.felix.scr.ScrService.class, ServiceEvent.REGISTERED,
                5000);
        ServiceUtils.waitForServiceEvent(bc, ExtenderService.class,
                ServiceEvent.REGISTERED, 5000);
        ServiceUtils.waitForServiceEvent(bc, ContributionHandlerService.class,
                ServiceEvent.REGISTERED, 15000);

        ensureRequiredBundlesAreAvailable();
        ensureRequiredServicesAreAvailable();

        assertServiceAvailable("ContributionHandlerService is not available",
                ContributionHandlerService.class, 5000);
        assertServiceAvailable("ExtenderService was not available",
                ExtenderService.class, 5000);
        assertServiceAvailable("ServiceFactoryTest was not registered",
                ServiceFactoryTest.class);
        assertServiceAvailable("ServiceActivatedByExtender was not registered",
                ServiceActivatedByExtender.class, 5000);

        ConfigurationAdminAssert.assertConfigurationAvailable(bc,
                ConfigurationAdminUtils.createConfigurationFilter(null,
                        "servicefactory1", null));
        ConfigurationAdminAssert.assertConfigurationAvailable(bc,
                ConfigurationAdminUtils.createConfigurationFilter("service1",
                        null, null));
        ConfigurationAdminAssert.assertConfigurationAvailable(bc,
                ConfigurationAdminUtils.createConfigurationFilter("service2",
                        null, null));

        // assertThat(isComponentEnabled("org.lunifera.runtime.utils.osgi.itests.samples.extender.AnComponentExtenderForTest"),
        // is(true));
        // assertThat(isComponentEnabled("org.lunifera.runtime.utils.osgi.itests.samples.b2.AnComponentServiceFactoryForTest"),
        // is(true));
        // assertThat(isComponentEnabled("org.lunifera.runtime.utils.osgi.itests.samples.b3.AnComponentActivatedByAnExtender"),
        // is(true));
        // assertThat(isComponentEnabled("org.lunifera.runtime.utils.osgi.itests.samples.b3.AnComponentUsingAnExtendedService"),
        // is(true));
    }

    @Override
    public void ensureRequiredBundlesAreAvailable() {
        super.ensureRequiredBundlesAreAvailable();
        assertBundleAvailable("org.lunifera.runtime.utils.osgi");
        assertBundleAvailable("org.lunifera.runtime.utils.osgi.itests.samples.config");
        assertBundleAvailable("org.lunifera.runtime.utils.osgi.itests.samples.bundlev1");
        assertBundleAvailable("org.lunifera.runtime.utils.osgi.itests.samples.bundlev2");
        assertBundleAvailable("org.lunifera.runtime.utils.osgi.itests.samples.extender");
    }

    public boolean isComponentEnabled(String componentName) {
        Component[] components = scrManagedService.getComponents();
        for (int i = 0; i < components.length; i++) {
            Component component = components[i];
            System.out.println("COMP:" + component.getName());
        }
        Component[] componentsw = scrManagedService
                .getComponents(componentName);
        if (componentsw == null)
            return false;
        int state = components[0].getState();
        return Component.STATE_ACTIVE == state;
    }

    @ProbeBuilder
    public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
        probe.setHeader(REQUIRE_BUNDLE,
                "org.hamcrest.library;bundle-version=\"1.3.0\"");
        return probe;
    }
}
