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
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A container for attributes and directives under a certain namespace. Can
 * generate a capability and/or a requirement from the contained information.
 */
public final class Builder {
    /** the namespace */
    private String namespace = null;

    /** the attributes */
    private final Map<String, Object> attributes = new LinkedHashMap<String, Object>();

    /** the directives */
    private final Map<String, String> directives = new LinkedHashMap<String, String>();

    /**
     * @param namespace
     *            the namespace to set
     * @return this
     */
    public Builder setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    /**
     * Add an attribute
     * 
     * @param name
     *            attribute name
     * @param value
     *            attribute value
     * @return this
     */
    public Builder addAttribute(String name, Object value) {
        attributes.put(name, value);
        return this;
    }

    /**
     * Add a directive
     * 
     * @param name
     *            directive name
     * @param value
     *            directive value
     * @return this
     */
    public Builder addDirective(String name, String value) {
        directives.put(name, value);
        return this;
    }

    /**
     * @return a new capability, constructed from the namespace, attributes and
     *         directives
     * @throws IllegalStateException
     *             when the namespace isn't set
     */
    public Capability buildCapability() throws IllegalStateException {
        if (namespace == null)
            throw new IllegalStateException("Namespace not set");

        return new Capability(namespace, new LinkedHashMap<String, Object>(
                attributes), new LinkedHashMap<String, String>(directives));
    }

    /**
     * @return a new requirement, constructed from the namespace, attributes and
     *         directives
     * @throws IllegalStateException
     *             when the namespace isn't set
     */
    public Requirement buildRequirement() throws IllegalStateException {
        if (namespace == null)
            throw new IllegalStateException("Namespace not set");

        return new Requirement(namespace, new LinkedHashMap<String, Object>(
                attributes), new LinkedHashMap<String, String>(directives));
    }
}
