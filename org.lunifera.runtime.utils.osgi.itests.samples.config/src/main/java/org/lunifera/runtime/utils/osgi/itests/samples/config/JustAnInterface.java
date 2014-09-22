package org.lunifera.runtime.utils.osgi.itests.samples.config;

/*
 * #%L
 * org.lunifera.runtime.utils.osgi.itests.samples.config
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

import java.util.Dictionary;

/**
 * @since 0.0.1
 * @author Cristiano Gavião
 * 
 */
public interface JustAnInterface {

    String getServiceID();

    Dictionary<String, Object> getPropertiesFromComponent();
}
