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
 * and it is released under OSGi Specification License, Version 2.0
 */
public final class Schema {

    public static final Object XML_PROCESSING_INSTRUCTION = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
    public static final String NAMESPACE = "http://www.osgi.org/xmlns/repository/v1.0.0";
    public static final String NAMESPACE_PREFIX = "repo";

    public static final String ELEM_REPOSITORY = NAMESPACE_PREFIX + ':'
            + "repository";
    public static final String ELEM_RESOURCE = NAMESPACE_PREFIX + ':'
            + "resource";
    public static final String ELEM_CAPABILITY = NAMESPACE_PREFIX + ':'
            + "capability";
    public static final String ELEM_REQUIREMENT = NAMESPACE_PREFIX + ':'
            + "requirement";
    public static final String ELEM_ATTRIBUTE = NAMESPACE_PREFIX + ':'
            + "attribute";
    public static final String ELEM_DIRECTIVE = NAMESPACE_PREFIX + ':'
            + "directive";

    public static final String ATTR_XML_NAMESPACE = "xmlns" + ':'
            + NAMESPACE_PREFIX;
    public static final String ATTR_NAME = "name";
    public static final String ATTR_INCREMENT = "increment";
    public static final String ATTR_NAMESPACE = "namespace";
    public static final String ATTR_TYPE = "type";
    public static final String ATTR_VALUE = "value";

    public static final String TYPE_VERSION = "Version";

}
