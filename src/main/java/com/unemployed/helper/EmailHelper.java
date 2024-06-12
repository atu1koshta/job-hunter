package com.unemployed.helper;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class EmailHelper {
    public static String addJobIdToBody(String body, String jobId) {
        if (jobId != null && !jobId.isEmpty()) {
            body += "<p style='margin: 0;'><strong style='background-color: yellow; font-size: 16px;'>Job ID: <span style='background-color: yellow; font-size: 16px;'>" + jobId + "</span></strong></p>";
        }
        return body;
    }
}
