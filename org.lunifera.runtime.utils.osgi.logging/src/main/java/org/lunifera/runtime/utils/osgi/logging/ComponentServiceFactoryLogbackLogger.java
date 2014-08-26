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

import java.util.Dictionary;

import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.util.StatusPrinter;

@Component(enabled = true, immediate = true)
public class ComponentServiceFactoryLogbackLogger implements ILoggerFactory {

    private static org.slf4j.Logger logger = LoggerFactory
            .getLogger(ComponentServiceFactoryLogbackLogger.class);

    private Long componentId;
    private String componentName;
    private LoggerContext loggerContext = null;
    private Logger rootLogger = null;

    @Activate
    protected void activate(ComponentContext ctx) {
        setupLoggingEngine(ctx.getProperties());

        componentId = (Long) ctx.getProperties().get(
                ComponentConstants.COMPONENT_ID);
        componentName = (String) ctx.getProperties().get(
                ComponentConstants.COMPONENT_NAME);
        logger.info("Component '{}' ({}) was activated.", componentName,
                componentId);
    }

    @Deactivate
    protected void deactivate(ComponentContext ctx) {
        logger.info("Component '{}' ({}) was deactivated.", componentName,
                componentId);
        loggerContext.reset();
        componentId = null;
        componentName = null;
        logger = null;
        loggerContext = null;
        rootLogger = null;
    }

    public Logger getRootLogger(){
        if (rootLogger == null){
            rootLogger = getLoggerContext().getLogger(
                    org.slf4j.Logger.ROOT_LOGGER_NAME);
        }
        return rootLogger;
    }
    
    @Override
    public Logger getLogger(String name) {
        return getLoggerContext().getLogger(name);
    }

    private LoggerContext getLoggerContext() {
        if (loggerContext == null) {
            loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        }
        return loggerContext;
    }

    @Modified
    void modified(ComponentContext ctx) {
        setupLoggingEngine(ctx.getProperties());
    }

    public void setLogLevel(String logName, String level) {
        Logger loggerl = getLoggerContext().getLogger(logName);
        loggerl.setLevel(Level.toLevel(level));

    }

    public void setRootLogLevel(String level) {
        getRootLogger().setLevel(Level.toLevel(level));
    }

    private Appender<ILoggingEvent> setupConsoleAppender() {
        PatternLayoutEncoder ple = new PatternLayoutEncoder();
        ple.setPattern("%d{HH:mm:ss.SSS} [%M] %-5level %logger{36} - %msg%n");
        // ple.setPattern("%d{HH:mm:ss.SSS} [%X{bundle.name}.%X{bundle.version}] %-5level %logger{36} - %msg%n");
        ple.setContext(getLoggerContext());
        ple.start();
        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<ILoggingEvent>();
        consoleAppender.setName("STDOUT");
        consoleAppender.setEncoder(ple);
        consoleAppender.setContext(getLoggerContext());
        consoleAppender.setTarget("System.err");
        consoleAppender.start();

        return consoleAppender;
    }

    protected Appender<ILoggingEvent> setupFileAppender(String file) {

        PatternLayoutEncoder ple = new PatternLayoutEncoder();
        ple.setPattern("%date %level [%thread] %logger{10} [%file:%line] %msg%n");
        ple.setContext(getLoggerContext());
        ple.start();
        FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
        fileAppender.setName("FILE");
        fileAppender.setFile(file);
        fileAppender.setEncoder(ple);
        fileAppender.setContext(getLoggerContext());
        fileAppender.start();

        return fileAppender;
    }

    private void setupLoggingEngine(Dictionary<?, ?> properties) {
        String level = null;
        // reset any previous config
        getLoggerContext().reset();

        // properties
        level = (String) properties.get(ConstantsLogging.LOGGING_LEVEL);

        if (level == null || level.isEmpty()) {
            level = System.getProperty(ConstantsLogging.LOGGING_LEVEL);
        }
        if (level == null || level.isEmpty()) {
            level = "trace";
        }
       
        getRootLogger().addAppender(setupConsoleAppender());
        getRootLogger().setLevel(Level.toLevel(level));
        getLogger("org.apache.aries.subsystem").setLevel(Level.INFO);
    }

    // TODO integrate with CM in order to setup this kind of appenders...
    protected Appender<ILoggingEvent> setupRollingFileAppender(String file) {

        RollingFileAppender<ILoggingEvent> rfAppender = new RollingFileAppender<ILoggingEvent>();
        rfAppender.setContext(loggerContext);
        rfAppender.setFile(file);
        rfAppender.setName("RFILE");

        FixedWindowRollingPolicy rollingPolicy = new FixedWindowRollingPolicy();
        rollingPolicy.setContext(loggerContext);
        // rolling policies need to know their parent
        // it's one of the rare cases, where a sub-component knows about its
        // parent
        rollingPolicy.setParent(rfAppender);
        rollingPolicy.setFileNamePattern("testFile.%i.log.zip");
        rollingPolicy.start();

        SizeBasedTriggeringPolicy<ILoggingEvent> triggeringPolicy = new SizeBasedTriggeringPolicy<ILoggingEvent>();
        triggeringPolicy.setMaxFileSize("5MB");
        triggeringPolicy.start();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern("%-4relative [%thread] %-5level %logger{35} - %msg%n");
        encoder.start();

        rfAppender.setEncoder(encoder);
        rfAppender.setRollingPolicy(rollingPolicy);
        rfAppender.setTriggeringPolicy(triggeringPolicy);

        rfAppender.start();

        // attach the rolling file appender to the logger of your choice
        Logger logbackLogger = loggerContext.getLogger("Main");
        logbackLogger.addAppender(rfAppender);

        // OPTIONAL: print logback internal status messages
        StatusPrinter.print(loggerContext);

        return rfAppender;
    }

    // private void setupSiftingAppender() {
    // SiftingAppender siftingAppender;
    // }
}
