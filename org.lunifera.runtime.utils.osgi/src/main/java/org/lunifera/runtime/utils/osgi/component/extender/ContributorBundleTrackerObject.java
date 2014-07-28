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

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationAdmin;

import com.google.common.collect.Maps;

/**
 * A small object used to track the bundle used to contribute item to an
 * extender component.
 * 
 * @author Cristiano Gavião
 * @since 0.0.1
 * @noextend
 */
public class ContributorBundleTrackerObject {

    public static final class ContributionItem {

        public static final String EXTENDER_CONTRIBUTOR_BUNDLE = "lunifera.extender.contributor.bundle";
        public static final String EXTENDER_CONTRIBUTOR_BUNDLE_VERSION = "lunifera.extender.contributor.bundle.version";
        public static final String EXTENDER_CONTRIBUTOR_ITEM = "lunifera.extender.contributor.item";

        private final ContributorBundleTrackerObject contributorBundleTrackerObject;
        private final Map<String, Object> propertiesMap = Maps.newHashMap();
        private final URL resourceLocation;

        /**
         * This constructor aims to be used with extensions using
         * {@link ContributionHandlingStrategy #PER_BUNDLE}.
         * 
         * @param contributorBundleTrackerObject
         * @param propertiesMap
         */
        public ContributionItem(
                ContributorBundleTrackerObject contributorBundleTrackerObject,
                Map<String, Object> properties) {
            this.resourceLocation = null;
            this.contributorBundleTrackerObject = contributorBundleTrackerObject;
            propertiesMap.put(EXTENDER_CONTRIBUTOR_BUNDLE,
                    contributorBundleTrackerObject.getContributorBundle()
                            .getSymbolicName());
            propertiesMap.put(EXTENDER_CONTRIBUTOR_BUNDLE_VERSION,
                    contributorBundleTrackerObject.getContributorBundle()
                            .getVersion().toString());
            propertiesMap.putAll(properties);
        }

        public ContributionItem(
                ContributorBundleTrackerObject contributorBundleTrackerObject,
                URL resourceLocation) {
            this.resourceLocation = resourceLocation;
            this.contributorBundleTrackerObject = contributorBundleTrackerObject;
            propertiesMap.put(EXTENDER_CONTRIBUTOR_BUNDLE,
                    contributorBundleTrackerObject.getContributorBundle()
                            .getSymbolicName());
            propertiesMap.put(EXTENDER_CONTRIBUTOR_BUNDLE_VERSION,
                    contributorBundleTrackerObject.getContributorBundle()
                            .getVersion().toString());
            propertiesMap.put(EXTENDER_CONTRIBUTOR_ITEM,
                    resourceLocation.getFile());
        }

        public ContributorBundleTrackerObject getContributorBundleHolder() {
            return contributorBundleTrackerObject;
        }

        public String getFactoryPid() {
            return (String) getPropertiesMap().get(
                    ConfigurationAdmin.SERVICE_FACTORYPID);
        }

        public String getFilter() {
            String filter = "";
            if (getFactoryPid() != null) {
                filter = "(&("
                        + ConfigurationAdmin.SERVICE_FACTORYPID
                        + "="
                        + getFactoryPid()
                        + ") ("
                        + EXTENDER_CONTRIBUTOR_BUNDLE
                        + "="
                        + contributorBundleTrackerObject.getContributorBundle()
                                .getSymbolicName() + ")("
                        + EXTENDER_CONTRIBUTOR_ITEM + "="
                        + resourceLocation.getFile() + "))";
            } else {
                filter = "(&("
                        + EXTENDER_CONTRIBUTOR_BUNDLE
                        + "="
                        + contributorBundleTrackerObject.getContributorBundle()
                                .getSymbolicName() + ")("
                        + EXTENDER_CONTRIBUTOR_ITEM + "="
                        + resourceLocation.getFile() + "))";
            }

            return filter;
        }

        public String getPid() {
            return (String) getPropertiesMap().get(Constants.SERVICE_PID);
        }

        public Map<String, Object> getPropertiesMap() {
            return propertiesMap;
        }

        public URL getResourceLocation() {
            return resourceLocation;
        }

        public void setFactoryPid(String factoryPid) {
            getPropertiesMap().put(ConfigurationAdmin.SERVICE_FACTORYPID,
                    factoryPid);
        }

        public void setPid(String pid) {
            getPropertiesMap().put(Constants.SERVICE_PID, pid);
        }
    }

    private final Bundle bundle;

    private List<ContributionItem> contributionItemList;

    public ContributorBundleTrackerObject(Bundle contributorBundle) {
        this.bundle = contributorBundle;
    }

    public void addContributionItem(Map<String, Object> properties) {
        ContributionItem contributionItem = new ContributionItem(this,
                properties);
        getContributionItemList().add(contributionItem);
    }

    public void addContributionItem(URL resourceLocation) {
        ContributionItem contributionItem = new ContributionItem(this,
                resourceLocation);
        getContributionItemList().add(contributionItem);
    }

    public void addContributionItem(URL resourceLocation,
            Map<String, String> properties) {
        ContributionItem contributionItem = new ContributionItem(this,
                resourceLocation);
        contributionItem.getPropertiesMap().putAll(properties);
        getContributionItemList().add(contributionItem);
    }

    public List<ContributionItem> getContributionItemList() {
        if (contributionItemList == null) {
            contributionItemList = new CopyOnWriteArrayList<>();
        }
        return contributionItemList;
    }

    public Bundle getContributorBundle() {
        return bundle;
    }
}
