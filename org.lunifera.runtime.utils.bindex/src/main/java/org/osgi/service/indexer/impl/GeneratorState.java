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
 * and it is released under OSGi Specification License, Version 2.0
 */
import java.net.URL;

class GeneratorState {

	private final URL rootUrl;
	private final String urlTemplate;

	public GeneratorState(URL rootUrl, String urlTemplate) {
		this.rootUrl = rootUrl;
		this.urlTemplate = urlTemplate;
	}

	public URL getRootUrl() {
		return rootUrl;
	}

	public String getUrlTemplate() {
		return urlTemplate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rootUrl == null) ? 0 : rootUrl.hashCode());
		result = prime * result + ((urlTemplate == null) ? 0 : urlTemplate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeneratorState other = (GeneratorState) obj;
		if (rootUrl == null) {
			if (other.rootUrl != null)
				return false;
		} else if (!rootUrl.equals(other.rootUrl))
			return false;
		if (urlTemplate == null) {
			if (other.urlTemplate != null)
				return false;
		} else if (!urlTemplate.equals(other.urlTemplate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GeneratorState [rootUrl=" + rootUrl + ", urlTemplate=" + urlTemplate + "]";
	}

}
