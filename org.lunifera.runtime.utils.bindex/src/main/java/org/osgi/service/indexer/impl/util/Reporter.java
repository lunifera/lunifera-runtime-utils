package org.osgi.service.indexer.impl.util;

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

import java.util.List;

public interface Reporter {
	void error(String s, Object... args);

	void warning(String s, Object... args);

	void progress(String s, Object... args);

	void trace(String s, Object... args);

	List<String> getWarnings();

	List<String> getErrors();

	boolean isPedantic();
}
