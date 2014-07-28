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

import static org.lunifera.runtime.utils.osgi.component.extender.ConstantsExtender.RESOURCE_TYPE_EMF_MODEL_FILE_RESOURCE;
import static org.lunifera.runtime.utils.osgi.component.extender.ConstantsExtender.RESOURCE_TYPE_PROPERTIES_FILE_RESOURCE;
import static org.lunifera.runtime.utils.osgi.component.extender.ConstantsExtender.RESOURCE_TYPE_PROPERTY_VALUE_PAIRS;
import static org.lunifera.runtime.utils.osgi.component.extender.ConstantsExtender.RESOURCE_TYPE_SINGLE_CLASS_NAME;
import static org.lunifera.runtime.utils.osgi.component.extender.ConstantsExtender.RESOURCE_TYPE_XTEXT_MODEL_FILE_RESOURCE;

import org.osgi.framework.wiring.BundleWiring;

/**
 * Defines the type of the manifest header item used in the contributor bundle
 * to extend other bundle. This will help to select the proper
 * {@link ContributionHandlerService}.
 * 
 * @author Cristiano Gavião
 * @since 0.0.1
 */
public enum ContributionItemResourceType {

    /**
     * 
     * <pre>
     * Header: http://myserver.com/resource_name.model
     * 
     * </pre>
     */
    EMF_MODEL_FILE_RESOURCE(RESOURCE_TYPE_EMF_MODEL_FILE_RESOURCE),

    /**
     * 
     * <pre>
     * Header: http://myserver.com/resource_name.model
     * 
     * </pre>
     */
    XTEXT_MODEL_FILE_RESOURCE(RESOURCE_TYPE_XTEXT_MODEL_FILE_RESOURCE),

    /**
     * Defines that one or more manifest header items should be treated as
     * resource file that must be searched and loaded from the bundle classpath.
     * <p>
     * 
     * The developer can use the entire resource name or wildcard. eg:
     * 
     * <pre>
     * Header: resource_name.cfg, values.properties, resource.*
     * 
     * </pre>
     * 
     * The developer can use a valid URL. eg:
     * 
     * <pre>
     * Header: http://myserver.com/resource_name.cfg, file://resource_name.cfg
     * </pre>
     * 
     * @see {@link BundleWiring#listResources(String path, String filePattern, int options)}
     */
    PROPERTIES_FILE_RESOURCE(RESOURCE_TYPE_PROPERTIES_FILE_RESOURCE),

    /**
     * Defines that one or more manifest header items should be treated as
     * property-value pairs. This must be used only with
     * {@link ContributionHandlingStrategy #PER_BUNDLE}.
     * <p>
     * 
     * The developer must use a valid property-value pair separated by comma.
     * The type could be informed after the property name followed by a colon.
     * If not informed the default type is String.eg:
     * 
     * <pre>
     * Header: propertyOne:int=123,properyTwo:String=value,properyTwo:String=value
     * </pre>
     */
    PROPERTY_VALUE_PAIRS(RESOURCE_TYPE_PROPERTY_VALUE_PAIRS),

    /**
     * The contribution is a Java class that needs to be loaded and
     * instantiated.
     * 
     * <pre>
     * Header: my.pack.MyClass
     * 
     * </pre>
     */
    SINGLE_CLASS_NAME(RESOURCE_TYPE_SINGLE_CLASS_NAME);

    public static ContributionItemResourceType fromString(String ident) {
        if (ident != null) {
            for (ContributionItemResourceType b : ContributionItemResourceType
                    .values()) {
                if (ident.equalsIgnoreCase(b.ident)) {
                    return b;
                }
            }
        }
        return null;
    }

    private final String ident;

    ContributionItemResourceType(String ident) {
        this.ident = ident;
    }

    public String getIdent() {
        return ident;
    }
}
