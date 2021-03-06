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

import static org.ops4j.pax.exam.Constants.START_LEVEL_SYSTEM_BUNDLES;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.options.AbstractDelegateProvisionOption;
import org.ops4j.pax.exam.options.MavenArtifactProvisionOption;

/**
 * This {@link Option} indicate to Pax-exam to use the Junit bundle provided by
 * Eclipse Orbit.
 * <p>
 * This is needed in order to use the Hamcrest bundles provided by Eclipse
 * Orbit.
 * 
 * @author cvgaviao
 * @since 0.0.1
 */
public class JUnitBundlesOrbitOption extends
        AbstractDelegateProvisionOption<JUnitBundlesOrbitOption> {

    /**
     * Constructor.
     */
    public JUnitBundlesOrbitOption() {
        super(mavenBundle("org.lunifera.osgi", "org.junit","4.11.0.v201303080030"));
        noUpdate();
        startLevel(START_LEVEL_SYSTEM_BUNDLES);
    }

    /**
     * Sets the junit version.
     * 
     * @param version
     *            junit version.
     * 
     * @return itself, for fluent api usage
     */
    public JUnitBundlesOrbitOption version(final String version) {
        ((MavenArtifactProvisionOption) getDelegate()).version(version);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("JUnitBundlesOrbitOption");
        sb.append("{url=").append(getURL());
        sb.append('}');
        return sb.toString();
    }

    protected JUnitBundlesOrbitOption itself() {
        return this;
    }
}
