package com.amshulman.insight.backend;

import com.amshulman.insight.query.QueryParameters;
import com.amshulman.insight.util.InsightConfigurationContext;

public class RollbackReadBackend extends AbstractCallbackReadBackend {

    public RollbackReadBackend(InsightConfigurationContext configurationContext) {
        super(configurationContext.getReadBackend());
    }

    public void rollback(final String playerName, final QueryParameters params, final boolean fancyResults) {}
}
