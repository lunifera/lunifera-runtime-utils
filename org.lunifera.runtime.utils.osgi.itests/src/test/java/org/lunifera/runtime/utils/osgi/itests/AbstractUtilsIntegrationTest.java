package org.lunifera.runtime.utils.osgi.itests;

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

import static org.knowhowlab.osgi.testing.assertions.BundleAssert.assertBundleAvailable;
import static org.knowhowlab.osgi.testing.assertions.ServiceAssert.assertServiceAvailable;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.knowhowlab.osgi.testing.assertions.BundleAssert;
import org.knowhowlab.osgi.testing.assertions.OSGiAssert;
import org.knowhowlab.osgi.testing.assertions.ServiceAssert;
import org.knowhowlab.osgi.testing.assertions.cmpn.ConfigurationAdminAssert;
import org.knowhowlab.osgi.testing.utils.ServiceUtils;
import org.lunifera.runtime.utils.paxexam.PaxexamDefaultOptions;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.Version;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public abstract class AbstractUtilsIntegrationTest {
    /**
     * Injected BundleContext
     */
    @Inject
    protected BundleContext bc;

    @Configuration
    public Option[] configureIntegrationTest() {
        return PaxexamDefaultOptions.basicNodeConfiguration(true,
                bundlesToBeInstalled());
    }

    public void ensureRequiredBundlesAreAvailable() {
        assertBundleAvailable("org.knowhowlab.osgi.testing.utils", new Version(
                "1.2.2"));
        assertBundleAvailable("slf4j.api");
    }

    public void ensureRequiredServicesAreAvailable() {

        ServiceUtils.waitForServiceEvent(bc, "org.apache.felix.scr.ScrService",
                ServiceEvent.REGISTERED, 5000);
        ServiceUtils.waitForServiceEvent(bc,
                "org.osgi.service.cm.ConfigurationAdmin",
                ServiceEvent.REGISTERED, 5000);

        // assert OSGi CM service is available
        assertServiceAvailable("CM is not available",
                "org.osgi.service.cm.ConfigurationAdmin", 15000);
        // assert OSGi DS service is available
        assertServiceAvailable("DS is not available",
                "org.apache.felix.scr.ScrService", 5000);
        // assert OSGi Log service is available
        assertServiceAvailable("LogService is not available",
                "org.osgi.service.log.LogService");
    }

    /**
     * Points to the bundles to be installed in the test container.
     * <p>
     * The correct way to extend the bundles to be installed is children to call
     * this method directly as in the example below:
     * <p>
     * 
     * <code>
     * ...
     * return super.bundlesToBeInstalled().add(<i>myBundles()</i>); <code>
     * 
     * @return
     */
    protected DefaultCompositeOption bundlesToBeInstalled() {
        DefaultCompositeOption options = new DefaultCompositeOption();
        return options;
    }

    @Before
    public void initAssertTools() throws Exception {
        System.out.println("Setting up the AssertionTools");
        OSGiAssert.setDefaultBundleContext(bc);
        ServiceAssert.setDefaultBundleContext(bc);
        BundleAssert.setDefaultBundleContext(bc);
        ConfigurationAdminAssert.setDefaultBundleContext(bc);

    }
}
