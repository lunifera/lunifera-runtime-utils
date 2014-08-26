package org.lunifera.runtime.utils.osgi.component.extender;

import java.net.URL;
import java.util.Map;

import com.google.common.collect.Maps;

public class ContributionItemDefaultImpl implements ContributionItem {

    private final URL contributionResourceFileLocation;

    private final ContributorBundle contributorBundle;

    private final Map<String, Object> propertiesMap = Maps.newHashMap();

    public ContributionItemDefaultImpl(ContributorBundle contributorBundle,
            URL resourceLocation) {
        this(contributorBundle, resourceLocation, null);
    }

    public ContributionItemDefaultImpl(ContributorBundle contributorBundle,
            URL resourceLocation, Map<String, Object> properties) {
        this.contributionResourceFileLocation = resourceLocation;
        this.contributorBundle = contributorBundle;
        propertiesMap.put(ConstantsExtender.EXTENDER_CONTRIBUTOR_BUNDLE_NAME,
                contributorBundle.bundleWrapped().getSymbolicName());
        propertiesMap.put(
                ConstantsExtender.EXTENDER_CONTRIBUTOR_BUNDLE_VERSION,
                contributorBundle.bundleWrapped().getVersion().toString());
        propertiesMap.put(ConstantsExtender.EXTENDER_CONTRIBUTOR_ITEM,
                resourceLocation.getFile());
        if (properties != null && !properties.isEmpty()) {
            propertiesMap.putAll(properties);
        }
    }

    @Override
    public URL contributionResourceFileLocation() {
        return contributionResourceFileLocation;
    }

    @Override
    public ContributorBundle contributorBundle() {
        return contributorBundle;
    }

    @Override
    public Map<String, Object> propertiesMap() {
        return propertiesMap;
    }
}