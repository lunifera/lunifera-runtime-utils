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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.framework.Bundle;

/**
 * A small object used to track the bundle used to contribute item to an
 * extender component.
 * 
 * @author Cristiano Gavião
 * @since 0.0.1
 * @noextend
 */
public class ContributorBundleDefault implements ContributorBundle {

    private final Bundle bundle;

    private List<ContributionItem> contributionItemList;

    public ContributorBundleDefault(Bundle contributorBundle) {
        if (contributorBundle == null) {
            throw new ExceptionComponentExtenderSetup(
                    "Contributor Bundle can't be null");
        }
        this.bundle = contributorBundle;
    }

    public void addContributionItem(ContributionItem contributionItem) {
        contributionItems().add(contributionItem);
    }

    public List<ContributionItem> contributionItems() {
        if (contributionItemList == null) {
            contributionItemList = new CopyOnWriteArrayList<>();
        }
        return contributionItemList;
    }

    public Bundle bundleWrapped() {
        return bundle;
    }

    public String symbolicName() {
        return bundleWrapped().getSymbolicName();
    }

    public String version() {
        return bundleWrapped().getVersion().toString();
    }
}
