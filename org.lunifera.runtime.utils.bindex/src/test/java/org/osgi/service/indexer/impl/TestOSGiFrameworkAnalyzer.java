package org.osgi.service.indexer.impl;

/*
 * #%L
 * Lunifera Runtime Utilities - OSGi Repository Indexer
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

import static org.osgi.service.indexer.impl.Utils.findCaps;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.osgi.framework.Version;
import org.osgi.service.indexer.Capability;
import org.osgi.service.indexer.Requirement;

public class TestOSGiFrameworkAnalyzer extends TestCase {

    public void testOsgiFramework() throws Exception {
        LinkedList<Capability> caps = new LinkedList<Capability>();
        LinkedList<Requirement> reqs = new LinkedList<Requirement>();

        JarResource input = new JarResource(new File(getClass().getResource(
                "/testdata/org.apache.felix.framework-4.0.2.jar").getPath()));
        new BundleAnalyzer(new NullLogSvc()).analyzeResource(input, caps, reqs);
        new OSGiFrameworkAnalyzer(new NullLogSvc()).analyzeResource(input,
                caps, reqs);

        List<Capability> fwkCaps = findCaps("osgi.contract", caps);
        assertNotNull(fwkCaps);
        assertEquals(1, fwkCaps.size());
        Capability fwkCap = fwkCaps.get(0);

        assertEquals("OSGiFramework",
                fwkCap.getAttributes().get("osgi.contract"));
        assertEquals(new Version("4.3.0"), fwkCap.getAttributes()
                .get("version"));
        assertEquals(
                "org.osgi.framework.startlevel,org.osgi.framework.wiring,org.osgi.framework.hooks.bundle,org.osgi.framework.hooks.service,org.osgi.framework.hooks.resolver,org.osgi.framework.launch,org.osgi.framework,org.osgi.framework.hooks.weaving,org.osgi.service.packageadmin,org.osgi.service.url,org.osgi.service.startlevel,org.osgi.util.tracker",
                fwkCap.getDirectives().get("uses"));
    }

    public void testOsgiFrameworkSpecificationVersions() throws Exception {
        LinkedList<Capability> caps;
        LinkedList<Requirement> reqs;

        caps = new LinkedList<Capability>();
        reqs = new LinkedList<Requirement>();
        JarResource inputResource = new JarResource(new File(getClass()
                .getResource("/testdata/org.apache.felix.framework-4.0.2.jar")
                .getPath()));
        new BundleAnalyzer(new NullLogSvc()).analyzeResource(inputResource,
                caps, reqs);
        new OSGiFrameworkAnalyzer(new NullLogSvc()).analyzeResource(
                inputResource, caps, reqs);
        assertEquals(
                new Version("4.3.0"),
                findCaps("osgi.contract", caps).get(0).getAttributes()
                        .get("version"));

        caps = new LinkedList<Capability>();
        reqs = new LinkedList<Requirement>();
        inputResource = new JarResource(new File(getClass().getResource(
                "/testdata/org.eclipse.osgi_3.7.2.v20120110-1415.jar")
                .getPath()));
        new BundleAnalyzer(new NullLogSvc()).analyzeResource(inputResource,
                caps, reqs);
        new OSGiFrameworkAnalyzer(new NullLogSvc()).analyzeResource(
                inputResource, caps, reqs);
        assertEquals(
                new Version("4.3.0"),
                findCaps("osgi.contract", caps).get(0).getAttributes()
                        .get("version"));

        caps = new LinkedList<Capability>();
        reqs = new LinkedList<Requirement>();
        inputResource = new JarResource(new File(getClass().getResource(
                "/testdata/org.apache.felix.framework-3.2.2.jar").getPath()));
        new BundleAnalyzer(new NullLogSvc()).analyzeResource(inputResource,
                caps, reqs);
        new OSGiFrameworkAnalyzer(new NullLogSvc()).analyzeResource(
                inputResource, caps, reqs);
        assertEquals(
                new Version("4.2.0"),
                findCaps("osgi.contract", caps).get(0).getAttributes()
                        .get("version"));

        caps = new LinkedList<Capability>();
        reqs = new LinkedList<Requirement>();
        inputResource = new JarResource(new File(getClass().getResource(
                "/testdata/org.eclipse.osgi_3.6.2.R36x_v20110210.jar")
                .getPath()));
        new BundleAnalyzer(new NullLogSvc()).analyzeResource(inputResource,
                caps, reqs);
        new OSGiFrameworkAnalyzer(new NullLogSvc()).analyzeResource(
                inputResource, caps, reqs);
        assertEquals(
                new Version("4.2.0"),
                findCaps("osgi.contract", caps).get(0).getAttributes()
                        .get("version"));

    }

    public void testNonOsgiFramework() throws Exception {
        OSGiFrameworkAnalyzer a = new OSGiFrameworkAnalyzer(new NullLogSvc());
        LinkedList<Capability> caps = new LinkedList<Capability>();
        LinkedList<Requirement> reqs = new LinkedList<Requirement>();

        a.analyzeResource(
                new JarResource(new File(getClass().getResource(
                        "/testdata/03-export.jar").getPath())), caps, reqs);

        List<Capability> fwkCaps = findCaps("osgi.contract", caps);
        assertNotNull(fwkCaps);
        assertEquals(0, fwkCaps.size());
    }
}
