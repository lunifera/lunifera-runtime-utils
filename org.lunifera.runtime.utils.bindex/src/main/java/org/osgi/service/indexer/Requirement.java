package org.osgi.service.indexer;

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
import java.util.Collections;
import java.util.Map;

/**
 * A requirement
 */
public final class Requirement {
	/** the namespace */
	private final String namespace;

	/** the attributes */
	private final Map<String, Object> attributes;

	/** the directives */
	private final Map<String, String> directives;

	/**
	 * Constructor
	 * 
	 * @param namespace
	 *            the namespace
	 * @param attributes
	 *            the attributes
	 * @param directives
	 *            the directives
	 */
	Requirement(String namespace, Map<String, Object> attributes, Map<String, String> directives) {
		this.namespace = namespace;
		this.attributes = attributes;
		this.directives = directives;
	}

	/**
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * @return the attributes
	 */
	public Map<String, Object> getAttributes() {
		return Collections.unmodifiableMap(attributes);
	}

	/**
	 * @return the directives
	 */
	public Map<String, String> getDirectives() {
		return Collections.unmodifiableMap(directives);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("REQUIREMENT [namespace=").append(namespace).append(", attributes=").append(attributes).append(", directives=").append(directives).append("]");
		return builder.toString();
	}
}
