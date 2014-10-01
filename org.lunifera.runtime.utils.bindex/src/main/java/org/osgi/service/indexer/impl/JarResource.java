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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.osgi.service.indexer.Resource;

public class JarResource implements Resource {

	private final File file;
	private final JarFile jarFile;
	private final String location;

	private final Dictionary<String, Object> properties = new Hashtable<String, Object>();

	private final Map<String, List<JarEntry>> prefixMap = new HashMap<String, List<JarEntry>>();
	private final Map<String, JarEntry> paths = new HashMap<String, JarEntry>();

	private Manifest manifest;

	public JarResource(File file) throws IOException {
		this.file = file;

		this.location = file.getPath();
		this.jarFile = new JarFile(file);

		properties.put(NAME, file.getName());
		properties.put(LOCATION, location);
		properties.put(SIZE, file.length());
		properties.put(LAST_MODIFIED, file.lastModified());

		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();

			String path = entry.getName();
			paths.put(path, entry);

			String parentPath = getParentPath(path);

			List<JarEntry> list = getOrCreatePrefix(parentPath);
			list.add(entry);
		}
	}

	private static String getParentPath(String path) {
		int index;
		if (path.endsWith("/")) {
			index = path.substring(0, path.length() - 1).lastIndexOf("/");
		} else {
			index = path.lastIndexOf("/");
		}

		String parentPath;
		if (index == -1)
			parentPath = "";
		else
			parentPath = path.substring(0, index + 1);
		return parentPath;
	}

	private synchronized List<JarEntry> getOrCreatePrefix(String prefix) {
		List<JarEntry> list = prefixMap.get(prefix);
		if (list == null) {
			list = new LinkedList<JarEntry>();
			prefixMap.put(prefix, list);
		}
		return list;
	}

	public String getLocation() {
		return location;
	}

	public Dictionary<String, Object> getProperties() {
		return properties;
	}

	public long getSize() {
		return file.length();
	}

	public InputStream getStream() throws IOException {
		return new FileInputStream(file);
	}

	public Manifest getManifest() throws IOException {
		synchronized (this) {
			if (manifest == null) {
				Resource manifestResource = getChild("META-INF/MANIFEST.MF");
				if (manifestResource != null) {
					try {
						manifest = new Manifest(manifestResource.getStream());
					} finally {
						manifestResource.close();
					}
				}
			}
			return manifest;
		}
	}

	public List<String> listChildren(String prefix) throws IOException {
		List<JarEntry> entries = prefixMap.get(prefix);
		if (entries == null)
			return null;

		List<String> result = new ArrayList<String>(entries.size());
		for (JarEntry entry : entries) {
			String unprefixedPath = entry.getName().substring(prefix.length());
			result.add(unprefixedPath);
		}
		return result;
	}

	public Resource getChild(String path) throws IOException {
		String childLocation = getLocation() + "#" + path;

		JarEntry entry = paths.get(path);
		if (entry == null)
			return null;
		return new FlatStreamResource(path, childLocation, jarFile.getInputStream(entry));
	}

	public void close() {
		try {
			jarFile.close();
		} catch (IOException e) {
			// Don't care
		}
	}

}
