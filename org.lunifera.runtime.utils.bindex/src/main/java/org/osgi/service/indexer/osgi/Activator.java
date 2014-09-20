package org.osgi.service.indexer.osgi;

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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.indexer.ResourceIndexer;
import org.osgi.service.indexer.impl.RepoIndex;

public class Activator implements BundleActivator {

	private LogTracker logTracker;
	private AnalyzerTracker analyzerTracker;

	private ServiceRegistration<?> registration;

	public void start(BundleContext context) throws Exception {
		logTracker = new LogTracker(context);
		logTracker.open();

		RepoIndex indexer = new RepoIndex(logTracker);

		analyzerTracker = new AnalyzerTracker(context, indexer, logTracker);
		analyzerTracker.open();

		registration = context.registerService(ResourceIndexer.class.getName(), indexer, null);
	}

	public void stop(BundleContext context) throws Exception {
		registration.unregister();
		analyzerTracker.close();
		logTracker.close();
	}

}
