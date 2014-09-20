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

import java.io.File;
import java.util.List;
import java.util.jar.Manifest;

import junit.framework.TestCase;

import org.osgi.service.indexer.Resource;

public class TestJarResource extends TestCase {

	public void testJarName() throws Exception {
		JarResource resource = new JarResource(new File(getClass()
                .getResource("/testdata/01-bsn+version.jar").getPath()));
		String location = resource.getLocation();
		assertEquals("testdata/01-bsn+version.jar", location.substring(location.length()-27));
	}

	public void testJarSize() throws Exception {
		JarResource resource = new JarResource(new File(getClass()
                .getResource("/testdata/01-bsn+version.jar").getPath()));
		assertEquals(1104L, resource.getSize());
	}

	public void testJarListing() throws Exception {
		JarResource resource = new JarResource(new File(getClass()
                .getResource("/testdata/01-bsn+version.jar").getPath()));
		List<String> children = resource.listChildren("org/example/a/");
		assertEquals(2, children.size());
		assertEquals("A.class", children.get(0));
		assertEquals("packageinfo", children.get(1));
	}

	public void testJarListingInvalidPaths() throws Exception {
		JarResource resource = new JarResource(new File(getClass()
                .getResource("/testdata/01-bsn+version.jar").getPath()));
		assertNull(resource.listChildren("org/wibble/"));
		assertNull(resource.listChildren("org/example/a"));
	}

	public void testJarListingRoot() throws Exception {
		JarResource resource = new JarResource(new File(getClass()
                .getResource("/testdata/org.eclipse.osgi_3.7.2.v20120110-1415.jar").getPath()));
		List<String> children = resource.listChildren("");
		assertEquals(21, children.size());
		assertEquals("META-INF/", children.get(0));
	}

	public void testJarFileContent() throws Exception {
		JarResource resource = new JarResource(new File(getClass()
                .getResource("/testdata/01-bsn+version.jar").getPath()));
		Resource pkgInfoResource = resource.getChild("org/example/a/packageinfo");

		assertEquals("version 1.0", Utils.readStream(pkgInfoResource.getStream()));
	}

	public void testJarManifest() throws Exception {
		JarResource resource = new JarResource(new File(getClass()
                .getResource("/testdata/01-bsn+version.jar").getPath()));
		Manifest manifest = resource.getManifest();
		assertEquals("org.example.a", manifest.getMainAttributes().getValue("Bundle-SymbolicName"));
	}

}
