package org.lunifera.runtime.utils.osgi.component.whiteboard;

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
 * 
 * @author Cristiano Gavião
 * @see {@link ServiceEvent}
 */
public enum ServiceTrackerEvent {
    REGISTERED(0x00000001), MODIFIED(0x00000002), UNREGISTERING(0x00000004), MODIFIED_ENDMATCH(
            0x00000008);

    private final int value;

    ServiceTrackerEvent(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
