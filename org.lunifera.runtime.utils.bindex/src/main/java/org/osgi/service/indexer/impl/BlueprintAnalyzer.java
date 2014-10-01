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
import java.util.List;

import org.osgi.service.indexer.Builder;
import org.osgi.service.indexer.Capability;
import org.osgi.service.indexer.Namespaces;
import org.osgi.service.indexer.Requirement;
import org.osgi.service.indexer.Resource;
import org.osgi.service.indexer.ResourceAnalyzer;
import org.osgi.service.log.LogService;

public class BlueprintAnalyzer implements ResourceAnalyzer {

	private static final String BUNDLE_BLUEPRINT_HEADER = "BUNDLE-Blueprint";

	@SuppressWarnings("unused")
	private LogService log;

	public BlueprintAnalyzer(LogService log) {
		this.log = log;
	}

	public void analyzeResource(Resource resource, List<Capability> capabilities, List<Requirement> requirements) throws Exception {
		boolean blueprintEnabled = false;

		String header = resource.getManifest().getMainAttributes().getValue(BUNDLE_BLUEPRINT_HEADER);
		if (header != null) {
			blueprintEnabled = true;
		} else {
			List<String> children = resource.listChildren("OSGI-INF/blueprint/");
			if (children != null) {
				for (String child : children) {
					if (child.toLowerCase().endsWith(".xml")) {
						blueprintEnabled = true;
						break;
					}
				}
			}
		}

		if (blueprintEnabled)
			requirements.add(createRequirement());
	}

	private Requirement createRequirement() {
		Builder builder = new Builder().setNamespace(Namespaces.NS_EXTENDER);
		String filter = String.format("(&(%s=%s)(version>=1.0.0)(!(version>=2.0.0)))", Namespaces.NS_EXTENDER, Namespaces.EXTENDER_BLUEPRINT);
		builder.addDirective(Namespaces.DIRECTIVE_FILTER, filter).addDirective(Namespaces.DIRECTIVE_EFFECTIVE, Namespaces.EFFECTIVE_ACTIVE);
		return builder.buildRequirement();
	}

}
