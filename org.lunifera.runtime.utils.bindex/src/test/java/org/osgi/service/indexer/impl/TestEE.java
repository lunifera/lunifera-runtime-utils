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

import junit.framework.TestCase;

public class TestEE extends TestCase {

	public void testSingleVersioned() {
		EE ee = EE.parseBREE("JavaSE-1.6");
		assertEquals("JavaSE", ee.getName());
		assertEquals("1.6", ee.getVersion().toString());
		assertEquals("(&(osgi.ee=JavaSE)(version=1.6))", ee.toFilter());
	}

	public void testBadVersion() {
		EE ee = EE.parseBREE("MyEE-badVersion");
		assertEquals("MyEE-badVersion", ee.getName());
		assertNull(ee.getVersion());
		assertEquals("(osgi.ee=MyEE-badVersion)", ee.toFilter());
	}

	public void testAlias1() {
		EE ee = EE.parseBREE("OSGi/Minimum-1.2");
		assertEquals("OSGi/Minimum", ee.getName());
		assertEquals("1.2", ee.getVersion());
		assertEquals("(&(osgi.ee=OSGi/Minimum)(version=1.2))", ee.toFilter());
	}

	public void testAlias2() {
		EE ee = EE.parseBREE("AA/BB-1.7");
		assertEquals("AA/BB", ee.getName());
		assertEquals("1.7", ee.getVersion());
		assertEquals("(&(osgi.ee=AA/BB)(version=1.7))", ee.toFilter());
	}

	public void testVersionedAlias() {
		EE ee = EE.parseBREE("CDC-1.0/Foundation-1.0");
		assertEquals("CDC/Foundation", ee.getName());
		assertEquals("1.0", ee.getVersion());
		assertEquals("(&(osgi.ee=CDC/Foundation)(version=1.0))", ee.toFilter());
	}

	public void testUnmatchedAliasVersions() {
		EE ee = EE.parseBREE("V1-1.5/V2-1.6");
		assertEquals("V1-1.5/V2-1.6", ee.getName());
		assertNull(ee.getVersion());
		assertEquals("(osgi.ee=V1-1.5/V2-1.6)", ee.toFilter());
	}

	public void testReplaceJ2SEWithJavaSE() {
		EE ee = EE.parseBREE("J2SE-1.4");
		assertEquals("JavaSE", ee.getName());
		assertEquals("1.4", ee.getVersion().toString());
		assertEquals("(&(osgi.ee=JavaSE)(version=1.4))", ee.toFilter());
	}
}
