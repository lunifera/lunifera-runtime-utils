package org.lunifera.runtime.utils.osgi.component.extender;

import java.net.URL;
import java.util.Map;

public interface ContributionItem {
    
    ContributorBundle contributorBundle();
    
    public Map<String, Object> propertiesMap();
    
    URL contributionResourceFileLocation();
}
