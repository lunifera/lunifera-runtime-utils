package org.lunifera.runtime.utils.osgi.services;

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

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * 
 * @author cvgaviao
 *
 * @param <S>
 *            The type of the service being tracked.
 */
public abstract class PluggableServiceTracker<S> {

    private final BundleContext bundleContext;

    /**
     * Service tracker for the <S> service.
     */
    private ServiceTracker<S, S> currentServiceTracker;

    private final Filter filter;

    private final Class<S> serviceType;

    public Class<S> getServiceType() {
        return serviceType;
    }

    public PluggableServiceTracker(Class<S> serviceClass,
            BundleContext bundleContext) throws InvalidSyntaxException {
        this(serviceClass, bundleContext, "");
    }

    public PluggableServiceTracker(Class<S> serviceClass,
            BundleContext bundleContext, Filter filter)
            throws InvalidSyntaxException {
        this.bundleContext = bundleContext;
        this.serviceType = serviceClass;
        this.filter = filter;
    }

    public PluggableServiceTracker(Class<S> serviceClass,
            BundleContext bundleContext, String filterStr)
            throws InvalidSyntaxException {
        this.bundleContext = bundleContext;
        this.serviceType = serviceClass;

        if (filterStr != null && !filterStr.isEmpty()) {
            this.filter = bundleContext.createFilter(filterStr);
        } else {
            this.filter = bundleContext.createFilter("(objectclass="
                    + serviceType + ")");
        }
    }

    public void close() {
        if (currentServiceTracker != null) {
            currentServiceTracker.close();
            currentServiceTracker = null;
        }
    }

    public S getHeadServiceInstance() {
        return currentServiceTracker.getService();
    }

    public S getService() {
        if (currentServiceTracker != null) {
            return currentServiceTracker.getService();
        }
        return null;
    }

    public List<S> getServices() {
        if (currentServiceTracker == null) {
            return null;
        }
        synchronized (currentServiceTracker) {
            ServiceReference<S>[] references = currentServiceTracker
                    .getServiceReferences();
            int length = (references == null) ? 0 : references.length;
            if (length == 0) {
                return null;
            }
            List<S> objects = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                objects.add(currentServiceTracker.getService(references[i]));
            }
            return objects;
        }
    }

    public void open() {
        if (currentServiceTracker == null) {
            currentServiceTracker = new ServiceTracker<>(bundleContext, filter,
                    new ServiceTrackerCustomizer<S, S>() {

                        @Override
                        public S addingService(ServiceReference<S> reference) {
                            S result = (S) bundleContext.getService(reference);
                            whenAddedService(reference);
                            return result;
                        }

                        @Override
                        public void modifiedService(
                                ServiceReference<S> reference, S service) {
                            whenModifiedService(reference);
                        }

                        @Override
                        public void removedService(
                                ServiceReference<S> reference, S service) {
                            whenRemovedService(reference);
                            bundleContext.ungetService(reference);
                        }
                    });
        }
    }

    /**
     * A notification method that is triggered when services were removed from
     * the OSGi service register.
     * <p>
     * It is aimed to be overridden by children classes that desires to do some
     * action when the remove event has occurred.
     * 
     * @param reference
     */
    protected void whenRemovedService(ServiceReference<S> reference) {
    }

    /**
     * A notification method that is triggered when services were modified into
     * the OSGi service register.
     * <p>
     * It is aimed to be overridden by children classes that desires to do some
     * action when the modify event has occurred.
     * 
     * @param reference
     */
    protected void whenModifiedService(ServiceReference<S> reference) {
    }

    /**
     * A notification method that is triggered when services were added into the
     * OSGi service register.
     * <p>
     * It is aimed to be overridden by children classes that desires to do some
     * action when the add event has occurred.
     * 
     * @param reference
     */
    protected void whenAddedService(ServiceReference<S> reference) {
    }

}
