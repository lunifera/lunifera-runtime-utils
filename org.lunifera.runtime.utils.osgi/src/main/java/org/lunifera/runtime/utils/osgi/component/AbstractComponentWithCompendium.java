package org.lunifera.runtime.utils.osgi.component;

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

import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.lunifera.runtime.utils.osgi.services.PluggableEventTracker;
import org.lunifera.runtime.utils.osgi.services.PluggableServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;
import org.osgi.service.prefs.PreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

/**
 * An abstract class to be inherited by components that requires a logging, an
 * event admin, a preference and some others OSGi compendium services bounded at
 * activation time.
 * <p>
 * Unfortunately, OSGi DS Annotations don't support inheritance, so every DS
 * annotation must be used inside concrete children classes.
 * <p>
 * In order to facilitate developers life it was included in this abstract
 * component 3 methods (a bindX(), a getX() and a unbindX()). Therefore, in
 * order to use them children classes must override the correspondent bindX()
 * method and apply the proper @{@link Reference} annotation.
 * 
 * @author Cristiano Gavião
 * @since 0.0.1
 * 
 */
public abstract class AbstractComponentWithCompendium extends
        AbstractComponentBasic {

    public static final String PROPERTY_ENABLE_INNER_SERVICE_TRACKERS = "lunifera.enable.inner.service.trackers";

    /**
     * Holds an atomic reference of a {@link ConfigurationAdmin} service.
     */
    private final AtomicReference<ConfigurationAdmin> configAdminServiceRef = new AtomicReference<ConfigurationAdmin>();

    private boolean enableInnerServiceTrackers = false;

    /**
     * Holds an atomic reference of a {@link EventAdmin} service.
     */
    private final AtomicReference<EventAdmin> eventAdminServiceRef = new AtomicReference<EventAdmin>();

    /**
     * Holds a set of {@link PluggableEventTracker} objects.
     * <p>
     * Those events will be registered and unregistered following this
     * component's lifecycle.
     */
    private Set<PluggableEventTracker> eventTrackers = null;

    @SuppressWarnings("rawtypes")
    private Map<Class<?>, PluggableServiceTracker> innerPluggableServiceTrackers = null;

    private Logger logger;

    /**
     * Holds an atomic reference for an OSGi logging service {@link LogService}.
     * <p>
     * When a log service were not tied, then SLF4j {@link Logger} will be used
     * instead.
     */
    private final AtomicReference<LogService> logServiceRef = new AtomicReference<LogService>();

    /**
     * Holds an atomic reference of a {@link PreferencesService} service.
     */
    private final AtomicReference<PreferencesService> preferencesServiceRef = new AtomicReference<PreferencesService>();

    /**
     * DS needs a default constructor.
     */
    public AbstractComponentWithCompendium() {
    }

    /**
     * Constructor created just to help unit testing. It is not used by OSGi.
     * 
     * @param componentContext
     */
    @SuppressWarnings("rawtypes")
    protected AbstractComponentWithCompendium(BundleContext bundleContext,
            ComponentContext componentContext,
            Map<Class<?>, PluggableServiceTracker> serviceTrackers,
            Set<PluggableEventTracker> eventTrackers) {
        super(bundleContext, componentContext);
        this.eventTrackers = eventTrackers;
        this.innerPluggableServiceTrackers = serviceTrackers;
    }

    /**
     * Method used by Declarative Service engine to activate this component.
     * <p>
     * For concrete classes the activation process was divided in three parts
     * sequentially called. <br>
     * The first method to be called is {@link #doBeforeActivateComponent()},
     * then the main execution in {@link #doWhenActivateComponent()} and to
     * complete the activation process, {@link #doAfterActivateComponent()}.
     * <p>
     * This separation will help add-ons developers to facilitate the end users
     * life, creating abstract classes that implements the most important parts
     * in its before and after methods.
     */
    @Override
    protected final void activate(ComponentContext context)
            throws ExceptionComponentLifecycle {

        super.activate(context);

        trace("({}) - Starting activation of component {}(v{}) -> {}.",
                getId(), getName(), getVersion(), getLocation());
        trace("({}) - Processing annotations...", getId());
        doRuntimeAnnotationsProcessing();

        if (!getProperties().isEmpty()) {
            trace("({}) - Processing properties...", getId());
            doRuntimePropertiesProcessing(getProperties());
        }

        trace("({}) - Defining events to be tracked...", getId());
        defineEventsToBeTracked();
        registerDefinedEventTrackers();

        Object enableTrackersObj = context.getProperties().get(
                PROPERTY_ENABLE_INNER_SERVICE_TRACKERS);
        enableInnerServiceTrackers = (boolean) (enableTrackersObj != null ? enableTrackersObj
                : false);
        if (enableInnerServiceTrackers) {
            trace("({}) - Defining the inner services to be tracked...",
                    getId());
            defineInnerServicesToBeTracked();
            openDefinedInnerServiceTrackers();
        }

        try {
            trace("({}) - Running the pre-activate procedure...", getId());
            doBeforeActivateComponent();
        } catch (ExceptionComponentLifecycle e) {
            error("Error in the pre-activate procedure.", e);
            throw e;
        }

        try {
            trace("({}) - Running the activate procedure...", getId());
            doWhenActivateComponent();
        } catch (ExceptionComponentLifecycle e) {
            error("Error in the activate procedure.", e);
            throw e;
        }

        try {
            trace("({}) - Running the pos-activate procedure...", getId());
            doAfterActivateComponent();
        } catch (ExceptionComponentLifecycle e) {
            error("Error in the pos-activate procedure.", e);
            throw e;
        }

        debug("({}) - Activated component '{}'.", getId(), getName());
    }

    /**
     * 
     * @param configurationAdmin
     */
    protected void bindConfigurationAdminService(
            ConfigurationAdmin configurationAdmin) {
        this.configAdminServiceRef.set(configurationAdmin);
        trace("({}) - Bound ConfigurationAdmin for component '{}'.", getId(),
                this.getClass().getSimpleName());
    }

    /**
     * 
     * @param eventAdmin
     */
    protected void bindEventAdminService(EventAdmin eventAdmin) {
        this.eventAdminServiceRef.set(eventAdmin);
        trace("({}) - Bound EventAdmin for component '{}'.", getId(), this
                .getClass().getSimpleName());
    }

    /**
     * 
     * @param logger
     */
    protected void bindLoggerService(LogService logService) {
        this.logServiceRef.set(logService);
        trace("({}) - Bound LogService for component '{}'.", getId(), this
                .getClass().getName());
    }

    /**
     * 
     * @param preferencesService
     */
    protected void bindPreferencesService(PreferencesService preferencesService) {
        this.preferencesServiceRef.set(preferencesService);
        trace("({}) - Bound PreferencesService for component '{}'.", getId(),
                this.getClass().getName());
    }

    /**
     * Traverse the service tracker list and calls the
     * {@link PluggableServiceTracker#close()} method for all instances.
     */
    protected final void closeInnerServicesTrackers() {
        if (enableInnerServiceTrackers) {
            for (PluggableServiceTracker<?> tracker : getInnerPluggableServiceTrackers()
                    .values()) {
                tracker.close();
            }
        }
    }

    /**
     * Called by the DS engine when deactivating a component.
     */
    @Override
    protected final void deactivate(ComponentContext context)
            throws ExceptionComponentLifecycle {
        debug("({}) - deactivating the component '{}'.", getId(), getName());

        try {
            doBeforeDeactivateComponent();
        } catch (ExceptionComponentLifecycle e) {
            error("Error before deactivating a component.", e);
            throw e;
        }

        try {
            doWhenDeactivateComponent();
        } catch (ExceptionComponentLifecycle e) {
            error("Error when deactivating a component.", e);
            throw e;
        }

        try {
            doAfterDeactivateComponent();
        } catch (ExceptionComponentLifecycle e) {
            error("Error after deactivating a component.", e);
            throw e;
        }

        closeInnerServicesTrackers();

        unregisterEventTrackers();

        getInnerPluggableServiceTrackers().clear();

        getEventTrackers().clear();

        super.deactivate(context);
    }

    /**
     * Log a message using the DEBUG level.
     * <p>
     * When a {@link LogService} instance is bound to this instance it will be
     * used to log the message, otherwise a SLF4J's {@link Logger} instance will
     * be used.
     * 
     * @param message
     */
    public final void debug(String message) {
        getLogger().debug(message);
    }

    /**
     * Log a message using the DEBUG level.
     * <p>
     * When a {@link LogService} instance is bound to this instance it will be
     * used to log the message, otherwise a SLF4J's {@link Logger} instance will
     * be used.
     * 
     * @param format
     * @param arguments
     */
    public final void debug(String format, Object... arguments) {
        getLogger().debug(format, arguments);
    }

    /**
     * A method where developer can define which {@link Event events} will be
     * automatically tracked and properly handled by this component class.
     * <p>
     * Developer must use the method {@link #trackEvent(PluggableEventTracker)}
     * to register an event to be tracked.
     * <p>
     * It is aimed to be overridden by children concrete classes.
     */
    protected void defineEventsToBeTracked() {
    }

    /**
     * A method where developer can define which services will be automatically
     * tracked by this component class.
     * <p>
     * Developer must use the method
     * {@link #trackServicesWith(PluggableServiceTracker)} to register a service
     * to be tracked.
     * <p>
     * To get an instance of those registered service just use the correspondent
     * getXXX() method from this class.
     * <p>
     * It is aimed to be overridden by children concrete classes but don't
     * forget to call super.defineServicesToBeTracked().
     * 
     * @throws InvalidSyntaxException
     */
    protected void defineInnerServicesToBeTracked()
            throws ExceptionComponentLifecycle {

    }

    /**
     * Called after the activate component method be run.
     * <p>
     * It is aimed to be overridden by children concrete classes.
     */
    protected void doAfterActivateComponent()
            throws ExceptionComponentLifecycle {

    }

    /**
     * Called before the deactivate component method be run.
     * <p>
     * It is aimed to be overridden by children concrete classes.
     */
    protected void doAfterDeactivateComponent()
            throws ExceptionComponentLifecycle {

    }

    /**
     * Called before the activate component method be run.
     * <p>
     * It is aimed to be overridden by children concrete classes.
     */
    protected void doBeforeActivateComponent()
            throws ExceptionComponentLifecycle {

    }

    /**
     * Called before the deactivate component method be run.
     * <p>
     * It is aimed to be overridden by children concrete classes.
     */
    protected void doBeforeDeactivateComponent()
            throws ExceptionComponentLifecycle {

    }

    /**
     * This method is a hook that could be used to process the annotations
     * tagged in the component class declaration.
     * <p>
     * As this hook method will be called at the beginning of the activation
     * procedure, values encountered here could be hold in class variables and
     * used by other methods of the component.
     * <p>
     * It is aimed to be overridden by children concrete classes.
     * 
     * @param annotations
     * 
     * @throws Exception
     */
    protected void doRuntimeAnnotationsProcessing()
            throws ExceptionComponentLifecycle {

    }

    /**
     * Do the processing of the properties passed for this component at runtime.
     * <p>
     * This method is called in the beginning of the activation process.
     * <p>
     * It is aimed to be overridden by children concrete classes.
     * 
     * @param properties
     */
    protected void doRuntimePropertiesProcessing(
            Dictionary<String, Object> properties) {
        // should be implemented by concrete children.
    }

    /**
     * This method is called by the {@link #activate(ComponentContext)} method
     * in order to complete the activation of the child concrete component.
     * <p>
     * It is aimed to be overridden by children concrete classes.
     */
    protected void doWhenActivateComponent() throws ExceptionComponentLifecycle {

    }

    /**
     * 
     * @throws ExceptionComponentLifecycle
     */
    protected void doWhenDeactivateComponent()
            throws ExceptionComponentLifecycle {

    }

    /**
     * Log a message using the ERROR level.
     * <p>
     * When a {@link LogService} instance is bound to this instance it will be
     * used to log the message, otherwise a SLF4J's {@link Logger} instance will
     * be used.
     * 
     * @param message
     */
    public final void error(String message) {
        if (getLogService() != null) {
            getLogService().log(LogService.LOG_ERROR, message);
        } else {
            getLogger().error(message);
        }
    }

    /**
     * Log a message using the ERROR level.
     * <p>
     * When a {@link LogService} instance is bound to this instance it will be
     * used to log the message, otherwise a SLF4J's {@link Logger} instance will
     * be used.
     * 
     * @param message
     */
    public final void error(String format, Object... arguments) {
        if (getLogService() != null) {
            getLogService().log(LogService.LOG_ERROR,
                    MessageFormatter.format(format, arguments).getMessage());
        } else {
            getLogger().error(format, arguments);
        }
    }

    /**
     * Log a message using the ERROR level.
     * <p>
     * When a {@link LogService} instance is bound to this instance it will be
     * used to log the message, otherwise a SLF4J's {@link Logger} instance will
     * be used.
     * 
     * @param message
     */
    public final void error(String message, Throwable throwable) {
        if (getLogService() != null) {
            getLogService().log(LogService.LOG_ERROR, message, throwable);
        } else {
            getLogger().error(message, throwable);
        }
    }

    /**
     * A method that returns the {@link EventAdmin} service instance.
     * 
     * @return the EventAdmin service instance
     */
    protected final ConfigurationAdmin getConfigurationAdminService() {
        return configAdminServiceRef.get();
    }

    /**
     * A method that returns the {@link EventAdmin} service instance.
     * 
     * @return the EventAdmin service instance
     */
    protected final EventAdmin getEventAdminService() {
        return eventAdminServiceRef.get();
    }

    protected final Set<PluggableEventTracker> getEventTrackers() {
        if (eventTrackers == null) {
            eventTrackers = new HashSet<>();
        }
        return eventTrackers;
    }

    @SuppressWarnings("unchecked")
    protected final <S> PluggableServiceTracker<S> getInnerPluggableServiceTracker(
            Class<S> serviceInterface) {
        return getInnerPluggableServiceTrackers().get(serviceInterface);
    }

    @SuppressWarnings("rawtypes")
    protected final Map<Class<?>, PluggableServiceTracker> getInnerPluggableServiceTrackers() {
        if (innerPluggableServiceTrackers == null) {
            innerPluggableServiceTrackers = new HashMap<>();
        }
        return innerPluggableServiceTrackers;
    }

    /**
     * A method that returns an {@link Logger} instance for the current class.
     * <p>
     */
    protected final Logger getLogger() {
        if (logger == null){
            logger = LoggerFactory.getLogger(this.getClass());
        }
        return logger;
    }

    /**
     * A method that returns the bound {@link LogService} instance.
     * <p>
     * If no Logger service were registered this method will return an instance
     * created by the {@link LoggerFactory} factory class.
     *
     * @return a non null logger object.
     */
    protected final LogService getLogService() {
        if (logServiceRef.get() != null) {
            return logServiceRef.get();
        } else
            return null;
    }

    /**
     * A method that returns the {@link PreferencesService} instance.
     * <p>
     * It can be null;
     * 
     * @return the preference service instance
     */
    protected final PreferencesService getPreferencesService() {

        return preferencesServiceRef.get();
    }

    /**
     * Log a message using the INFO level.
     * <p>
     * When a {@link LogService} instance is bound to this instance it will be
     * used to log the message, otherwise a SLF4J's {@link Logger} instance will
     * be used.
     * 
     * @param message
     */
    public final void info(String message) {
        if (getLogService() != null) {
            getLogService().log(LogService.LOG_INFO, message);
        } else {
            getLogger().info(message);
        }
    }

    /**
     * Log a message using the INFO level.
     * <p>
     * When a {@link LogService} instance is bound to this instance it will be
     * used to log the message, otherwise a SLF4J's {@link Logger} instance will
     * be used.
     * 
     * @param format
     * @param arguments
     */
    public final void info(String format, Object... arguments) {
        if (getLogService() != null) {
            getLogService().log(LogService.LOG_INFO,
                    MessageFormatter.format(format, arguments).getMessage());
        } else {
            getLogger().info(format, arguments);
        }
    }

    protected final boolean isIgnoreTrackers() {
        return enableInnerServiceTrackers;
    }

    /**
     * Traverse the service tracker list and calls the
     * {@link PluggableServiceTracker#open()} method for all instances.
     * 
     * @throws Exception
     * 
     */
    protected final void openDefinedInnerServiceTrackers()
            throws ExceptionComponentLifecycle {

        for (PluggableServiceTracker<?> serviceTracker : getInnerPluggableServiceTrackers()
                .values()) {
            serviceTracker.open();
        }
    }

    protected final void postEvent(String eventTopic) {
        Map<String, Object> properties = new HashMap<String, Object>();
        postEvent(eventTopic, properties);
    }

    protected final void postEvent(String eventTopic, Map<String, ?> properties) {
        Event event = new Event(eventTopic, properties);
        getEventAdminService().postEvent(event);
    }

    protected final void postEvent(String eventTopic, String context) {
        Map<String, Object> properties = new HashMap<String, Object>();
        // properties.put(LUNIFERA_RUNTIME_EVENT_PROPERTY_CONTEXT, context);
        postEvent(eventTopic, properties);
    }

    /**
     * Traverse the event tracker list and calls the
     * {@link PluggableEventTracker#registerEventHandler()} method for all
     * instances.
     * 
     */
    protected final void registerDefinedEventTrackers() {
        for (PluggableEventTracker pluggableEventTracker : getEventTrackers()) {
            pluggableEventTracker.registerEventHandler();
        }
    }

    protected final void sendEvent(String eventTopic) {
        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        sendEvent(eventTopic, properties);
    }

    protected final void sendEvent(String eventTopic,
            Dictionary<String, ?> properties) {
        Event event = new Event(eventTopic, properties);
        getEventAdminService().sendEvent(event);
    }

    protected final void sendEvent(String eventTopic, String context) {
        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        // properties.put(LUNIFERA_RUNTIME_EVENT_PROPERTY_CONTEXT, context);
        sendEvent(eventTopic, properties);
    }

    /**
     * Log a message using the TRACE level.
     * <p>
     * When a {@link LogService} instance is bound to this instance it will be
     * used to log the message, otherwise a SLF4J's {@link Logger} instance will
     * be used.
     * <p>
     * As {@link LogService} doesn't have a trace() method it will be used the
     * debug() instead.
     * 
     * @param message
     */
    public final void trace(String message) {
        if (getLogService() != null) {
            getLogService().log(LogService.LOG_DEBUG, message);
        } else {
            getLogger().trace(message);
        }
    }

    /**
     * Log a message using the TRACE level.
     * <p>
     * When a {@link LogService} instance is bound to this instance it will be
     * used to log the message, otherwise a SLF4J's {@link Logger} instance will
     * be used.
     * <p>
     * As {@link LogService} doesn't have a trace() method it will be used the
     * debug() instead.
     * 
     * @param format
     * @param arguments
     */
    public final void trace(String format, Object... arguments) {
        if (getLogService() != null) {
            getLogService().log(LogService.LOG_DEBUG,
                    MessageFormatter.format(format, arguments).getMessage());
        } else {
            getLogger().trace(format, arguments);
        }
    }

    /**
     * Includes an {@link PluggableEventTracker event tracker} into the list to
     * be managed by this component.
     * 
     * @param eventTracker
     */
    protected final void trackEvent(PluggableEventTracker eventTracker) {
        getEventTrackers().add(eventTracker);
    }

    /**
     * Adds a service class to be tracked by this component.
     * <p>
     * An instance of PluggableServiceTracker
     * 
     * @param serviceToTrack
     * @throws ExceptionComponentLifecycle
     */
    protected final <T> PluggableServiceTracker<T> trackServicesWith(
            Class<T> serviceToTrack) throws ExceptionComponentLifecycle {
        return trackServicesWith(serviceToTrack, null);
    }

    /**
     * Adds a service class to be tracked by this component.
     * <p>
     * An instance of PluggableServiceTracker will be created for each
     * interface.
     * 
     * @param serviceToTrack
     *            the interface of the service to be tracked.
     * @param filter
     *            used to track for the service.
     * @throws ExceptionComponentLifecycle
     */
    protected final <T> PluggableServiceTracker<T> trackServicesWith(
            Class<T> serviceToTrack, String filter)
            throws ExceptionComponentLifecycle {
        PluggableServiceTracker<T> st = null;
        if (!enableInnerServiceTrackers) {
            try {
                st = new PluggableServiceTracker<T>(serviceToTrack,
                        getBundleContext(), filter) {
                };
            } catch (InvalidSyntaxException e) {
                throw new ExceptionComponentLifecycle(e);
            }
            getInnerPluggableServiceTrackers().put(serviceToTrack, st);
        }
        return st;
    }

    /**
     * Adds a service class to be tracked by this component.
     * <p>
     * An instance of PluggableServiceTracker
     * 
     * @param serviceToTrack
     * @throws InvalidSyntaxException
     */
    protected final <T> PluggableServiceTracker<T> trackServicesWith(
            PluggableServiceTracker<T> plugableServiceTracker)
            throws ExceptionComponentLifecycle {
        getInnerPluggableServiceTrackers()
                .put(plugableServiceTracker.getServiceType(),
                        plugableServiceTracker);
        return plugableServiceTracker;
    }

    /**
     * Method called by the DS or other to unbind an instance of
     * {@link ConfigurationAdmin} service.
     * 
     * @param configurationAdmin
     */
    protected final void unbindConfigurationAdminService(
            ConfigurationAdmin configurationAdmin) {
        trace("({}) - Unbound ConfigurationAdmin for component '{}'.", getId(),
                this.getClass().getName());
        configAdminServiceRef.compareAndSet(configurationAdmin, null);
    }

    /**
     * Method called by the DS or other to unbind an instance of
     * {@link EventAdmin} service.
     * 
     * @param eventAdmin
     */
    protected final void unbindEventAdminService(EventAdmin eventAdmin) {
        trace("({}) - Unbound EventAdmin for component '{}'.", getId(), this
                .getClass().getName());
        eventAdminServiceRef.compareAndSet(eventAdmin, null);
    }

    /**
     * Method called by the DS or other to unbind an instance of
     * {@link LogService} service.
     * 
     * @param logService
     */
    protected final void unbindLogService(LogService logService) {
        trace("({}) - Unbound LogService for component '{}'.", getId(), this
                .getClass().getName());
        logServiceRef.compareAndSet(logService, null);
    }

    /**
     * Method called by the DS or other to unbind an instance of
     * {@link PreferencesService} service.
     * 
     * @param preferencesService
     */
    protected final void unbindPreferencesService(
            PreferencesService preferencesService) {
        trace("({}) - Unbound PreferencesService for component '{}'.", getId(),
                this.getClass().getName());
        preferencesServiceRef.compareAndSet(preferencesService, null);
    }

    /**
     * Traverse the event tracker list and calls the
     * {@link PluggableEventTracker#unregisterEventHandler()} method for all
     * instances.
     * 
     */
    protected final void unregisterEventTrackers() {
        for (PluggableEventTracker pluggableEventTracker : getEventTrackers()) {
            pluggableEventTracker.unregisterEventHandler();
        }
    }

    /**
     * Log a message using the WARN level.
     * <p>
     * When a {@link LogService} instance is bound to this instance it will be
     * used to log the message, otherwise a SLF4J's {@link Logger} instance will
     * be used.
     * 
     * @param message
     */
    public final void warn(String message) {
        if (getLogService() != null) {
            getLogService().log(LogService.LOG_WARNING, message);
        } else {
            getLogger().warn(message);
        }
    }

    /**
     * Log a message using the WARN level.
     * <p>
     * When a {@link LogService} instance is bound to this instance it will be
     * used to log the message, otherwise a SLF4J's {@link Logger} instance will
     * be used.
     * 
     * @param format
     * @param arguments
     */
    public final void warn(String format, Object... arguments) {
        getLogger().warn(format, arguments);
        if (getLogService() != null) {
            getLogService().log(LogService.LOG_WARNING,
                    MessageFormatter.format(format, arguments).getMessage());
        } else {
            getLogger().warn(format, arguments);
        }
    }
}
