package org.lunifera.runtime.utils.osgi.component.extender;

/*
 * #%L
 * Lunifera Subsystems - Runtime Utilities for OSGi
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

/**
 * Defines how a contribution bundle and its header items should be processed.
 * 
 * @author Cristiano Gavião
 * @since 0.0.1
 */
public enum ExtensionHandlingStrategy {

    /**
     * Each item in the manifest header must be processed individually.
     */
    PER_ITEM("per_item"),

    /**
     * All header items must be processed as a whole and only one contribution
     * will be madden for the entire bundle.
     */
    PER_BUNDLE("per_bundle"),

    /**
     * Contribution will be categorized by a customized pattern. Each match of
     * that pattern must generate one contribution object.
     */
    PER_CATEGORY("per_category");

    public static ExtensionHandlingStrategy fromString(String ident) {
        if (ident != null) {
            for (ExtensionHandlingStrategy b : ExtensionHandlingStrategy
                    .values()) {
                if (ident.equalsIgnoreCase(b.ident)) {
                    return b;
                }
            }
        }
        return null;
    }

    private final String ident;

    ExtensionHandlingStrategy(String ident) {
        this.ident = ident;
    }

    public String getIdent() {
        return ident;
    }
}
