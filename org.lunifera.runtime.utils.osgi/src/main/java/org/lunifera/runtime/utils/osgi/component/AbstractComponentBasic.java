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

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;

import com.google.common.base.Objects;

/**
 * A basic abstract parent class for an OSGi Declarative Service based
 * component.
 * <p>
 * It has a custom activate() method that set informations collected about the
 * component and has some common useful methods.<br>
 * 
 * @since 0.0.1
 * @author Cristiano Gavião
 * 
 */
public abstract class AbstractComponentBasic {

	public static String COMPONENT_DESCRIPTION = "component.description";
	public static String EXTENDER_SERVICE_LOOKUP_STATE_MASK = "lunifera.extender.lookup.state.mask";
	public static String EXTENDER_SERVICE_ATTR_MANIFEST_HEADER_NAME = "lunifera.extender.manifest.header.name";
	public static String EXTENDER_SERVICE_ATTR_CONTRIBUTION_ITEM_RESOURCE_TYPE = "lunifera.extender.contribution.item.resource.type";
	public static String EXTENDER_SERVICE_ATTR_CONTRIBUTION_HANDLER_SERVICE = "lunifera.extender.contribution.handler";
	public static String EXTENDER_SERVICE_ATTR_EXTENSION_HANDLING_STRATEGY = "lunifera.extender.handling.strategy";

	/**
	 * The component ID is defined by DS bundle.
	 */
	private ComponentContext componentContext;

	/**
	 * The component ID is defined by the component's developer.
	 */
	private String componentDescription;

	/**
	 * The component ID is defined by DS bundle.
	 */
	private Long componentId;

	/**
	 * The component ID is defined by the component's developer.
	 */
	private String componentName;

	/**
	 * DS needs a default constructor.
	 */
	public AbstractComponentBasic() {
	}

	/**
	 * Constructor created just to help unit testing. It is not used by OSGi.
	 * 
	 * @param componentContext
	 */
	public AbstractComponentBasic(ComponentContext componentContext) {
		super();
		this.componentContext = componentContext;
	}

	/**
	 * This is the default activate method called by OSGi Declarative Service
	 * extender.
	 * <p>
	 * Concrete class can use a {@link Activate @Activate} annotation with this
	 * method.
	 * 
	 * @param context
	 */
	protected void activate(ComponentContext componentContext)
			throws ExceptionComponentLifecycle {

		// save bundleContext reference...
		this.componentContext = componentContext;

		// set common component attributes
		componentId = (Long) componentContext.getProperties().get(
				ComponentConstants.COMPONENT_ID);
		componentName = (String) componentContext.getProperties().get(
				ComponentConstants.COMPONENT_NAME);
		componentDescription = (String) componentContext.getProperties().get(
				COMPONENT_DESCRIPTION);
	}

	/**
	 * This is the default deactivate method called by OSGi Declarative Service
	 * extender.
	 * 
	 * @param context
	 */
	protected void deactivate(ComponentContext context)
			throws ExceptionComponentLifecycle {

		componentDescription = null;
		componentName = null;
		componentId = null;
		componentContext = null;

	}

	/**
	 * This method returns the associated BundleContext.
	 * 
	 * @return
	 */
	protected final BundleContext getBundleContext() {
		return getComponentContext().getBundleContext();
	}

	/**
	 * This method returns the associated ComponentContext.
	 * 
	 * @return the ComponentContext.
	 */
	protected final ComponentContext getComponentContext() {
		return componentContext;
	}

	/**
	 * Return the component description
	 * 
	 * @return
	 */
	protected final String getDescription() {
		return (componentDescription != null ? componentDescription : "");
	}

	/**
	 * 
	 * 
	 * @return
	 */
	protected final Long getId() {
		return (componentId != null ? componentId : 0);
	}

	/**
	 * The name of the registered component.
	 * <p>
	 * This value is set after the component be activated by DS.
	 * 
	 * @return the component's name.
	 */
	protected final String getName() {
		return (componentName != null ? componentName : "");
	}

	/**
	 * Return the properties map associated with the component instance.
	 * 
	 * @return the component's properties map.
	 */
	protected final Dictionary<String, Object> getProperties() {
		return getComponentContext().getProperties();
	}

	/**
	 * This is the main activation method of DS components.
	 * <p>
	 * 
	 * @param context
	 */
	protected void modified(ComponentContext context)
			throws ExceptionComponentLifecycle {

		activate(context);
	}

	@Override
	public final String toString() {
		return Objects.toStringHelper(getClass()).add("Id", componentId)
				.add("Name", getName()).toString();
	}
}
