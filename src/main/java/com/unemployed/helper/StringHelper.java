package com.unemployed.helper;

public class StringHelper {
    public static String removeBOM(String content) {
        if (content.startsWith("\uFEFF")) {
            content = content.substring(1);
        }
        return content;
    }
}
