package org.lunifera.runtime.utils.osgi.component.extender;

import java.util.List;

import org.osgi.framework.Bundle;

public interface ContributorBundle {

    public void addContributionItem(ContributionItem contributionItem);

    public List<ContributionItem> contributionItems();
    
    public Bundle bundleWrapped();

    public String symbolicName();

    public String version();
}
