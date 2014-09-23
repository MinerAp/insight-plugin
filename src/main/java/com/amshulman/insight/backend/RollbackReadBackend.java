package com.amshulman.insight.backend;

import com.amshulman.insight.util.InsightConfigurationContext;

public class RollbackReadBackend extends AbstractCallbackReadBackend {

    public RollbackReadBackend(InsightConfigurationContext configurationContext) {
        super(configurationContext.getReadBackend());
        // TODO Auto-generated constructor stub
    }
}
