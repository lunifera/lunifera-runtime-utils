package org.lunifera.runtime.utils.osgi.component.extender;

import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;

public class ContributionItemConfigAdminImpl extends
        ContributionItemDefaultImpl implements ContributionItemConfigAdmin {

    private boolean usingFactoryConfiguration;

    private final String targetPID;

    public ContributionItemConfigAdminImpl(
            ContributorBundle contributorBundleTrackerObject,
            URL contributionResourceLocation) {
        this(contributorBundleTrackerObject, contributionResourceLocation, null);

    }

    public ContributionItemConfigAdminImpl(
            ContributorBundle contributorBundleTrackerObject,
            URL contributionResourceLocation, Map<String, Object> properties) {
        super(contributorBundleTrackerObject, contributionResourceLocation,
                properties);
        targetPID = extractTargetPidFromResourceFileName();
    }

    protected String extractTargetPidFromResourceFileName() {
        String separator = "/";
        String filename;
        String configFile = contributionResourceFileLocation().getFile();

        // Remove the path up to the filename.
        int lastSeparatorIndex = configFile.lastIndexOf(separator);
        if (lastSeparatorIndex == -1) {
            filename = configFile;
        } else {
            filename = configFile.substring(lastSeparatorIndex + 1);
        }

        Matcher matcher = ContributionItemConfigAdmin.PATTERN_TARGET_PID
                .matcher(filename);
        if (!matcher.find()) {
            throw new ExceptionComponentExtenderSetup("Resource '" + configFile
                    + "' is not in the expected format.");
        }
        String pid = matcher.group(1);
        String bsn = matcher.group(3);
        String version = matcher.group(5);
        String location = matcher.group(10);
        // String extension = matcher.group(11);

        // it is a Factory configuration ?
        int qualifierIndex = pid.lastIndexOf('\u005f');
        if (qualifierIndex != -1) {
            usingFactoryConfiguration = true;
            pid = pid.substring(0, qualifierIndex);
        }
        StringBuilder s = new StringBuilder(pid);
        if (bsn != null) {
            s.append("|" + bsn);
        }
        if (version != null) {
            s.append("|" + version);
        }
        if (location != null) {
            s.append("|" + location);
        }
        return s.toString();
    }

    @Override
    public boolean usingFactoryConfiguration() {
        return usingFactoryConfiguration;
    }

    @Override
    public String targetPID() {
        return targetPID;
    }
}
