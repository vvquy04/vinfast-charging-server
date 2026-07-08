package com.vanquy.evcserver.util;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class ImageUtil {

    public static String sanitizeStationImage(String urlOrFilename) {
        if (urlOrFilename == null) {
            return null;
        }
        String fileName = urlOrFilename.trim();
        if (fileName.contains("/uploads/stations/")) {
            fileName = fileName.substring(fileName.lastIndexOf("/uploads/stations/") + "/uploads/stations/".length());
        }
        return fileName;
    }

    public static String sanitizeAvatarImage(String urlOrFilename) {
        if (urlOrFilename == null) {
            return null;
        }
        String fileName = urlOrFilename.trim();
        if (fileName.contains("/uploads/avatars/")) {
            fileName = fileName.substring(fileName.lastIndexOf("/uploads/avatars/") + "/uploads/avatars/".length());
        }
        return fileName;
    }

    public static String getStationImageUrl(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return null;
        }
        if (fileName.startsWith("http://") || fileName.startsWith("https://")) {
            return fileName;
        }
        try {
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/stations/")
                    .path(fileName)
                    .toUriString();
        } catch (Exception e) {
            return "http://localhost:8080/uploads/stations/" + fileName;
        }
    }

    public static String getAvatarImageUrl(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return null;
        }
        if (fileName.startsWith("http://") || fileName.startsWith("https://")) {
            return fileName;
        }
        try {
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/avatars/")
                    .path(fileName)
                    .toUriString();
        } catch (Exception e) {
            return "http://localhost:8080/uploads/avatars/" + fileName;
        }
    }

    public static String sanitizeCheckinImage(String urlOrFilename) {
        if (urlOrFilename == null) {
            return null;
        }
        String fileName = urlOrFilename.trim();
        if (fileName.contains("/uploads/checkins/")) {
            fileName = fileName.substring(fileName.lastIndexOf("/uploads/checkins/") + "/uploads/checkins/".length());
        }
        return fileName;
    }

    public static String getCheckinImageUrl(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return null;
        }
        String cleanName = sanitizeCheckinImage(fileName);
        try {
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/checkins/")
                    .path(cleanName)
                    .toUriString();
        } catch (Exception e) {
            return "http://localhost:8080/uploads/checkins/" + cleanName;
        }
    }
}
