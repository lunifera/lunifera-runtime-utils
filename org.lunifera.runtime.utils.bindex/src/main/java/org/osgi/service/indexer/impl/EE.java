package org.osgi.service.indexer.impl;

/*
 * #%L
 * Lunifera Runtime Utilities - OSGi Repository Indexer
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
/*
 * Part of this code was borrowed from BIndex project (https://github.com/osgi/bindex) 
 * and it is released under OSGi Specification License, VERSION 2.0
 */
import org.osgi.framework.Version;

public final class EE {

	private String name;
	private String version;

	private EE() {
	}

	public static final EE parseBREE(String bree) {
		EE ee = new EE();

		int splitIndex = bree.indexOf("/");
		if (splitIndex >= 0) {
			Segment segment1 = versionSplit(bree.substring(0, splitIndex));
			if (segment1.name.equals("J2SE"))
				segment1.name = "JavaSE";

			Segment segment2 = versionSplit(bree.substring(splitIndex + 1));

			if (segment1.version != null) {
				if (segment1.version.equals(segment2.version)) {
					ee.name = segment1.name + "/" + segment2.name;
					ee.version = segment1.version;
				} else {
					StringBuilder builder = new StringBuilder().append(segment1.name).append('-').append(segment1.version).append('/').append(segment2.name);
					if (segment2.version != null)
						builder.append('-').append(segment2.version);
					ee.name = builder.toString();
					ee.version = null;
				}
			} else {
				ee.name = segment1.name + "/" + segment2.name;
				ee.version = segment2.version;
			}
		} else {
			Segment segment = versionSplit(bree);
			if (segment.name.equals("J2SE"))
				segment.name = "JavaSE";

			ee.name = segment.name;
			ee.version = segment.version;
		}

		return ee;
	}

	private static class Segment {
		private String name;
		private String version;
	}

	private static Segment versionSplit(String input) {
		Segment result = new Segment();
		int index = input.indexOf('-');
		if (index >= 0) {
			String name = input.substring(0, index);
			String versionStr = input.substring(index + 1);

			try {
				new Version(versionStr);
				result.name = name;
				result.version = versionStr;
			} catch (IllegalArgumentException e) {
				result.name = input;
				result.version = null;
			}
		} else {
			result.name = input;
			result.version = null;
		}
		return result;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String toFilter() {
		StringBuilder builder = new StringBuilder();
		builder.append("(osgi.ee=").append(name).append(")");

		if (version != null) {
			builder.insert(0, "(&");
			builder.append("(version=").append(version).append(")");
			builder.append(")");
		}

		return builder.toString();
	}

}
