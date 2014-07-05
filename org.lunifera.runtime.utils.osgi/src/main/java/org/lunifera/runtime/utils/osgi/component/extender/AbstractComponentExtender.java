package org.lunifera.runtime.utils.osgi.component.extender;

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
import java.util.concurrent.atomic.AtomicReference;

import org.lunifera.runtime.utils.osgi.component.AbstractComponentCompendium;
import org.lunifera.runtime.utils.osgi.component.ExceptionComponentLifecycle;
import org.lunifera.runtime.utils.osgi.component.ExceptionComponentUnrecoveredActivationError;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * An abstract class for extender components.
 * <p>
 * This class plus the annotation {@link ComponentExtenderSetup} aims to provide
 * an easier way to configure an extender component.
 * <p>
 * An extender component can receive contributions from one or more bundles
 * (contributors) that contains a specific defined head in its manifest file.
 * 
 * @author Cristiano Gavião
 * @since 0.0.1
 */
public abstract class AbstractComponentExtender extends
		AbstractComponentCompendium implements ComponentExtenderService {

	/**
	 * This class is used to track 'extendee' bundles.
	 * 
	 */
	protected static class ContributorBundleTracker extends
			BundleTracker<ContributorBundleTrackerObject> {

		/**
         * 
         */
		private final AbstractComponentExtender abstractComponentExtender;

		/**
         * 
         */
		private ContributionHandlerService contributionHandlerService = null;

		/**
		 * 
		 * @param abstractComponentExtender
		 * @param context
		 * @param stateMask
		 * @param customizer
		 * @throws ExceptionComponentLifecycle
		 */
		protected ContributorBundleTracker(
				AbstractComponentExtender abstractComponentExtender,
				BundleContext context,
				int stateMask,
				BundleTrackerCustomizer<ContributorBundleTrackerObject> customizer)
				throws ExceptionComponentLifecycle {
			super(context, stateMask, customizer);
			this.abstractComponentExtender = abstractComponentExtender;
			this.contributionHandlerService = abstractComponentExtender
					.getContributionHandlerService();
			if (this.contributionHandlerService == null) {
				throw new ExceptionComponentUnrecoveredActivationError(
						"An instance of ContributionHandlerService wasn't set.");
			}
		}

		/**
		 * 
		 * @param abstractComponentExtender
		 * @param stateMask
		 * @throws ExceptionComponentLifecycle
		 */
		protected ContributorBundleTracker(
				AbstractComponentExtender abstractComponentExtender,
				int stateMask) throws ExceptionComponentLifecycle {
			this(abstractComponentExtender, abstractComponentExtender
					.getComponentContext().getBundleContext(), stateMask, null);
		}

		@Override
		public ContributorBundleTrackerObject addingBundle(
				Bundle contributorBundle, BundleEvent event) {

			String header = contributorBundle.getHeaders().get(
					this.abstractComponentExtender
							.getExtenderContributorManifestHeader());

			if (header == null) {
				this.abstractComponentExtender.getLoggerService().trace(
						"Ignored contributor bundle '{}' for '{}' extender.",
						contributorBundle.getSymbolicName(),
						this.abstractComponentExtender
								.getExtenderContributorManifestHeader());
				return null;
			}
			if (header.isEmpty()) {
				this.abstractComponentExtender
						.getLoggerService()
						.warn("Header values wasn't informed, so contributor bundle '{}' for '{}' extender was ignored.",
								contributorBundle.getSymbolicName(),
								this.abstractComponentExtender
										.getExtenderContributorManifestHeader());
				return null;
			}

			ContributorBundleTrackerObject contributorBundleTrackerObject = null;
			try {
				this.abstractComponentExtender
						.getLoggerService()
						.trace("Processing contributor bundle '{}' for '{}' extender.",
								contributorBundle.getSymbolicName(),
								this.abstractComponentExtender
										.getExtenderContributorManifestHeader());
				String headerName = this.abstractComponentExtender
						.getExtenderContributorManifestHeader();
				String headerValue = contributorBundle.getHeaders().get(
						headerName);

				contributorBundleTrackerObject = contributionHandlerService
						.onContributorBundleAddition(contributorBundle,
								headerName, headerValue);
			} catch (Exception e) {
				this.abstractComponentExtender
						.getLoggerService()
						.error("Problems occurred while evaluating contribution of bundle '{}'.",
								contributorBundle.getSymbolicName(), e);
				return null;
			}

			return contributorBundleTrackerObject;

		}

		@Override
		public void modifiedBundle(Bundle bundle, BundleEvent event,
				ContributorBundleTrackerObject contributorBundleTrackerObject) {

			try {

				contributionHandlerService
						.onContributorBundleModified(contributorBundleTrackerObject);
				this.abstractComponentExtender.getLoggerService().debug(
						"Contributor bundle '{}' for '{}' was modified.",
						bundle.getSymbolicName(),
						this.abstractComponentExtender
								.getExtenderContributorManifestHeader());
			} catch (Exception e) {
				this.abstractComponentExtender
						.getLoggerService()
						.warn("Problem occurred on mofification of contributor bundle '{}'.",
								bundle.getSymbolicName(), e);
			}

		}

		@Override
		public void removedBundle(Bundle bundle, BundleEvent event,
				ContributorBundleTrackerObject contributorBundleTrackerObject) {
			try {
				contributionHandlerService
						.onContributorBundleRemoval(contributorBundleTrackerObject);
				this.abstractComponentExtender.getLoggerService().debug(
						"Contributor bundle '{}' for '{}' was removed.",
						bundle.getSymbolicName(),
						this.abstractComponentExtender
								.getExtenderContributorManifestHeader());
			} catch (Exception e) {
				this.abstractComponentExtender
						.getLoggerService()
						.warn("Problem occurred on removal of contributor bundle '{}'.",
								bundle.getSymbolicName(), e);
			}

		}
	}

	/**
	 * This filter is constructed when the component is activated using the
	 * properties from configuration context.
	 */
	private Filter contributionHandlerServiceFilter = null;

	/**
	 * Service tracker for the {@link EventAdmin} service.
	 */
	private ServiceTracker<ContributionHandlerService, ContributionHandlerService> contributionHandlerServiceTracker;

	/**
	 * A reference to the current associated contribution handler service.
	 */
	private final AtomicReference<ContributionHandlerService> contributionHandlerServiceRef = new AtomicReference<ContributionHandlerService>();

	/**
	 * The {@link BundleTracker} used to track a contributor bundle.
	 */
	private volatile ContributorBundleTracker contributorBundleTracker = null;;

	/**
	 * The manifest header used to identify that a bundle is a contributor for
	 * an extender.
	 */
	private String extenderContributorManifestHeader = null;

	/**
	 * Defines which strategy is used to handle/process contributions from a
	 * bundle.
	 */
	private ExtensionHandlingStrategy extensionHandlingStrategy;

	/**
	 * Defines which type of resource is the contribution coming from the
	 * contributor bundle.
	 */
	private ContributionItemResourceType contributionItemResourceType;

	/**
	 * Defines which bundle states should tracked.
	 */
	private int stateMask;

	/**
	 * Defines an optional filter parameter for bind the
	 * ContributionHandlerService implementation to be used by this component.
	 */
	private String targetContributionHandlerService;

	/**
	 * A default constructor is required by the OSGi Declarative Service.
	 */
	public AbstractComponentExtender() {
		super();
	}

	/**
	 * Constructor created just to help unit testing. It is not used by OSGi.
	 * 
	 * @param componentContext
	 */
	protected AbstractComponentExtender(ComponentContext componentContext) {
		super(componentContext);
	}

	/**
	 * Constructor created just to help unit testing. It is not used by OSGi.
	 * 
	 * @param compendiumServices
	 */
	protected AbstractComponentExtender(
			ComponentContext componentContext,
			ContributionHandlerService contributionHandlerService,
			Filter contributionHandlerServiceFilter,
			ServiceTracker<ContributionHandlerService, ContributionHandlerService> contributionHandlerServiceTracker) {
		super(componentContext);
		this.contributionHandlerServiceRef.set(contributionHandlerService);
		this.contributionHandlerServiceFilter = contributionHandlerServiceFilter;
		this.contributionHandlerServiceTracker = contributionHandlerServiceTracker;
	}

	@Override
	public void beforeActivate() throws ExceptionComponentLifecycle {
		doProcessRuntimeAnnotations();

		doProcessProperties(getProperties());

		doOpenServiceTrackers();
	}

	/**
	 * 
	 * @param contributionHandlerServiceRef
	 */
	protected final void bindContributorBundleHandler(
			ContributionHandlerService contributionHandlerService) {
		this.contributionHandlerServiceRef.set(contributionHandlerService);
		trace("Bound ContributionHandlerService '{}' for extender component '{}'.",
				contributionHandlerService.getClass().getName(), this
						.getClass().getName());
	}

	protected Filter buildFilter() throws ExceptionComponentLifecycle {

		Filter contributionHandlerServiceFilter;

		// used by the contribution handler service tracker...
		StringBuilder filterStr = new StringBuilder("(&");

		if (targetContributionHandlerService != null
				&& !targetContributionHandlerService.isEmpty()) {
			filterStr.append("(" + ComponentConstants.COMPONENT_NAME + "="
					+ targetContributionHandlerService + ")");
		}

		if (contributionItemResourceType != null) {
			filterStr.append("("
					+ EXTENDER_SERVICE_ATTR_CONTRIBUTION_ITEM_RESOURCE_TYPE
					+ "=" + contributionItemResourceType.getIdent() + ")");
		}
		if (extensionHandlingStrategy != null) {
			filterStr.append("("
					+ EXTENDER_SERVICE_ATTR_EXTENSION_HANDLING_STRATEGY + "="
					+ extensionHandlingStrategy.getIdent() + ")");
		}

		filterStr.append(")");

		// create the filter that will be used to track the contribution
		// handler service
		try {
			contributionHandlerServiceFilter = getBundleContext().createFilter(
					filterStr.toString());
		} catch (InvalidSyntaxException e) {
			throw new ExceptionComponentLifecycle(e);
		}

		trace("processed the following filter: '" + filterStr.toString());

		return contributionHandlerServiceFilter;
	}

	protected void closeContributorBundleTracker() {
		if (contributorBundleTracker != null) {
			contributorBundleTracker.close();
			trace("End listening for bundles with "
					+ getExtenderContributorManifestHeader()
					+ "manifest header...");
		}
	}

	@Override
	public void afterDeactivate() throws ExceptionComponentLifecycle {

		doFirstLevelDeactivation();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lunifera.runtime.utils.osgi.component.ManageableComponent#
	 * doFirstLevelDeactivation()
	 */
	public void doFirstLevelDeactivation() throws ExceptionComponentLifecycle {
		if (contributorBundleTracker != null) {
			contributorBundleTracker.close();
			contributorBundleTracker = null;
		}
		if (contributionHandlerServiceTracker != null) {
			contributionHandlerServiceTracker.close();
			contributionHandlerServiceTracker = null;
		}

		extenderContributorManifestHeader = null;
		contributionItemResourceType = null;
		extensionHandlingStrategy = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.lunifera.runtime.utils.osgi.component.AbstractComponentManageable
	 * #doOpenServiceTrackers()
	 */
	protected void doOpenServiceTrackers() throws ExceptionComponentLifecycle {

		if (contributionHandlerServiceFilter == null) {
			contributionHandlerServiceFilter = buildFilter();
		}
		if (contributionHandlerServiceFilter == null) {
			throw new ExceptionComponentUnrecoveredActivationError(
					"The extender class requires a proper configuration of the @ComponentExtenderSetup annotation.");
		}
		// open the service tracker...
		getContributionHandlerServiceTracker().open();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.lunifera.runtime.utils.osgi.component.AbstractComponentManageable
	 * #doProcessProperties(java.util.Dictionary)
	 */
	protected void doProcessProperties(Dictionary<String, Object> properties) {

		Object targetContributionHandlerServiceLoc = properties
				.get(EXTENDER_SERVICE_ATTR_CONTRIBUTION_HANDLER_SERVICE);

		Object contributionItemResourceTypeLoc = properties
				.get(EXTENDER_SERVICE_ATTR_CONTRIBUTION_ITEM_RESOURCE_TYPE);

		Object extenderContributorManifestHeaderLoc = properties
				.get(EXTENDER_SERVICE_ATTR_MANIFEST_HEADER_NAME);

		Object stateMaskLoc = properties
				.get(EXTENDER_SERVICE_LOOKUP_STATE_MASK);

		if (contributionItemResourceTypeLoc instanceof String) {
			contributionItemResourceType = ContributionItemResourceType
					.fromString((String) contributionItemResourceTypeLoc);
		}
		if (contributionItemResourceTypeLoc instanceof String) {
			extensionHandlingStrategy = ExtensionHandlingStrategy
					.fromString((String) properties
							.get(EXTENDER_SERVICE_ATTR_EXTENSION_HANDLING_STRATEGY));
		}

		if (targetContributionHandlerServiceLoc instanceof String) {
			targetContributionHandlerService = (String) targetContributionHandlerServiceLoc;
		}

		// used by the bundle tracker
		if (extenderContributorManifestHeaderLoc instanceof String) {
			extenderContributorManifestHeader = (String) extenderContributorManifestHeaderLoc;
		}

		if (stateMaskLoc instanceof Integer) {

			stateMask = (int) stateMaskLoc;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.lunifera.runtime.utils.osgi.component.AbstractComponentManageable
	 * #doProcessRuntimeAnnotations()
	 */
	protected void doProcessRuntimeAnnotations()
			throws ExceptionComponentLifecycle {
		if (this.getClass().isAnnotationPresent(ComponentExtenderSetup.class)) {

			ComponentExtenderSetup extenderSetupAnnotation = this.getClass()
					.getAnnotation(ComponentExtenderSetup.class);

			targetContributionHandlerService = extenderSetupAnnotation
					.targetContributionHandlerServicePID();

			contributionItemResourceType = extenderSetupAnnotation
					.contributionItemResourceType();

			extensionHandlingStrategy = extenderSetupAnnotation.strategy();

			// used by the bundle tracker
			extenderContributorManifestHeader = extenderSetupAnnotation
					.manifestHeaderName();

			stateMask = extenderSetupAnnotation.stateMask();

		}
	}

	/**
	 * 
	 * @return
	 */
	protected ContributionHandlerService getContributionHandlerService() {
		return contributionHandlerServiceRef.get();
	}

	/**
	 * @return
	 */
	protected Filter getContributionHandlerServiceFilter() {
		return contributionHandlerServiceFilter;
	}

	protected ServiceTracker<ContributionHandlerService, ContributionHandlerService> getContributionHandlerServiceTracker() {
		if (contributionHandlerServiceTracker == null) {
			contributionHandlerServiceTracker = new ServiceTracker<ContributionHandlerService, ContributionHandlerService>(
					getBundleContext(),
					contributionHandlerServiceFilter,
					new ServiceTrackerCustomizer<ContributionHandlerService, ContributionHandlerService>() {

						@Override
						public ContributionHandlerService addingService(
								ServiceReference<ContributionHandlerService> reference) {
							ContributionHandlerService service = getBundleContext()
									.getService(reference);
							contributionHandlerServiceRef.set(service);
							try {
								openContributorBundleTracker();
							} catch (ExceptionComponentLifecycle e) {
								error("Error when activating contributor bundle tracker.",
										e);
							}
							return service;
						}

						@Override
						public void modifiedService(
								ServiceReference<ContributionHandlerService> reference,
								ContributionHandlerService service) {
						}

						@Override
						public void removedService(
								ServiceReference<ContributionHandlerService> reference,
								ContributionHandlerService service) {
							contributionHandlerServiceRef.compareAndSet(
									service, null);
							closeContributorBundleTracker();
						}
					});
		}
		return contributionHandlerServiceTracker;
	}

	/**
	 * @return the contributorBundleTracker
	 */
	protected ContributorBundleTracker getContributorBundleTracker() {
		return contributorBundleTracker;
	}

	/**
	 * 
	 * @return
	 */
	public String getExtenderContributorManifestHeader() {
		return extenderContributorManifestHeader;
	}

	/**
	 * @return the extensionHandlingStrategy
	 */
	public ExtensionHandlingStrategy getExtensionStrategy() {
		return extensionHandlingStrategy;
	}

	/**
	 * @return the contributionItemResourceType
	 */
	public ContributionItemResourceType getManifestHeaderItemType() {
		return contributionItemResourceType;
	}

	private void openContributorBundleTracker()
			throws ExceptionComponentLifecycle {
		if (contributorBundleTracker == null) {
			contributorBundleTracker = new ContributorBundleTracker(this,
					stateMask);
		}
		contributorBundleTracker.open();
		trace("Now listening for bundles with "
				+ getExtenderContributorManifestHeader() + "manifest header...");
	}

	/**
	 * This method used to test purposes only. It should not be accessed by any
	 * other code.
	 * 
	 * @param bundleTracker
	 * 
	 */
	protected final void setContributorBundleTracker(
			ContributorBundleTracker bundleTracker) {
		contributorBundleTracker = bundleTracker;
	}

	/**
	 * 
	 * @param contributionHandlerService
	 */
	protected final void unbindContributorBundleHandler(
			ContributionHandlerService contributionHandlerService) {
		trace("Unbound ContributionHandlerService '{}' for component '{}'.",
				contributionHandlerService.getClass().getName(), this
						.getClass().getName());
		this.contributionHandlerServiceRef.compareAndSet(
				contributionHandlerService, null);
	}

}
