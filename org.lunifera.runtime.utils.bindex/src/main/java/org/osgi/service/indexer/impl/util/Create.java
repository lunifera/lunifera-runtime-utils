package org.osgi.service.indexer.impl.util;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class Create {

	public static <K, V> Map<K, V> map() {
		return new LinkedHashMap<K, V>();
	}

	public static <K, V> Map<K, V> map(Class<K> key, Class<V> value) {
		return Collections.checkedMap(new LinkedHashMap<K, V>(), key, value);
	}

	public static <T> List<T> list() {
		return new ArrayList<T>();
	}

	public static <T> List<T> list(Class<T> c) {
		return Collections.checkedList(new ArrayList<T>(), c);
	}

	public static <T> Set<T> set() {
		return new HashSet<T>();
	}

	public static <T> Set<T> set(Class<T> c) {
		return Collections.checkedSet(new HashSet<T>(), c);
	}

	public static <T> List<T> list(T[] source) {
		return new ArrayList<T>(Arrays.asList(source));
	}

	public static <T> Set<T> set(T[] source) {
		return new HashSet<T>(Arrays.asList(source));
	}

	public static <K, V> Map<K, V> copy(Map<K, V> source) {
		return new LinkedHashMap<K, V>(source);
	}

	public static <T> List<T> copy(List<T> source) {
		return new ArrayList<T>(source);
	}

	public static <T> Set<T> copy(Collection<T> source) {
		if (source == null)
			return set();
		return new HashSet<T>(source);
	}

}
