package com.amshulman.insight.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.amshulman.insight.query.QueryParameterBuilder;
import com.amshulman.insight.query.QueryParameters;
import com.amshulman.insight.results.InsightResultSet;

@Getter
@Setter
@NoArgsConstructor
public class PlayerInfo {

    private QueryParameters wandQueryParameters = new QueryParameterBuilder().build();
    private InsightResultSet lastResults = null;
    private int lastRequestedPage = -1;
}
