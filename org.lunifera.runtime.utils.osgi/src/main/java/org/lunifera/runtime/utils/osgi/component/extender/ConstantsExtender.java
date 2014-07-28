package org.lunifera.runtime.utils.osgi.component.extender;

public interface ConstantsExtender {

    String EXTENDER_ATTR_CONTRIBUTION_HANDLER_ALIAS = "lunifera.extender.contribution.handler.alias";
    
    String EXTENDER_ATTR_CONTRIBUTION_ITEM_RESOURCE_TYPE = "lunifera.extender.contribution.item.resource.type";
    
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
}
