package com.vanquy.evcserver.util;

public class GenderUtil {
    public static String standardize(String gender) {
        if (gender == null) {
            return null;
        }
        String clean = gender.trim().toUpperCase();
        if (clean.equals("NAM") || clean.equals("MALE") || clean.equals("M")) {
            return "MALE";
        }
        if (clean.equals("NỮ") || clean.equals("NU") || clean.equals("FEMALE") || clean.equals("F")) {
            return "FEMALE";
        }
        if (clean.equals("KHÁC") || clean.equals("KHAC") || clean.equals("OTHER") || clean.equals("O")) {
            return "OTHER";
        }
        return "OTHER";
    }
}
