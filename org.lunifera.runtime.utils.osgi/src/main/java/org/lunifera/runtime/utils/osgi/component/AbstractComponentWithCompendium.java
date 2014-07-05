package org.lunifera.runtime.utils.osgi.component;

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

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.prefs.PreferencesService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract class to be inherited by components that requires a logging, an
 * event admin, preference and some others OSGi compendium services bounded at
 * activation time.
 * <p>
 * This component defines 3 life cycle events attached to its activate() and
 * deactivate() methods. This provides to children ways to define pre and post
 * operations for an activate or deactivate occurrences.
 * 
 * Unfortunately, OSGi DS Annotations don't support inheritance, so any DS
 * annotation setup shouldn't be made inside a parent abstract class, only on
 * its concrete children.<br>
 * For this reason a ServiceTracker its being used to obtain references of those
 * Compendium services instead of using a Declarative Service implementation.
 * <p>
 * Therefore, a children component extending this class just need to call
 * getLoggerService(), getPreferencesService() or getEventAdminService() methods
 * to get references to the compendium services (if they are available in the
 * container, of course).
 * <p>
 * Developers can choose to use only DS, preventing the creation of tracker
 * objects. To do that, he must override the bind method of the required service
 * in the concrete class and add a {@link Reference @Reference} annotation for
 * each override method. Doing this, the service tracker won't be created.
 * 
 * @author Cristiano Gavião
 * @since 0.0.1
 * 
 */
public abstract class AbstractComponentWithCompendium extends
		AbstractComponentBasic {

	/**
	 * Holds an atomic reference of a {@link EventAdmin} service.
	 */
	private final AtomicReference<EventAdmin> eventAdminServiceRef = new AtomicReference<EventAdmin>();

	/**
	 * Service tracker for the {@link EventAdmin} service.
	 */
	private ServiceTracker<EventAdmin, EventAdmin> eventAdminServiceTracker;

	/**
	 * Holds an atomic reference for the logging service {@link Logger}. Repair
	 * that this service is not the one provided by OSGi, we are using the SLF4J
	 * interface that is more complete and give us more flexibilities on report.
	 */
	private final AtomicReference<Logger> loggerServiceRef = new AtomicReference<Logger>();

	/**
	 * A service tracker for the logging service.
	 */
	private ServiceTracker<Logger, Logger> loggerServiceTracker;

	/**
	 * Service tracker for the {@link PreferencesService} service.
	 */
	private ServiceTracker<PreferencesService, PreferencesService> preferenceServiceTracker;

	/**
	 * Holds an atomic reference of a {@link PreferencesService} service.
	 */
	private final AtomicReference<PreferencesService> preferencesServiceRef = new AtomicReference<PreferencesService>();

	/**
	 * DS needs a default constructor.
	 * 
	 */
	public AbstractComponentWithCompendium() {
	}

	/**
	 * Constructor created just to help unit testing. It is not used by OSGi.
	 * 
	 * @param componentContext
	 */
	protected AbstractComponentWithCompendium(ComponentContext componentContext) {
		super(componentContext);
	}

	@Override
	protected final void activate(ComponentContext context)
			throws ExceptionComponentLifecycle {

		super.activate(context);

		openCompendiumServiceTrackers();
		try {
			beforeActivate();
		} catch (ExceptionComponentLifecycle e) {
			error("Error before activating component.", e);
			throw e;
		}

		try {
			onActivate();
		} catch (ExceptionComponentLifecycle e) {
			error("Error when activating component.", e);
			throw e;
		}

		try {
			afterActivate();
		} catch (ExceptionComponentLifecycle e) {
			error("Error after activating component.", e);
			throw e;
		}

		trace("({}) - Activated component '{}'.", getId(), getName());
	}

	/**
	 * To be implemented by children.
	 */
	public void afterActivate() throws ExceptionComponentLifecycle {

	}

	public void afterDeactivate() throws ExceptionComponentLifecycle {

	}

	/**
	 * To be implemented by children.
	 */
	public void beforeActivate() throws ExceptionComponentLifecycle {

	}

	public void beforeDeactivate() throws ExceptionComponentLifecycle {

	}

	/**
	 * 
	 * @param eventAdmin
	 */
	protected void bindEventAdmin(EventAdmin eventAdmin) {
		this.eventAdminServiceRef.set(eventAdmin);
		trace("({}) - Bound EventAdmin for component '{}'.", getId(), this
				.getClass().getSimpleName());
	}

	/**
	 * 
	 * @param logger
	 */
	protected void bindLoggerService(Logger logger) {
		this.loggerServiceRef.set(logger);
		trace("({}) - Bound Logger for component '{}'.", getId(), this
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
     * 
     */
	protected final void closeCompendiumServicesTrackers() {
		if (eventAdminServiceTracker != null) {
			eventAdminServiceTracker.close();
			eventAdminServiceTracker = null;
		}
		if (preferenceServiceTracker != null) {
			preferenceServiceTracker.close();
			preferenceServiceTracker = null;
		}
		if (loggerServiceTracker != null) {
			loggerServiceTracker.close();
			loggerServiceTracker = null;
		}

	}

	@Override
	protected final void deactivate(ComponentContext context)
			throws ExceptionComponentLifecycle {
		debug("({}) - deactivating component '{}'.", getId(), getName());

		try {
			beforeDeactivate();
		} catch (ExceptionComponentLifecycle e) {
			error("Error before activating component.", e);
			throw e;
		}

		try {
			onDeactivate();
		} catch (ExceptionComponentLifecycle e) {
			error("Error when deactivating component.", e);
			throw e;
		}

		try {
			afterDeactivate();
		} catch (ExceptionComponentLifecycle e) {
			error("Error after activating component.", e);
			throw e;
		}

		closeCompendiumServicesTrackers();

		super.deactivate(context);
	}

	protected final void debug(String msg) {
		getLoggerService().debug(msg);
	}

	protected final void debug(String format, Object... arguments) {
		getLoggerService().debug(format, arguments);

	}

	protected final void error(String msg) {
		getLoggerService().error(msg);
	}

	protected final void error(String format, Object... arguments) {
		getLoggerService().error(format, arguments);
	}

	protected void error(String format, Throwable throwable) {
		getLoggerService().error(format, throwable);
	}

	/**
	 * A method that returns the event admin service instance.
	 * 
	 * @return the event admin service instance
	 */
	public final EventAdmin getEventAdminService() {
		return eventAdminServiceRef.get();
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public final Logger getLoggerService() {
		if (loggerServiceRef.get() == null) {
			Logger logger = LoggerFactory
					.getLogger(AbstractComponentWithCompendium.class);
			return logger;
		}
		return loggerServiceRef.get();
	}

	/**
	 * A method that returns the preference service instance.
	 * 
	 * @return the preference service instance
	 */
	public final PreferencesService getPreferencesService() {

		return preferencesServiceRef.get();
	}

	protected final void info(String msg) {
		getLoggerService().info(msg);

	}

	protected final void info(String format, Object... arguments) {
		getLoggerService().info(format, arguments);
	}

	/**
	 * To be implemented by children.
	 */
	public void onActivate() throws ExceptionComponentLifecycle {

	}

	public void onDeactivate() throws ExceptionComponentLifecycle {

	}

	/**
	 * @throws Exception
	 * 
	 */
	private void openCompendiumServiceTrackers()
			throws ExceptionComponentLifecycle {
		if (loggerServiceRef.get() == null) {
			openTrackerForLoggingService();
		}

		if (eventAdminServiceRef.get() == null) {
			openTrackerForEventAdminService();
		}

		if (preferencesServiceRef.get() == null) {
			openTrackerForPreferenceService();
		}
	}

	/**
	 * Hook method used to setup all needed ServiceTracker (when not using DS)
	 * 
	 * @throws Exception
	 * @nooverride
	 */
	protected boolean openTrackerForEventAdminService()
			throws ExceptionComponentLifecycle {
		if (eventAdminServiceTracker == null) {
			Filter filter;
			try {
				filter = FrameworkUtil
						.createFilter("(objectclass=org.osgi.service.event.EventAdmin)");
			} catch (InvalidSyntaxException e) {
				throw new ExceptionComponentLifecycle(e);
			}
			eventAdminServiceTracker = new ServiceTracker<>(getBundleContext(),
					filter,
					new ServiceTrackerCustomizer<EventAdmin, EventAdmin>() {
						@Override
						public EventAdmin addingService(
								ServiceReference<EventAdmin> reference) {
							EventAdmin eventAdmin = getBundleContext()
									.getService(reference);
							bindEventAdmin(eventAdmin);
							return eventAdmin;
						}

						@Override
						public void modifiedService(
								ServiceReference<EventAdmin> reference,
								EventAdmin service) {
							if (getEventAdminService().equals(service)) {
								addingService(reference);
							}
						}

						@Override
						public void removedService(
								ServiceReference<EventAdmin> reference,
								EventAdmin service) {
							unbindEventAdmin(service);
						}
					});
			eventAdminServiceTracker.open();
		}
		return true;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 * @nooverride
	 */
	protected boolean openTrackerForLoggingService()
			throws ExceptionComponentLifecycle {
		if (loggerServiceTracker == null) {
			Filter filter;
			try {
				filter = FrameworkUtil
						.createFilter("(objectclass=org.slf4j.Logger)");
			} catch (InvalidSyntaxException e) {
				throw new ExceptionComponentLifecycle(e);
			}
			loggerServiceTracker = new ServiceTracker<>(getBundleContext(),
					filter, new ServiceTrackerCustomizer<Logger, Logger>() {
						@Override
						public Logger addingService(
								ServiceReference<Logger> reference) {
							Logger logger = getBundleContext().getService(
									reference);
							if (logger != null)
								bindLoggerService(logger);
							return logger;
						}

						@Override
						public void modifiedService(
								ServiceReference<Logger> reference,
								Logger service) {
							if (getLoggerService().equals(service)) {
								addingService(reference);
							}
						}

						@Override
						public void removedService(
								ServiceReference<Logger> reference,
								Logger service) {
							unbindLoggerService(service);
						}
					});
			loggerServiceTracker.open();
		}
		return true;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 * @nooverride
	 */
	protected boolean openTrackerForPreferenceService()
			throws ExceptionComponentLifecycle {
		if (preferenceServiceTracker == null) {
			Filter filter;
			try {
				filter = FrameworkUtil
						.createFilter("(objectclass=org.osgi.service.prefs.PreferencesService)");
			} catch (InvalidSyntaxException e) {
				throw new ExceptionComponentLifecycle(e);
			}
			preferenceServiceTracker = new ServiceTracker<>(
					getBundleContext(),
					filter,
					new ServiceTrackerCustomizer<PreferencesService, PreferencesService>() {
						@Override
						public PreferencesService addingService(
								ServiceReference<PreferencesService> reference) {
							PreferencesService preferencesService = getBundleContext()
									.getService(reference);
							bindPreferencesService(preferencesService);
							return preferencesService;
						}

						@Override
						public void modifiedService(
								ServiceReference<PreferencesService> reference,
								PreferencesService service) {
							if (getPreferencesService().equals(service)) {
								addingService(reference);
							}
						}

						@Override
						public void removedService(
								ServiceReference<PreferencesService> reference,
								PreferencesService service) {
							unbindPreferencesService(service);
						}
					});
			preferenceServiceTracker.open();
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.event.EventAdmin#postEvent()
	 */
	protected final void postEvent(String eventTopic) {
		Map<String, Object> properties = new HashMap<String, Object>();
		postEvent(eventTopic, properties);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.event.EventAdmin#postEvent()
	 */
	protected final void postEvent(String eventTopic, Map<String, ?> properties) {
		Event event = new Event(eventTopic, properties);
		getEventAdminService().postEvent(event);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.event.EventAdmin#postEvent()
	 */
	protected final void postEvent(String eventTopic, String context) {
		Map<String, Object> properties = new HashMap<String, Object>();
		// properties.put(LUNIFERA_RUNTIME_EVENT_PROPERTY_CONTEXT, context);
		postEvent(eventTopic, properties);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.event.EventAdmin#sendEvent()
	 */
	protected final void sendEvent(String eventTopic) {
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		sendEvent(eventTopic, properties);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.event.EventAdmin#sendEvent()
	 */
	protected final void sendEvent(String eventTopic,
			Dictionary<String, ?> properties) {
		Event event = new Event(eventTopic, properties);
		getEventAdminService().sendEvent(event);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.event.EventAdmin#sendEvent()
	 */
	protected final void sendEvent(String eventTopic, String context) {
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		// properties.put(LUNIFERA_RUNTIME_EVENT_PROPERTY_CONTEXT, context);
		sendEvent(eventTopic, properties);

	}

	protected final void trace(String msg) {
		getLoggerService().trace(msg);
	}

	protected final void trace(String format, Object... arguments) {
		getLoggerService().trace(format, arguments);
	}

	/**
	 * Method called by the DS or other to unbind an instance of
	 * {@link EventAdmin} service.
	 * 
	 * @param eventAdmin
	 */
	protected final void unbindEventAdmin(EventAdmin eventAdmin) {
		trace("({}) - Unbound EventAdmin for component '{}'.", getId(), this
				.getClass().getName());
		eventAdminServiceRef.compareAndSet(eventAdmin, null);
	}

	/**
	 * Method called by the DS or other to unbind an instance of {@link Logger}
	 * service.
	 * 
	 * @param logger
	 */
	protected final void unbindLoggerService(Logger logger) {
		trace("({}) - Unbound Logger for component '{}'.", getId(), this
				.getClass().getName());
		loggerServiceRef.compareAndSet(logger, null);
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

	protected void warn(String msg) {
		getLoggerService().warn(msg);
	}

	protected void warn(String format, Object... arguments) {
		getLoggerService().warn(format, arguments);
	}
}
