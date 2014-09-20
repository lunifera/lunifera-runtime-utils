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

import java.util.Properties;

import junit.framework.TestCase;

public class TestMacro extends TestCase {

	public void testSimpleProperty() {
		Properties props = new Properties();
		props.setProperty("foo", "bar");

		assertEquals("bar", Util.readProcessedProperty("foo", props));
	}

	public void testMacroProperty() {
		Properties props = new Properties();
		props.setProperty("gnu", "GNU is not UNIX");
		props.setProperty("message", "The meaning of GNU is \"${gnu}\".");

		assertEquals("The meaning of GNU is \"GNU is not UNIX\".", Util.readProcessedProperty("message", props));
	}

	public void testMultiLevelPropertiesMacro() {
		Properties baseProps = new Properties();
		baseProps.setProperty("gnu", "GNU is not UNIX");

		Properties extensionProps = new Properties();
		extensionProps.put("message", "The meaning of GNU is \"${gnu}\".");

		assertEquals("The meaning of GNU is \"GNU is not UNIX\".", Util.readProcessedProperty("message", extensionProps, baseProps));
	}
}
