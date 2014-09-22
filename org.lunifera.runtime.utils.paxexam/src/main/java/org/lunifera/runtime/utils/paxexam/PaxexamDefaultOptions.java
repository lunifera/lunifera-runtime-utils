package org.lunifera.runtime.utils.paxexam;

/*
 * #%L
 * Lunifera Runtime Utilities - for PaxExam.
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

import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.cleanCaches;
import static org.ops4j.pax.exam.CoreOptions.frameworkProperty;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.when;

import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;
import org.ops4j.pax.exam.options.CompositeOption;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.options.libraries.JUnitBundlesOption;
import org.ops4j.pax.exam.util.PathUtils;

/**
 * This class provides useful Pax-Exam options in order to setup integration
 * tests.
 * 
 * @author cvgaviao
 * @since 0.0.1
 */
public class PaxexamDefaultOptions {

    // the name of the system property which captures the jacoco coverage agent
    // command
    // if specified then agent would be specified otherwise ignored
    public static final String COVERAGE_COMMAND = "jacoco.agent.it.arg";

    public final static String LUNIFERA_OSGi_TRACE_PROPERTY = "lunifera.osgi.trace";

    public final static String OSGI_CONSOLE_PROPERTY = "osgi.console";

    /**
     * Property that should be used to specify which OSGi FRAMEWORK_IN_USE to
     * use.
     * <p>
     * Currently the options are: equinox_luna, felix.
     */
    public final static String OSGi_FRAMEWORK_PROPERTY = "lunifera.itests.framework";
    public final static String PAX_EXAM_VERSION_PROPERTY = "paxexam.version";
    public final static String PROJECT_VERSION_PROPERTY = "project.version";

    public final static String FRAMEWORK_IN_USE = System
            .getProperty(OSGi_FRAMEWORK_PROPERTY);

    public final static String PAX_EXAM_VERSION = System
            .getProperty(PAX_EXAM_VERSION_PROPERTY);

    public final static String PROJECT_VERSION = System
            .getProperty(PROJECT_VERSION_PROPERTY);

    private static Option addCodeCoverageOption() {
        String coverageCommand = System.getProperty(COVERAGE_COMMAND);
        if (coverageCommand != null) {
            System.out.println("Jacoco:" + coverageCommand);
            return CoreOptions.vmOption(coverageCommand);
        }
        return null;
    }

    public static Option[] basicNodeConfiguration() {
        return basicNodeConfiguration(false);
    }

    public static Option[] basicNodeConfiguration(boolean cleanCaches,
            Option... extraOptions) {

        DefaultCompositeOption options = new DefaultCompositeOption();
        options.add(when(cleanCaches).useOptions(cleanCaches()));
        options.add(junitBundles());
        System.out.println("OSGi Framework: " + FRAMEWORK_IN_USE);
        System.out.println("Pax-Exam version: " + PAX_EXAM_VERSION);
        System.out.println("Logback file: " + PathUtils.getBaseDir()
                + "/src/test/resources/logback-test.xml");
        if (isFelix()) {
            options.add(felix());
        } else
            if (isEquinoxLuna()) {
                options.add(equinox());
            } else
                if (isEquinoxMars()) {
                    options.add(equinox());
                }
        options.add(mavenBundle("org.lunifera.osgi", "javax.persistence")
                .versionAsInProject());
        options.add(mavenBundle("org.lunifera.osgi",
                "org.eclipse.equinox.region").noStart().versionAsInProject());
        options.add(mavenBundle("org.apache.felix",
                "org.apache.felix.bundlerepository").startLevel(2)
                .versionAsInProject());
        options.add(mavenBundle("org.apache.aries.subsystem",
                "org.apache.aries.subsystem.api").versionAsInProject());
        options.add(mavenBundle("org.apache.aries.subsystem",
                "org.apache.aries.subsystem.core").versionAsInProject());
        options.add(mavenBundle("org.apache.aries", "org.apache.aries.util")
                .versionAsInProject());
        options.add(mavenBundle("org.knowhowlab.osgi",
                "org.knowhowlab.osgi.testing.utils").versionAsInProject());
        options.add(mavenBundle("org.knowhowlab.osgi",
                "org.knowhowlab.osgi.testing.assertions").versionAsInProject());
        options.add(mavenBundle("com.google.guava", "guava")
                .versionAsInProject());
        options.add(mavenBundle("org.slf4j", "slf4j-api").versionAsInProject());
        options.add(mavenBundle("ch.qos.logback", "logback-core")
                .versionAsInProject());
//        options.add(mavenBundle("org.slf4j", "osgi-over-slf4j")
//                .versionAsInProject().start());
        options.add(mavenBundle("ch.qos.logback", "logback-classic")
                .versionAsInProject());
        if (extraOptions != null) {
            options.add(extraOptions);
        }
        options.add(addCodeCoverageOption());
        options.add(
                systemProperty("eclipse.consoleLog").value("true"),
                systemProperty("eclipse.log.level").value("DEBUG"),
                systemProperty("pax.exam.logging").value("none"),
                systemProperty("pax.exam.system").value("default"),
//                systemProperty("logback.configurationFile").value(
//                        "file:" + PathUtils.getBaseDir()
//                                + "/src/test/resources/logback-test.xml"),
                systemProperty("lunifera.logging.level").value("TRACE"));
        return OptionUtils.expand(options);
    }

    public static CompositeOption equinox() {
        DefaultCompositeOption options = new DefaultCompositeOption();

        options.add(mavenBundle("org.apache.felix",
                "org.apache.felix.gogo.runtime").start().versionAsInProject());
        options.add(mavenBundle("org.lunifera.osgi",
                "org.eclipse.osgi.services").versionAsInProject());
        options.add(mavenBundle("org.lunifera.osgi", "org.eclipse.equinox.ds")
                .versionAsInProject().startLevel(3));
        options.add(mavenBundle("org.lunifera.osgi", "org.eclipse.equinox.cm")
                .startLevel(1).versionAsInProject());
        options.add(mavenBundle("org.lunifera.osgi",
                "org.eclipse.equinox.event").versionAsInProject().startLevel(2));
        options.add(mavenBundle("org.lunifera.osgi",
                "org.eclipse.equinox.preferences").versionAsInProject()
                .startLevel(2));
        options.add(mavenBundle("org.lunifera.osgi", "org.eclipse.equinox.util")
                .versionAsInProject().startLevel(2));
        options.add(mavenBundle("org.lunifera.osgi",
                "org.eclipse.equinox.common").versionAsInProject());
        options.add(mavenBundle("org.lunifera.osgi", "org.eclipse.osgi.util")
                .startLevel(2).versionAsInProject());
        options.add(mavenBundle("org.lunifera.osgi",
                "org.eclipse.equinox.coordinator").versionAsInProject());

        if (isConsoleOn()) {
            options.add(mavenBundle("org.apache.felix",
                    "org.apache.felix.gogo.command").start()
                    .versionAsInProject());
            options.add(mavenBundle("org.apache.felix",
                    "org.apache.felix.gogo.shell").start().versionAsInProject());
            options.add(mavenBundle("org.lunifera.osgi",
                    "org.eclipse.equinox.console").start().versionAsInProject());
        }

        return options;
    }

    public static CompositeOption felix() {
        DefaultCompositeOption options = new DefaultCompositeOption();
        options.add(mavenBundle("org.apache.felix",
                "org.apache.felix.configadmin").versionAsInProject()
                .startLevel(2));
        options.add(mavenBundle("org.apache.felix",
                "org.apache.felix.eventadmin").versionAsInProject().startLevel(
                1));
        options.add(mavenBundle("org.apache.felix",
                "org.apache.felix.gogo.runtime").start().versionAsInProject());
        options.add(mavenBundle("org.apache.felix", "org.apache.felix.metatype")
                .versionAsInProject());
        options.add(mavenBundle("org.apache.felix", "org.apache.felix.prefs")
                .versionAsInProject().startLevel(3));
        options.add(mavenBundle("org.apache.felix", "org.apache.felix.log")
                .versionAsInProject().startLevel(1));
        options.add(mavenBundle("org.apache.felix", "org.apache.felix.scr")
                .versionAsInProject().startLevel(2));
        options.add(mavenBundle("org.apache.felix", "org.apache.felix.resolver")
                .version("1.0.0").startLevel(2));
        options.add(mavenBundle("org.apache.felix",
                "org.apache.felix.coordinator").version("1.0.0").startLevel(2));
        options.add(frameworkProperty(
                "org.osgi.framework.system.packages.extra").value(
                "org.ops4j.pax.exam;version=\"" + PAX_EXAM_VERSION
                        + "\",org.ops4j.pax.exam.options;version=\""
                        + PAX_EXAM_VERSION
                        + "\",org.ops4j.pax.exam.util;version=\""
                        + PAX_EXAM_VERSION + "\",org.w3c.dom.traversal"));

        if (isConsoleOn()) {
            options.add(mavenBundle("org.lunifera.osgi",
                    "org.apache.felix.gogo.command").start()
                    .versionAsInProject());
            options.add(mavenBundle("org.lunifera.osgi",
                    "org.apache.felix.gogo.shell").start().versionAsInProject());
        }

        return options;
    }

    public static boolean isConsoleOn() {
        String port = System.getProperty(OSGI_CONSOLE_PROPERTY);
        if (port != null && !port.isEmpty())
            return true;
        else
            return false;
    }

    public static boolean isEquinoxMars() {
        return "equinox_mars".equals(FRAMEWORK_IN_USE)
                || "equinox-mars".equals(FRAMEWORK_IN_USE);
    }

    public static boolean isEquinoxLuna() {
        return "equinox_luna".equals(FRAMEWORK_IN_USE)
                || "equinox-luna".equals(FRAMEWORK_IN_USE);
    }

    public static boolean isFelix() {
        return "felix".equals(FRAMEWORK_IN_USE);
    }

    public static boolean isTraceOn() {
        String trace = System.getProperty(LUNIFERA_OSGi_TRACE_PROPERTY);
        return "true".equals(trace) || "on".equals(trace);
    }

    /**
     * Creates a {@link JUnitBundlesOption}.
     * 
     * @return junit bundles option
     */
    public static CompositeOption junitBundles() {
        DefaultCompositeOption options = new DefaultCompositeOption();
        options.add(new JUnitBundlesOrbitOption());
        options.add(systemProperty("pax.exam.invoker").value("junit"));
        options.add(bundle("link:classpath:META-INF/links/org.ops4j.pax.exam.invoker.junit.link"));
        // options.add(bundle("link:classpath:META-INF/links/org.ops4j.pax.tipi.hamcrest.core.link"));
        options.add(mavenBundle("org.lunifera.osgi",
                "org.hamcrest.integration", "1.3.0.v201305210900"));
        options.add(mavenBundle("org.lunifera.osgi", "org.hamcrest.library",
                "1.3.0.v201305281000"));
        options.add(mavenBundle("org.lunifera.osgi", "org.hamcrest.core",
                "1.3.0.v201303031735"));
        return options;
    }

}
