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
