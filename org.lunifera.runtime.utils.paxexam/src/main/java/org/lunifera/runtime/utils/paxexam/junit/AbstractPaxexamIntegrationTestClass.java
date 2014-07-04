package org.lunifera.runtime.utils.paxexam.junit;

/*
 * #%L
 * Lunifera Subsystems - Runtime Utilities for PaxExam integration tests.
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

import javax.inject.Inject;

import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.osgi.framework.BundleContext;

@RunWith(PaxExam.class)
public class AbstractPaxexamIntegrationTestClass {

	/**
	 * Injected BundleContext
	 */
	@Inject
	protected BundleContext bc;

	/**
	 * The correct way to extend the bundles to be installed is children to call
	 * this method directly as in the example below:
	 * <p>
	 * 
	 * <code>
	 * ...
	 * return super.extraBundles().add(<i>myBundles()</i>); <code>
	 * 
	 * @return
	 */
	protected DefaultCompositeOption extraBundles() {
		DefaultCompositeOption options = new DefaultCompositeOption();
		return options;
	}
}
