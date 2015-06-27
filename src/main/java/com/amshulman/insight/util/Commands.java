package com.amshulman.insight.util;

import com.amshulman.mbapi.util.PermissionsEnum;

public enum Commands implements PermissionsEnum {
    INSIGHT;

    @Override
    public String getPrefix() {
        return "";
    }

    public enum InsightCommands implements PermissionsEnum {
        FORCEROLLBACK, LOOKUP, NEXT, ROLLBACK, PAGE, PREV, TP, WAND;

        @Override
        public String getPrefix() {
            return "insight.";
        }
    }
}
