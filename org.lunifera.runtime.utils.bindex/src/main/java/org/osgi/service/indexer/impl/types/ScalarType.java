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
public enum ScalarType {
    STRING("String"), VERSION("Version"), LONG("Long"), DOUBLE("Double");

    private String key;

    private ScalarType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static ScalarType fromString(String text) {
        if (text != null) {
            for (ScalarType b : ScalarType.values()) {
                if (text.equalsIgnoreCase(b.key)) {
                    return b;
                }
            }
        }
        return null;
    }
}
