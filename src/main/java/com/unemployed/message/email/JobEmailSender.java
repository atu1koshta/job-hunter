package com.unemployed.message.email;

import com.unemployed.constant.MessageSenderConstant;
import com.unemployed.helper.StringHelper;
import com.unemployed.message.MessageSender;
import com.unemployed.model.EmailContent;
import com.unemployed.service.EmailService;
import com.unemployed.service.GoogleSheetService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JobEmailSender extends EmailTemplate implements MessageSender {
    private final String spreadSheetId = "1XwEQae9Z6-nest8okXG6kKfygGkKYdb4XVt5eptAraA";

    private List<List<Object>> readSheet() {
        String sheetName = "Apply";
        String range = sheetName + "!A2:Z100";
        return GoogleSheetService.readSheet(spreadSheetId, range);
    }

    private String createSubject(String type, String company, String role) {
        if (type.equals(MessageSenderConstant.COMPANY)) {
            return "Application for " + role + " at " + company;
        } else {
            return "Application for " + role;
        }
    }

    public String readMainContentTemplate(String type, String company, String role) throws IOException {
        String content;
        if (type.equals(MessageSenderConstant.COMPANY)) {
            content = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "cover-letter.txt")), StandardCharsets.UTF_8);
        } else {
            content = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "cover-letter-recruting-firm.txt")), StandardCharsets.UTF_8);
        }
        content = content.replace("{ROLE}", role).replace("{COMPANY}", company);
        return StringHelper.removeBOM(content);
    }

    public String closing() {
        return "<p>"
                + "<strong>Thank you for considering my application</strong>."
                + " I would welcome the opportunity to discuss how my background, skills, and certifications will be beneficial to your team."
                + " I am available for an interview at your earliest convenience and look forward to hearing from you."
                + "</p>";
    }

    private String createHtmlEmailBody(String type, String recipient, String company, String role) throws IOException {
        String greeting = greeting(recipient);
        String mainContent = readMainContentTemplate(type, company, role);
        String closing = closing();
        String enclosure = enclosure();
        String signature = signature();

        return "<html>"
                + "<body>"
                + greeting
                + mainContent
                + "<br>"
                + closing
                + "<br>"
                + signature
                + "<br>"
                + enclosure
                + "</body>"
                + "</html>";
    }

    private EmailContent draftEmailContent(List<Object> row) throws IOException {
        String resumePath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "resume.pdf").toString();

        String company = (String) row.get(0);
        String role = (String) row.get(1);
        String to = (String) row.get(3);
        String type = (String) row.get(4);

        String recipient = "";
        try {
            recipient = (String) row.get(2);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        String subject = createSubject(type, company, role);
        String body = createHtmlEmailBody(type, recipient, company, role);

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
                moveEntry();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void moveEntry() {
        int sourceSheetId = 0;
        String sourceSheetName = "Apply";
        String targetSheetName = "Applied";
        int totalCols = 6;
        GoogleSheetService.moveEntry(spreadSheetId, sourceSheetId, sourceSheetName, targetSheetName, totalCols);
    }
}
