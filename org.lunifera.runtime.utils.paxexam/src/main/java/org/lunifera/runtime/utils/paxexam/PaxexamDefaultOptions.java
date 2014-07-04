package org.lunifera.runtime.utils.paxexam;

/*
 * #%L
 * Lunifera Subsystems - Runtime Utilities for PaxExam integration tests.
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
import static org.ops4j.pax.exam.CoreOptions.streamBundle;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.when;
import static org.osgi.framework.Constants.BUNDLE_ACTIVATIONPOLICY;
import static org.osgi.framework.Constants.BUNDLE_SYMBOLICNAME;
import static org.osgi.framework.Constants.BUNDLE_VERSION;
import static org.osgi.framework.Constants.EXPORT_PACKAGE;
import static org.osgi.framework.Constants.IMPORT_PACKAGE;

import org.lunifera.runtime.utils.paxexam.junit.AbstractPaxexamIntegrationTestClass;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;
import org.ops4j.pax.exam.options.CompositeOption;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.options.UrlProvisionOption;
import org.ops4j.pax.exam.options.libraries.JUnitBundlesOption;
import org.ops4j.pax.tinybundles.core.TinyBundles;

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
	 * Currently the options are: equinox_kepler, equinox_luna, felix.
	 */
	public final static String PAXEXAM_FRAMEWORK_PROPERTY = "pax.exam.framework";

	public final static String FRAMEWORK_IN_USE = System
			.getProperty(PAXEXAM_FRAMEWORK_PROPERTY);

	private static Option addCodeCoverageOption() {
		String coverageCommand = System.getProperty(COVERAGE_COMMAND);
		System.out.println("jacoco: " + coverageCommand);
		if (coverageCommand != null) {
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

		// repository(
		// "http://maven.lunifera.org:8086/nexus/content/repositories/releases/")
		// .id("lunifera"),
		options.add(junitBundles());
		options.add(when(cleanCaches).useOptions(cleanCaches()));
		if (isFelix()) {
			options.add(felix());
		} else if (isEquinoxLuna()) {
			options.add(equinox());
		} else if (isEquinoxKepler()) {
			options.add(equinox());
		}
		options.add(mavenBundle("org.lunifera.osgi", "javax.persistence")
				.versionAsInProject());
		options.add(mavenBundle("org.lunifera.osgi",
				"org.eclipse.equinox.region").versionAsInProject());
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
		options.add(mavenBundle("ch.qos.logback", "logback-classic")
				.versionAsInProject());
		if (extraOptions != null) {
			options.add(extraOptions);
		}
		options.add(addCodeCoverageOption());
		options.add(buildParentItestBundle());
		options.add(systemProperty("eclipse.consoleLog").value("true"),
				systemProperty("eclipse.log.level").value("DEBUG"),
				systemProperty("lunifera.logging.level").value("trace"));
		return OptionUtils.expand(options);
	}

	public static UrlProvisionOption buildParentItestBundle() {
		return streamBundle(TinyBundles
				.bundle()
				.add(AbstractPaxexamIntegrationTestClass.class)
				.add(PaxexamDefaultOptions.class)
				.set(BUNDLE_ACTIVATIONPOLICY, "lazy")
				.set(EXPORT_PACKAGE, "org.lunifera.runtime.utils.paxexam.junit")
				.set(BUNDLE_SYMBOLICNAME,
						"org.lunifera.runtime.utils.paxexam.junit")
				.set(BUNDLE_VERSION, "1.0.0")
				.set(IMPORT_PACKAGE,
						"org.lunifera.runtime.utils.paxexam.junit,"
								+ "javax.inject,org.junit,org.ops4j.pax.exam;version=\"[3.5,4)\","
								+ "org.ops4j.pax.exam.options;version=\"[3.5,4)\",org.ops4j.pax.exam.options.extra;version=\"[3.5,4)\","
								+ "org.osgi.framework;version=\"[1.6,2)\",org.osgi.util.tracker;version=\"[1.5,2)\"")
				.build(TinyBundles.withClassicBuilder()));
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
				.startLevel(1));
		options.add(mavenBundle("org.apache.felix",
				"org.apache.felix.eventadmin").versionAsInProject().startLevel(
				2));
		options.add(mavenBundle("org.apache.felix",
				"org.apache.felix.gogo.runtime").start().versionAsInProject());
		options.add(mavenBundle("org.apache.felix", "org.apache.felix.metatype")
				.versionAsInProject());
		options.add(mavenBundle("org.apache.felix", "org.apache.felix.prefs")
				.versionAsInProject().startLevel(2));
		options.add(mavenBundle("org.apache.felix", "org.apache.felix.log")
				.versionAsInProject().startLevel(1));
		options.add(mavenBundle("org.apache.felix", "org.apache.felix.scr")
				.versionAsInProject().startLevel(2));
		options.add(mavenBundle("org.apache.felix", "org.apache.felix.resolver")
				.version("1.0.0").startLevel(2));
		options.add(mavenBundle("org.apache.felix",
				"org.apache.felix.coordinator").version("1.0.0").startLevel(2));
		options.add(frameworkProperty(
				"org.osgi.framework.system.packages.extra")
				.value("org.ops4j.pax.exam;version=3.5.0,org.ops4j.pax.exam.options;version=3.5.0,org.ops4j.pax.exam.util;version=3.5.0,org.w3c.dom.traversal"));

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

	public static boolean isEquinoxKepler() {
		String env = System.getProperty(PAXEXAM_FRAMEWORK_PROPERTY);
		return "equinox_kepler".equals(env) || "equinox-kepler".equals(env);
	}

	public static boolean isEquinoxLuna() {
		String env = System.getProperty(PAXEXAM_FRAMEWORK_PROPERTY);
		return "equinox_luna".equals(env) || "equinox-luna".equals(env);
	}

	public static boolean isFelix() {

		return "felix".equals(System.getProperty(PAXEXAM_FRAMEWORK_PROPERTY));
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

		options.add(new OrbitJUnitBundlesOption());
		options.add(systemProperty("pax.exam.invoker").value("junit"));
		options.add(bundle("link:classpath:META-INF/links/org.ops4j.pax.exam.invoker.junit.link"));

		options.add(mavenBundle("org.lunifera.osgi", "org.hamcrest.integration")
				.versionAsInProject());
		options.add(mavenBundle("org.lunifera.osgi", "org.hamcrest.library")
				.versionAsInProject());
		options.add(mavenBundle("org.lunifera.osgi", "org.hamcrest.core")
				.versionAsInProject());
		return options;
	}

}
