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

import junit.framework.TestCase;

public class TestUtils extends TestCase {

	public void testFindPlainPath() throws Exception {
		JarResource jar = new JarResource(new File(getClass()
                .getResource("/testdata/org.eclipse.osgi_3.7.2.v20120110-1415.jar").getPath()));
		List<String> list = Util.findMatchingPaths(jar, "META-INF/services/org.osgi.framework.launch.FrameworkFactory");
		assertEquals(1, list.size());
		assertEquals("META-INF/services/org.osgi.framework.launch.FrameworkFactory", list.get(0));
	}

	public void testFindGlobPattern() throws Exception {
		JarResource jar = new JarResource(new File(getClass()
                .getResource("/testdata/org.eclipse.osgi_3.7.2.v20120110-1415.jar").getPath()));
		List<String> list = Util.findMatchingPaths(jar, "*.profile");

		assertEquals(12, list.size());
		assertEquals("CDC-1.0_Foundation-1.0.profile", list.get(0));
	}
}
