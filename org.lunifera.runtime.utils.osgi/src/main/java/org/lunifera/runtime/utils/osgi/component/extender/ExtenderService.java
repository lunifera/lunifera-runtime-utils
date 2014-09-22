package org.lunifera.runtime.utils.osgi.component.extender;

import org.osgi.framework.Bundle;

/*
 * #%L
 * Lunifera Runtime Utilities - for OSGi
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
 * This interface are public methods of a DS extender component.
 * 
 * @author Cristiano Gavião
 * @since 0.0.1
 */
public interface ExtenderService {

    /**
     * The manifest header used to track for contributions.
     * 
     * @return the header name.
     */
    String getExtenderContributorManifestHeader();

    /**
    * Defines which bundle states should tracked.
    */
    int getStateMask();
    
    
    ContributorBundle whenContributorBundleIsActivated(
            Bundle contributorBundleInstance, String headerName, String headerValue);
}
