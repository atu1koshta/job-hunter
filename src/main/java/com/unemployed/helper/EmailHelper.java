package com.unemployed.helper;

public class EmailHelper {
    public static String addJobIdToBody(String body, String jobId) {
        if (jobId != null && !jobId.isEmpty()) {
            body += "<p style='margin: 0;'><strong style='background-color: yellow; font-size: 16px;'>Job ID: <span style='background-color: yellow; font-size: 16px;'>" + jobId + "</span></strong></p>";
        }
        return body;
    }
}
