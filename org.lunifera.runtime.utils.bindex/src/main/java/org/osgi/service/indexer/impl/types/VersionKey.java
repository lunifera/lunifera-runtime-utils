package org.osgi.service.indexer.impl.types;

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
/*
 * Part of this code was borrowed from BIndex project (https://github.com/osgi/bindex) 
 * and it is released under OSGi Specification License, VERSION 2.0
 */
import org.osgi.framework.Constants;
import org.osgi.service.indexer.Namespaces;

public enum VersionKey {

	PACKAGEVERSION(Constants.VERSION_ATTRIBUTE), BUNDLEVERSION(Constants.BUNDLE_VERSION_ATTRIBUTE), NativeOsVersion(Namespaces.ATTR_NATIVE_OSVERSION);

	private String key;

	VersionKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
