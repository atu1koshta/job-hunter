package com.unemployed.message;

import com.unemployed.model.EmailContent;
import com.unemployed.service.EmailService;
import com.unemployed.service.GoogleSheetService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JobEmailSender extends MessageSender {
    private final String spreadSheetId = "1XwEQae9Z6-nest8okXG6kKfygGkKYdb4XVt5eptAraA";

    private static String removeBOM(String content) {
        if (content.startsWith("\uFEFF")) {
            content = content.substring(1);
        }
        return content;
    }

    private List<List<Object>> readSheet() {
        String sheetName = "Apply";
        String range = sheetName + "!A2:Z100";
        return GoogleSheetService.readSheet(spreadSheetId, range);
    }


    private String greeting(String recipient) {
        return "<p>Hi " + recipient + ",</p>";
    }
    private String readCoverLetterTemplate(String company, String role) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "cover-letter.txt")), StandardCharsets.UTF_8);
        content = content.replace("{ROLE}", role).replace("{COMPANY}", company);
        return removeBOM(content);
    }

    private String createClosing() {
        return "<p>"
                + "<strong>Thank you for considering my application</strong>."
                + " I would welcome the opportunity to discuss how my background, skills, and certifications will be beneficial to your team."
                + " I am available for an interview at your earliest convenience and look forward to hearing from you."
                + "</p>";
    }

    private String createEnclosure() {
        return "<p><strong>Enclosure: Resume</strong></p>";
    }
    private String createSignature() {
        return "<p style=\"font-family: 'Courier New', monospace;\">"
                + "Best Regards,"
                + "<br>"
                + "Atul Koshta"
                + "<br>"
                + "Phone: +91 9713443774"
                + "<br>"
                + "Email: atulk2018@gmail.com"
                + "<br> "
                + "LinkedIn: <a href='https://www.linkedin.com/in/atulkoshta/'>https://www.linkedin.com/in/atulkoshta/</a>"
                + "<br>"
                + "GitHub: <a href='https://github.com/atu1koshta'>https://github.com/atu1koshta</a>"
                + "</p>";
    }



    private String createHtmlEmailBody(String recipient, String company, String role) throws IOException {
        String greeting = greeting(recipient);
        String mainContent = readCoverLetterTemplate(company, role);
        String closing = createClosing();
        String enclosure = createEnclosure();
        String signature = createSignature();

        return "<html>"
                + "<body>"
                + greeting
                + mainContent
                + "<br>"
                + closing
                + "<br>"
                + enclosure
                + "<br>"
                + signature
                + "</body>"
                + "</html>";
    }

    private EmailContent draftEmailContent(List<Object> row) throws IOException {
        String resumePath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "resume.pdf").toString();

        String company = (String) row.get(0);
        String role = (String) row.get(1);
        String to = (String) row.get(2);
        String recipient = (String) row.get(3);
        String subject = "Application for " + role + " at " + company;
        String body = createHtmlEmailBody(recipient, company, role);

        ArrayList<String> attachments = new ArrayList<>();
        attachments.add(resumePath);

        return new EmailContent(to, subject, body, attachments);
    }

    @Override
    public void send() {
        List<List<Object>> data = readSheet();

        if (data == null) {
            return;
        }

        for (int i = 0; i < data.size(); i++) {
            List<Object> row = data.get(i);
            try {
                EmailContent emailContent = draftEmailContent(row);
                EmailService.sendEmail(emailContent);
                moveEntry(i + 2);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void moveEntry(int rowIndex) {
        String sourceSheetName = "Apply";
        String targetSheetName = "Applied";
        GoogleSheetService.moveEntry(spreadSheetId, sourceSheetName, targetSheetName, rowIndex);
    }
}
