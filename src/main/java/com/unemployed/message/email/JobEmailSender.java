package com.unemployed.message.email;

import com.unemployed.constant.MessageSenderConstant;
import com.unemployed.helper.EmailHelper;
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

    private String createSubject(String type, String company, String role, String jobId) {
        String subject;
        if (type.equals(MessageSenderConstant.COMPANY)) {
            subject = "Application for " + role + " at " + company;
        } else {
            subject = "Application for " + role;
        }

        if (jobId != null && !jobId.isEmpty()) {
            subject += " (Job ID: " + jobId + ")";
        }

        return subject;
    }

    public String readMainContentTemplate(String type, String company, String role, String jobId) throws IOException {
        String cover_letter_file;
        if (type.equals(MessageSenderConstant.COMPANY)) {
            cover_letter_file = "cover-letter.txt";
        } else {
            cover_letter_file = "cover-letter-recruting-firm.txt";
        }

        String content = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir"), "src", "main", "resources", cover_letter_file)), StandardCharsets.UTF_8);
        content = content.replace("{ROLE}", role).replace("{COMPANY}", company);
        content = EmailHelper.addJobIdToBody(content, jobId);

        return StringHelper.removeBOM(content);
    }

    public String closing(String company) {
        return "<p>"
            + "I look forward to the opportunity to discuss how my background, skills and expertise align with the needs"
            + "of " + company + ". <strong>I have attached my resume for your review.</strong>"
            + "</p>"
            + "<p>"
            + "Thank you for considering my application. I hope to hear from you soon."
            + "</p>";
    }

    private String createHtmlEmailBody(String type, String recipient, String company, String role, String jobId) throws IOException {
        String greeting = greeting(recipient);
        String mainContent = readMainContentTemplate(type, company, role, jobId);
        String closing = closing(company);
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
        String resumePath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "AtulKoshta_SoftwareDevelopmentEngineer_3YExp.pdf").toString();

        String company = (String) row.get(0);
        String role = (String) row.get(1);
        String jobId = (String) row.get(2);
        String to = (String) row.get(4);
        String cc = (String) row.get(5);
        String type = (String) row.get(6);

        String recipient = "";
        try {
            recipient = (String) row.get(3);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        String subject = createSubject(type, company, role, jobId);
        String body = createHtmlEmailBody(type, recipient, company, role, jobId);

        ArrayList<String> attachments = new ArrayList<>();
        attachments.add(resumePath);

        return new EmailContent(to, cc, subject, body, attachments);
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
        int totalCols = 10;
        GoogleSheetService.moveEntry(spreadSheetId, sourceSheetId, sourceSheetName, targetSheetName, totalCols);
    }
}
