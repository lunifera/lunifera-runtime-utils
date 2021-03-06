package org.lunifera.runtime.utils.osgi.component.extender;

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

import static org.osgi.framework.Constants.SERVICE_PID;

public interface ConstantsExtender {
    
    String EXTENDER_SERVICE_PID = SERVICE_PID;

    String EXTENDER_ATTR_CONTRIBUTION_HANDLER_ALIAS = "lunifera.extender.contribution.handler.alias";
    
    String EXTENDER_ATTR_CONTRIBUTION_ITEM_RESOURCE_TYPE = "lunifera.extender.contribution.item.resource.type";

    String EXTENDER_ATTR_CONTRIBUTION_ITEM_RESOURCE_EXTENSIONS = "lunifera.extender.contribution.item.resource.extensions";
    
    String EXTENDER_ATTR_CONTRIBUTION_HANDLING_STRATEGY = "lunifera.extender.contribution.handling.strategy";
    
    String EXTENDER_ATTR_MANIFEST_HEADER_NAME = "lunifera.extender.manifest.header.name";
    
    String EXTENDER_ATTR_BUNDLE_STATE_MASK = "lunifera.extender.bundle.state.mask";
    
    // HANDLING STRATEGY
    String CONTRIBUTION_HANDLING_STRATEGY_PER_ITEM = "per_item";
    
    String CONTRIBUTION_HANDLING_STRATEGY_PER_BUNDLE = "per_bundle";
    
    String CONTRIBUTION_HANDLING_STRATEGY_PER_CATEGORY = "per_category";


    // RESOURCE_TYPE
    String RESOURCE_TYPE_EMF_MODEL_FILE_RESOURCE = "emf_model_file_resource";

    String RESOURCE_TYPE_XTEXT_MODEL_FILE_RESOURCE = "xtext_model_file_resource";
    
    String RESOURCE_TYPE_PROPERTIES_FILE_RESOURCE = "properties_file_resource";
    
    String RESOURCE_TYPE_PROPERTY_VALUE_PAIRS = "property_value_pairs";
    
    String RESOURCE_TYPE_SINGLE_CLASS_NAME = "single_class_name";

    String EXTENDER_CONTRIBUTOR_BUNDLE_NAME = "lunifera.extender.contributor.bundle";

    String EXTENDER_CONTRIBUTOR_BUNDLE_VERSION = "lunifera.extender.contributor.bundle.version";

    String EXTENDER_CONTRIBUTOR_ITEM = "lunifera.extender.contributor.item";
}
