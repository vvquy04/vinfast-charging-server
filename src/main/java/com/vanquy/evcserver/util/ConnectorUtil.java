package com.vanquy.evcserver.util;

public class ConnectorUtil {
    public static String standardize(String type) {
        if (type == null) {
            return null;
        }
        String clean = type.toLowerCase().trim();
        if (clean.contains("ccs2")) {
            return "CCS2";
        }
        if (clean.contains("type 2") || clean.contains("type2") || clean.contains("ac")) {
            return "AC";
        }
        if (clean.contains("chademo")) {
            return "CHAdeMO";
        }
        return type;
    }
}
