package org.lunifera.runtime.utils.osgi.component.extender;

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

import java.util.regex.Pattern;

public interface ContributionItemConfigAdmin extends ContributionItem {

    String REGEX_TARGET_PID = "([a-zA-Z0-9\\.\\_]+)(\\|([a-zA-Z0-9\\.\\_]+)(\\|(\\d+(\\.\\d+(\\.\\d+(\\.[a-zA-Z0-9\\_]+)?)?)?)(\\|([a-zA-Z0-9\\.\\_]+))?)?)?(\\.config|\\.conf|\\.cfg)";

    Pattern PATTERN_TARGET_PID = Pattern.compile(REGEX_TARGET_PID);

    boolean usingFactoryConfiguration();

    String targetPID();
}
