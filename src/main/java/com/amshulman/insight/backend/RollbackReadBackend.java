package com.amshulman.insight.backend;

import com.amshulman.insight.query.QueryParameters;
import com.amshulman.insight.util.InsightConfigurationContext;

public class RollbackReadBackend extends AbstractCallbackReadBackend {

    public RollbackReadBackend(InsightConfigurationContext configurationContext) {
        super(configurationContext.getReadBackend());
    }

    public void rollback(String playerName, QueryParameters params, boolean force) {}
}
