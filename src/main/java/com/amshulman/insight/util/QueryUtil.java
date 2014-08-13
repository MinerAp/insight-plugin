package com.amshulman.insight.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;

import com.amshulman.insight.action.InsightAction;
import com.amshulman.insight.parser.InsightParserErrorStrategy;
import com.amshulman.insight.parser.QueryLexer;
import com.amshulman.insight.parser.QueryParser;
import com.amshulman.insight.query.QueryParameterBuilder;
import com.amshulman.insight.query.QueryParameters;
import com.amshulman.insight.types.InsightMaterial;
import com.amshulman.mbapi.util.StringUtil;
import com.amshulman.typesafety.TypeSafeList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class QueryUtil {

    public static QueryParameters parseArgs(TypeSafeList<String> args) throws RecognitionException {
        QueryParser parser = new QueryParser(new CommonTokenStream(new QueryLexer(new ANTLRInputStream(StringUtil.remainingArgs(args, 0)))));
        parser.setErrorHandler(new InsightParserErrorStrategy());
        return parser.parse().queryParameters;
    }

    public static TypeSafeList<String> tabComplete(TypeSafeList<String> args) {
        return null;
    }

    public static void copyActors(QueryParameters from, QueryParameterBuilder to) {
        for (String actor : from.getActors()) {
            to.addActor(actor);
        }

        if (from.isInvertActors()) {
            to.invertActors();
        }
    }

    public static void copyActions(QueryParameters from, QueryParameterBuilder to) {
        for (InsightAction action : from.getActions()) {
            to.addAction(action);
        }

        if (from.isInvertActions()) {
            to.invertActions();
        }
    }

    public static void copyActees(QueryParameters from, QueryParameterBuilder to) {
        for (String actee : from.getActees()) {
            to.addActee(actee);
        }

        if (from.isInvertActees()) {
            to.invertActees();
        }
    }

    public static void copyMaterials(QueryParameters from, QueryParameterBuilder to) {
        for (InsightMaterial m : from.getMaterials()) {
            to.addMaterial(m);
        }

        if (from.isInvertMaterials()) {
            to.invertMaterials();
        }
    }

    public static void copyOrder(QueryParameters from, QueryParameterBuilder to) {
        if (from.isReverseOrder()) {
            to.reverseOrder();
        }
    }

    public static void copyTimes(QueryParameters from, QueryParameterBuilder to) {
        to.setAfter(from.getAfter());
        to.setBefore(from.getBefore());
    }

    public static void copyWorlds(QueryParameters from, QueryParameterBuilder to) {
        for (String world : from.getWorlds()) {
            to.addWorld(world);
        }
    }
}
