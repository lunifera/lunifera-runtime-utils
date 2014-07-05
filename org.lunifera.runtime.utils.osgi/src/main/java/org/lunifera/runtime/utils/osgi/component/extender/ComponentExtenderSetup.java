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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.osgi.framework.Bundle;

/**
 * Annotation used to configure an Extender component based on the
 * {@link AbstractComponentExtender}.
 * 
 * @author Cristiano Gavião
 * @since 0.0.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ComponentExtenderSetup {

    /**
     * Defines the type of the manifest header item required by the extender.
     * 
     * @return
     */
    ContributionItemResourceType contributionItemResourceType() default ContributionItemResourceType.PROPERTIES_FILE_RESOURCE;

    /**
     * The manifest header name that identifies a contributor bundle.
     * 
     * @return the name of manifest header that will be used to identify a
     *         contributor bundle.
     */
    String manifestHeaderName();

    /**
     * The bit mask of the {@code OR}ing of the bundle states to be tracked.
     * {@code eg: Bundle.ACTIVE|Bundle.RESOLVED}
     * 
     * @return
     */
    int stateMask() default Bundle.ACTIVE;

    /**
     * Defines how an extension bundle should be processed.
     * 
     * @return the default extension strategy.
     */
    ExtensionHandlingStrategy strategy() default ExtensionHandlingStrategy.PER_ITEM;

    /**
     * The default ContributionHandlerService service to reference. This will
     * used to compose a filter to track the right service.
     */
    String targetContributionHandlerServicePID() default "org.lunifera.runtime.utils.osgi.component.extender.handlers.ComponentContributionHandlerPropertiesFileResourcePerItemWithConfigAdmin";
}
