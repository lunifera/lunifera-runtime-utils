package org.lunifera.runtime.utils.osgi.component.extender;

import java.util.regex.Pattern;

public interface ContributionItemConfigAdmin extends ContributionItem {

    String REGEX_TARGET_PID = "([a-zA-Z0-9\\.\\_]+)(\\|([a-zA-Z0-9\\.\\_]+)(\\|(\\d+(\\.\\d+(\\.\\d+(\\.[a-zA-Z0-9\\_]+)?)?)?)(\\|([a-zA-Z0-9\\.\\_]+))?)?)?(\\.config|\\.conf|\\.cfg)";

    Pattern PATTERN_TARGET_PID = Pattern.compile(REGEX_TARGET_PID);

    boolean usingFactoryConfiguration();

    String targetPID();
}
