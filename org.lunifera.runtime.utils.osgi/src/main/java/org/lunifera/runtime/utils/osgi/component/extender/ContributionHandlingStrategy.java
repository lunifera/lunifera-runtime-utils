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

import static org.lunifera.runtime.utils.osgi.component.extender.ConstantsExtender.CONTRIBUTION_HANDLING_STRATEGY_PER_BUNDLE;
import static org.lunifera.runtime.utils.osgi.component.extender.ConstantsExtender.CONTRIBUTION_HANDLING_STRATEGY_PER_CATEGORY;
import static org.lunifera.runtime.utils.osgi.component.extender.ConstantsExtender.CONTRIBUTION_HANDLING_STRATEGY_PER_ITEM;

/**
 * Defines how a contribution bundle and its header items should be processed.
 * 
 * @author Cristiano Gavião
 * @since 0.0.1
 */
public enum ContributionHandlingStrategy {

    /**
     * Each item in the manifest header must be processed individually.
     */
    PER_ITEM(CONTRIBUTION_HANDLING_STRATEGY_PER_ITEM),

    /**
     * All header items must be processed as a whole and only one contribution
     * will be madden for the entire bundle.
     */
    PER_BUNDLE(CONTRIBUTION_HANDLING_STRATEGY_PER_BUNDLE),

    /**
     * Contribution will be categorized by a customized pattern. Each match of
     * that pattern must generate one contribution object.
     */
    PER_CATEGORY(CONTRIBUTION_HANDLING_STRATEGY_PER_CATEGORY);

    public static ContributionHandlingStrategy fromString(String ident) {
        if (ident != null) {
            for (ContributionHandlingStrategy b : ContributionHandlingStrategy
                    .values()) {
                if (ident.equalsIgnoreCase(b.ident)) {
                    return b;
                }
            }
        }
        return null;
    }

    private final String ident;

    ContributionHandlingStrategy(String ident) {
        this.ident = ident;
    }

    public String getIdent() {
        return ident;
    }
}
