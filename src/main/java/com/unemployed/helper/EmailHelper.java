package com.unemployed.helper;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class EmailHelper {
    private static File createCoverLetterPdf(String templatePath, String name, String company) throws IOException {
        // Read the template file
        String content = new String(Files.readAllBytes(Paths.get(templatePath)));

        // Replace placeholders
        content = content.replace("{NAME}", name);
        content = content.replace("{COMPANY}", company);

        // Create a temporary file
        File tempFile = File.createTempFile("cover_letter", ".pdf");

        // Convert the content to PDF
        PdfWriter writer = new PdfWriter(tempFile);
        Document document = new Document(new com.itextpdf.kernel.pdf.PdfDocument(writer));
        document.add(new Paragraph(content));
        document.close();

        System.out.printf("Cover letter(%s): %s%n", company, tempFile.getAbsolutePath());
        return tempFile;
    }
}
