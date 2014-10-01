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
/*
 * Part of this code was borrowed from BIndex project (https://github.com/osgi/bindex) 
 * and it is released under OSGi Specification License, VERSION 2.0
 */
import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.jar.Manifest;

import org.osgi.service.indexer.Resource;

class FlatStreamResource implements Resource {

	private final String location;
	private final InputStream stream;

	private final Dictionary<String, Object> properties = new Hashtable<String, Object>();

	FlatStreamResource(String name, String location, InputStream stream) {
		this.location = location;
		this.stream = stream;

		properties.put(NAME, name);
		properties.put(LOCATION, location);
	}

	public String getLocation() {
		return location;
	}

	public Dictionary<String, Object> getProperties() {
		return properties;
	}

	public long getSize() {
		return 0L;
	}

	public InputStream getStream() throws IOException {
		return stream;
	}

	public Manifest getManifest() throws IOException {
		return null;
	}

	public List<String> listChildren(String prefix) throws IOException {
		return null;
	}

	public Resource getChild(String path) throws IOException {
		return null;
	}

	public void close() {
	}

}
