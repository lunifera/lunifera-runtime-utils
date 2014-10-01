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
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.osgi.framework.Version;
import org.osgi.service.indexer.Builder;
import org.osgi.service.indexer.Capability;
import org.osgi.service.indexer.Requirement;
import org.osgi.service.indexer.Resource;
import org.osgi.service.indexer.ResourceAnalyzer;
import org.osgi.service.indexer.impl.types.SymbolicName;
import org.osgi.service.indexer.impl.types.VersionRange;
import org.osgi.service.indexer.impl.util.OSGiHeader;

public class KnownBundleAnalyzer implements ResourceAnalyzer {

	private final Properties defaultProperties;
	private Properties extraProperties = null;

	public KnownBundleAnalyzer() {
		defaultProperties = new Properties();
		InputStream stream = KnownBundleAnalyzer.class.getResourceAsStream("/known-bundles.properties");
		if (stream != null) {
			try {
				defaultProperties.load(stream);
			} catch (IOException e) {
				// ignore
			} finally {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public KnownBundleAnalyzer(Properties properties) {
		this.defaultProperties = properties;
	}

	public void setKnownBundlesExtra(Properties extras) {
		this.extraProperties = extras;
	}

	public void analyzeResource(Resource resource, List<Capability> caps, List<Requirement> reqs) throws Exception {
		SymbolicName resourceName;
		try {
			resourceName = Util.getSymbolicName(resource);
		} catch (IllegalArgumentException e) {
			// not a bundle, so return without analyzing
			return;
		}

		for (Enumeration<?> names = defaultProperties.propertyNames(); names.hasMoreElements();) {
			String propName = (String) names.nextElement();
			processPropertyName(resource, caps, reqs, resourceName, propName, defaultProperties);
		}

		if (extraProperties != null)
			for (Enumeration<?> names = extraProperties.propertyNames(); names.hasMoreElements();) {
				String propName = (String) names.nextElement();
				processPropertyName(resource, caps, reqs, resourceName, propName, extraProperties, defaultProperties);
			}
	}

	private static void processPropertyName(Resource resource, List<Capability> caps, List<Requirement> reqs, SymbolicName resourceName, String name, Properties... propertiesList)
			throws IOException {
		String[] bundleRef = name.split(";");
		String bsn = bundleRef[0];

		if (resourceName.getName().equals(bsn)) {
			VersionRange versionRange = null;
			if (bundleRef.length > 1)
				versionRange = new VersionRange(bundleRef[1]);

			Version version = Util.getVersion(resource);
			if (versionRange == null || versionRange.match(version)) {
				processClause(name, Util.readProcessedProperty(name, propertiesList), caps, reqs);
			}
		}
	}

	private static enum IndicatorType {
		CAPABILITY("cap="), REQUIREMENT("req=");

		String prefix;

		IndicatorType(String prefix) {
			this.prefix = prefix;
		}

		public String getPrefix() {
			return prefix;
		}
	}

	private static void processClause(String bundleRef, String clauseStr, List<Capability> caps, List<Requirement> reqs) {
		Map<String, Map<String, String>> header = OSGiHeader.parseHeader(clauseStr);

		for (Entry<String, Map<String, String>> entry : header.entrySet()) {
			String indicator = OSGiHeader.removeDuplicateMarker(entry.getKey());
			IndicatorType type;

			String namespace;
			if (indicator.startsWith(IndicatorType.CAPABILITY.getPrefix())) {
				type = IndicatorType.CAPABILITY;
				namespace = indicator.substring(IndicatorType.CAPABILITY.getPrefix().length());
			} else if (indicator.startsWith(IndicatorType.REQUIREMENT.getPrefix())) {
				type = IndicatorType.REQUIREMENT;
				namespace = indicator.substring(IndicatorType.REQUIREMENT.getPrefix().length());
			} else {
				throw new IllegalArgumentException(MessageFormat.format(
						"Invalid indicator format in known-bundle parsing for bundle  \"{0}\", expected cap=namespace or req=namespace, found \"{1}\".", bundleRef, indicator));
			}

			Builder builder = new Builder().setNamespace(namespace);

			Map<String, String> attribs = entry.getValue();
			Util.copyAttribsToBuilder(builder, attribs);

			if (type == IndicatorType.CAPABILITY)
				caps.add(builder.buildCapability());
			else if (type == IndicatorType.REQUIREMENT)
				reqs.add(builder.buildRequirement());
		}
	}

}
