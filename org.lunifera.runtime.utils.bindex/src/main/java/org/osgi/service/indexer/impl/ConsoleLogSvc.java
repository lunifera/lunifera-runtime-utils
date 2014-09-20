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
import java.io.PrintStream;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

@SuppressWarnings("rawtypes")
public class ConsoleLogSvc implements LogService {

	public void log(int level, String message) {
		log(null, level, message, null);
	}

	public void log(int level, String message, Throwable exception) {
		log(null, level, message, exception);
	}

    public void log(ServiceReference sr, int level, String message) {
		log(sr, level, message, null);
	}

	public void log(ServiceReference sr, int level, String message, Throwable exception) {
		PrintStream out = level <= LOG_WARNING ? System.err : System.out;

		StringBuilder builder = new StringBuilder();
		switch (level) {
		case LOG_DEBUG:
			builder.append("DEBUG");
			break;
		case LOG_INFO:
			builder.append("INFO");
			break;
		case LOG_WARNING:
			builder.append("WARNING");
			break;
		case LOG_ERROR:
			builder.append("ERROR");
			break;
		default:
			builder.append("<<unknown>>");
		}
		builder.append(": ");
		builder.append(message);

		if (exception != null) {
			builder.append(" [");
			builder.append(exception.getLocalizedMessage());
			builder.append("]");
		}

		out.println(builder.toString());

		if (exception != null)
			exception.printStackTrace(out);
	}

}
