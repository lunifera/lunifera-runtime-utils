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

import org.junit.Ignore;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

@Ignore
@SuppressWarnings("rawtypes")
public class NullLogSvc implements LogService {

	public void log(int level, String message) {
	}

	public void log(int level, String message, Throwable exception) {
	}

    public void log(ServiceReference sr, int level, String message) {
	}

	public void log(ServiceReference sr, int level, String message, Throwable exception) {
	}

}
