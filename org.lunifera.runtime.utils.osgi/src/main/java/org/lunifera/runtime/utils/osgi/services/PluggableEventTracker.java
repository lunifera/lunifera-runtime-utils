package org.lunifera.runtime.utils.osgi.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

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
/**
 * 
 * @author cvgaviao
 * @since 0.5.0
 */
public abstract class PluggableEventTracker {

    class KernelNotificationEventHandler implements EventHandler {
        @Override
        public void handleEvent(Event event) {
            whenEventHappen(event);
        }
    }

    private final BundleContext bundleContext;

    private ServiceRegistration<EventHandler> eventHandlerServiceRegistration = null;

    private String filterStr;

    private List<String> topics;

    public PluggableEventTracker(BundleContext bundleContext) {
        this(bundleContext, null, "");
    }

    public PluggableEventTracker(BundleContext bundleContext, String[] topics) {
        this(bundleContext, topics, "");
    }

    public PluggableEventTracker(BundleContext bundleContext, String[] topics,
            String filterStr) {
        this.topics = Arrays.asList(topics);
        this.filterStr = filterStr;
        this.bundleContext = bundleContext;
    }

    /**
     * Change the properties of a registered EventHandler object.
     * 
     * @param topics
     * @param filterStr
     */
    public void changeProperties(String[] topics, String filterStr) {
        this.topics = Arrays.asList(topics);
        this.filterStr = filterStr;
        Dictionary<String, Object> props = new Hashtable<String, Object>();
        props.put(EventConstants.EVENT_TOPIC, getTopics());
        props.put(EventConstants.EVENT_FILTER, getFilterStr());

        eventHandlerServiceRegistration.setProperties(props);
    }

    protected BundleContext getBundleContext() {
        return bundleContext;
    }

    protected String getFilterStr() {
        return filterStr;
    }

    protected List<String> getTopics() {
        return Collections.unmodifiableList(topics);
    }

    /**
     * This method is called when a topic event is caught by the tracker.
     * <p>
     * Children classes must implement this method in order to start its own
     * response actions.
     * 
     * @param event
     */
    protected abstract void whenEventHappen(Event event);

    /**
     * Register an EventHandler object into the OSGi service registry with the
     * properties set in the constructor.
     */
    public void registerEventHandler() {
        Dictionary<String, Object> props = new Hashtable<String, Object>();
        props.put(EventConstants.EVENT_TOPIC, getTopics());
        props.put(EventConstants.EVENT_FILTER, getFilterStr());

        eventHandlerServiceRegistration = getBundleContext()
                .registerService(EventHandler.class,
                        new KernelNotificationEventHandler(), props);
    }

    /**
     * Remove the EventHandler from the OSGi service registry.
     */
    public void unregisterEventHandler() {
        eventHandlerServiceRegistration.unregister();
    }
}
