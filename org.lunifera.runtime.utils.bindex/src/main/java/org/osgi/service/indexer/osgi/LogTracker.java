package org.osgi.service.indexer.osgi;

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

import java.io.PrintStream;
import java.util.Date;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

class LogTracker extends ServiceTracker<LogService, LogService> implements
        LogService {

    public LogTracker(BundleContext context) {
        super(context, LogService.class.getName(), null);
    }

    public void log(int level, String message) {
        log(null, level, message, null);
    }

    public void log(int level, String message, Throwable exception) {
        log(null, level, message, exception);
    }

    @SuppressWarnings("rawtypes")
    public void log(ServiceReference sr, int level, String message) {
        log(sr, level, message, null);
    }

    @SuppressWarnings("rawtypes")
    public void log(ServiceReference sr, int level, String message,
            Throwable exception) {
        LogService log = (LogService) getService();

        if (log != null)
            log.log(sr, level, message, exception);
        else {
            PrintStream stream = (level <= LogService.LOG_WARNING) ? System.err
                    : System.out;
            if (message == null)
                message = "";
            Date now = new Date();
            stream.println(String.format("[%-7s] %tF %tT: %s",
                    LogUtils.formatLogLevel(level), now, now, message));
            if (exception != null)
                exception.printStackTrace(stream);
        }
    }

}
