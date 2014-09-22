package org.lunifera.runtime.utils.bindex.subsystems;

/*
 * #%L
 * Lunifera Runtime Utilities - OSGi Subsystem ResourceAnalyzer for BIndex
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

import java.util.List;

import org.apache.aries.subsystem.core.archive.SubsystemManifest;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.indexer.Builder;
import org.osgi.service.indexer.Capability;
import org.osgi.service.indexer.Namespaces;
import org.osgi.service.indexer.Requirement;
import org.osgi.service.indexer.Resource;
import org.osgi.service.indexer.ResourceAnalyzer;

@Component(enabled = true, immediate = true, property={"filter=(name=*.esa)"})
public class SubsystemResourceAnalyzer implements ResourceAnalyzer {

    public SubsystemResourceAnalyzer() {
    }

    @Override
    public void analyzeResource(Resource resource,
            List<Capability> capabilities, List<Requirement> requirements)
            throws Exception {

        try {
            Resource io = resource.getChild("OSGI-INF/SUBSYSTEM.MF");
            if (io == null){
                return;
            }
            SubsystemManifest subsystemManifest = new SubsystemManifest(
                    io.getStream());
            capabilities.add(processIdentity(subsystemManifest));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Capability processIdentity(SubsystemManifest subsystemManifest) {
        Builder builder = new Builder()
                .setNamespace(Namespaces.NS_IDENTITY)
                .addAttribute(
                        Namespaces.NS_IDENTITY,
                        subsystemManifest.getSubsystemSymbolicNameHeader()
                                .getSymbolicName())
                .addAttribute(Namespaces.ATTR_IDENTITY_TYPE,
                        subsystemManifest.getSubsystemTypeHeader().getType())
                .addAttribute(
                        Namespaces.ATTR_VERSION,
                        subsystemManifest.getSubsystemVersionHeader()
                                .getVersion());
        return builder.buildCapability();
    }

}
