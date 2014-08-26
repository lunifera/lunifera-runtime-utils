package org.lunifera.runtime.utils.osgi.logging;

/*
 * #%L
 * An add-on to manage logging reports.
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

import java.util.concurrent.atomic.AtomicReference;

import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.slf4j.Marker;

/**
 * This factory service component will provide one instance of the logger for
 * each bundle using it.
 * 
 * @since 0.0.1
 * @author Cristiano Gavião
 */
@Component(servicefactory = true, service = { Logger.class })
public class ComponentServiceFactoryLogger implements Logger {

    private ComponentContext componentContext;

    private Long componentId;
    private final AtomicReference<ILoggerFactory> loggerFactoryRef = new AtomicReference<>();
    private String loggerName = null;
    private final AtomicReference<Logger> loggerRef = new AtomicReference<>();

    @Activate
    protected void activate(ComponentContext ctx) {
        this.componentContext = ctx;
        componentId = (Long) ctx.getProperties().get(
                ComponentConstants.COMPONENT_ID);
        loggerName = (String) ctx.getProperties().get(
                ConstantsLogging.LOGGER_NAME);
        if (loggerName == null || loggerName.isEmpty()) {
            if (ctx.getUsingBundle() != null) {
                loggerName = ctx.getUsingBundle().getSymbolicName();
            } else {
                loggerName = "noname";
            }
        }
        loggerRef.set(loggerFactoryRef.get().getLogger(loggerName));

        getLogger().debug("A Logger service ({}) was activated for component '{}'.",
                loggerName, componentId);
    }

    @Reference(policy = ReferencePolicy.STATIC)
    protected void bindILoggerFactory(ILoggerFactory loggerFactory) {
        this.loggerFactoryRef.set(loggerFactory);
    }

    @Deactivate
    protected void deactivate(ComponentContext ctx) {
        getLogger().debug("Logger for component '{}' was deactivated.({})",
                loggerName, componentId);
    }

    @Override
    public void debug(Marker marker, String msg) {
        setBundleMDC();
        getLogger().debug(marker, msg);
        unsetBundleMDC();
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        setBundleMDC();
        getLogger().debug(marker, format, arg);
        unsetBundleMDC();
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        setBundleMDC();
        getLogger().debug(marker, format, arguments);
        unsetBundleMDC();
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        setBundleMDC();
        getLogger().debug(marker, format, arg1, arg2);
        unsetBundleMDC();
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        setBundleMDC();
        getLogger().debug(marker, msg, t);
        unsetBundleMDC();
    }

    @Override
    public void debug(String msg) {
        setBundleMDC();
        getLogger().debug(msg);
        unsetBundleMDC();
    }

    @Override
    public void debug(String format, Object arg) {
        setBundleMDC();
        getLogger().debug(format, arg);
        unsetBundleMDC();
    }

    @Override
    public void debug(String format, Object... arguments) {
        setBundleMDC();
        getLogger().debug(format, arguments);
        unsetBundleMDC();
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        setBundleMDC();
        getLogger().debug(format, arg1, arg2);
        unsetBundleMDC();
    }

    @Override
    public void debug(String msg, Throwable t) {
        setBundleMDC();
        getLogger().debug(msg, t);
        unsetBundleMDC();
    }

    @Override
    public void error(Marker marker, String msg) {
        setBundleMDC();
        getLogger().error(marker, msg);
        unsetBundleMDC();
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        setBundleMDC();
        getLogger().error(marker, format, arg);
        unsetBundleMDC();
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        setBundleMDC();
        getLogger().error(marker, format, arguments);
        unsetBundleMDC();
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        setBundleMDC();
        getLogger().error(marker, format, arg1, arg2);
        unsetBundleMDC();
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        setBundleMDC();
        getLogger().error(marker, msg, t);
        unsetBundleMDC();
    }

    @Override
    public void error(String msg) {
        setBundleMDC();
        getLogger().error(msg);
        unsetBundleMDC();
    }

    @Override
    public void error(String format, Object arg) {
        setBundleMDC();
        getLogger().error(format, arg);
        unsetBundleMDC();
    }

    @Override
    public void error(String format, Object... arguments) {
        setBundleMDC();
        getLogger().error(format, arguments);
        unsetBundleMDC();
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        setBundleMDC();
        getLogger().error(format, arg1, arg2);
        unsetBundleMDC();
    }

    @Override
    public void error(String msg, Throwable t) {
        setBundleMDC();
        getLogger().error(msg, t);
        unsetBundleMDC();
    }

    private Logger getLogger() {
        return loggerRef.get();
    }

    @Override
    public String getName() {
        return getLogger().getName();
    }

    @Override
    public void info(Marker marker, String msg) {
        setBundleMDC();
        getLogger().info(marker, msg);
        unsetBundleMDC();
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        setBundleMDC();
        getLogger().info(marker, format, arg);
        unsetBundleMDC();
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        setBundleMDC();
        getLogger().info(marker, format, arguments);
        unsetBundleMDC();
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        setBundleMDC();
        getLogger().info(marker, format, arg1, arg2);
        unsetBundleMDC();
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        setBundleMDC();
        getLogger().info(marker, msg, t);
        unsetBundleMDC();
    }

    @Override
    public void info(String msg) {
        setBundleMDC();
        getLogger().info(msg);
        unsetBundleMDC();
    }

    @Override
    public void info(String format, Object arg) {
        setBundleMDC();
        getLogger().info(format, arg);
        unsetBundleMDC();
    }

    @Override
    public void info(String format, Object... arguments) {
        setBundleMDC();
        getLogger().info(format, arguments);
        unsetBundleMDC();
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        setBundleMDC();
        getLogger().info(format, arg1, arg2);
        unsetBundleMDC();
    }

    @Override
    public void info(String msg, Throwable t) {
        setBundleMDC();
        getLogger().info(msg, t);
        unsetBundleMDC();
    }

    @Override
    public boolean isDebugEnabled() {
        return getLogger().isDebugEnabled();
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return getLogger().isDebugEnabled(marker);
    }

    @Override
    public boolean isErrorEnabled() {
        return getLogger().isErrorEnabled();
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return getLogger().isErrorEnabled(marker);
    }

    @Override
    public boolean isInfoEnabled() {
        return getLogger().isInfoEnabled();
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return getLogger().isInfoEnabled(marker);
    }

    @Override
    public boolean isTraceEnabled() {
        return getLogger().isTraceEnabled();
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return getLogger().isTraceEnabled(marker);
    }

    @Override
    public boolean isWarnEnabled() {
        return getLogger().isWarnEnabled();
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return getLogger().isWarnEnabled(marker);
    }

    private void setBundleMDC() {
        if (componentContext != null
                && componentContext.getUsingBundle() != null) {
            MDC.put("bundle.name", componentContext.getUsingBundle()
                    .getSymbolicName());
            MDC.put("bundle.version", componentContext.getUsingBundle()
                    .getVersion().toString());
        }
    }

    @Override
    public void trace(Marker marker, String msg) {
        setBundleMDC();
        getLogger().trace(marker, msg);
        unsetBundleMDC();
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        setBundleMDC();
        getLogger().trace(marker, format, arg);
        unsetBundleMDC();
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        setBundleMDC();
        getLogger().trace(marker, format, argArray);
        unsetBundleMDC();
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        setBundleMDC();
        getLogger().trace(marker, format, arg1, arg2);
        unsetBundleMDC();
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        setBundleMDC();
        getLogger().trace(marker, msg, t);
        unsetBundleMDC();
    }

    @Override
    public void trace(String msg) {
        setBundleMDC();
        getLogger().trace(msg);
        unsetBundleMDC();
    }

    @Override
    public void trace(String format, Object arg) {
        setBundleMDC();
        getLogger().trace(format, arg);
        unsetBundleMDC();
    }

    @Override
    public void trace(String format, Object... arguments) {
        setBundleMDC();
        getLogger().trace(format, arguments);
        unsetBundleMDC();
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        setBundleMDC();
        getLogger().trace(format, arg1, arg2);
        unsetBundleMDC();
    }

    @Override
    public void trace(String msg, Throwable t) {
        setBundleMDC();
        getLogger().trace(msg, t);
        unsetBundleMDC();
    }

    protected void unbindILoggerFactory(ILoggerFactory loggerFactory) {
        this.loggerFactoryRef.compareAndSet(loggerFactory, null);
    }

    private void unsetBundleMDC() {
        MDC.remove("bundle.name");
        MDC.remove("bundle.version");
    }

    @Override
    public void warn(Marker marker, String msg) {
        setBundleMDC();
        getLogger().warn(marker, msg);
        unsetBundleMDC();
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        setBundleMDC();
        getLogger().warn(marker, format, arg);
        unsetBundleMDC();
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        setBundleMDC();
        getLogger().warn(marker, format, arguments);
        unsetBundleMDC();
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        setBundleMDC();
        getLogger().warn(marker, format, arg1, arg2);
        unsetBundleMDC();
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        setBundleMDC();
        getLogger().warn(marker, msg, t);
        unsetBundleMDC();
    }

    @Override
    public void warn(String msg) {
        setBundleMDC();
        getLogger().warn(msg);
        unsetBundleMDC();
    }

    @Override
    public void warn(String format, Object arg) {
        setBundleMDC();
        getLogger().warn(format, arg);
        unsetBundleMDC();
    }

    @Override
    public void warn(String format, Object... arguments) {
        setBundleMDC();
        getLogger().warn(format, arguments);
        unsetBundleMDC();
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        setBundleMDC();
        getLogger().warn(format, arg1, arg2);
        unsetBundleMDC();
    }

    @Override
    public void warn(String msg, Throwable t) {
        setBundleMDC();
        getLogger().warn(msg, t);
        unsetBundleMDC();
    }
}
