package com.unemployed.message.email;

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

public class ReferralEmailSender extends EmailTemplate implements MessageSender {
    private final String spreadSheetId = "1XwEQae9Z6-nest8okXG6kKfygGkKYdb4XVt5eptAraA";

    private List<List<Object>> readSheet() {
        String sheetName = "Referral";
        String range = sheetName + "!A2:Z100";
        return GoogleSheetService.readSheet(spreadSheetId, range);
    }

    private String createSubject(String company, String role, String jobId) {
        String subject = "Referral Request for " + role + " at " + company;

        if (jobId != null && !jobId.isEmpty()) {
            subject += " (Job ID: " + jobId + ")";
        }

        return subject;
    }

    public String readMainContentTemplate(String company, String role, String jobId) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "referral-request-template.txt")), StandardCharsets.UTF_8);
        content = content.replace("{ROLE}", role).replace("{COMPANY}", company);
        content = EmailHelper.addJobIdToBody(content, jobId);

        return StringHelper.removeBOM(content);
    }

    public String closing_with_role_company(String role, String company) {
        return "<p>"
                + "<strong>I have attached my resume for your review.</strong> If you find my qualifications suitable, "
                + "I would appreciate your consideration in referring me for the role."
                + "</p>";
    }

    private String createHtmlEmailBody(String recipient, String company, String role, String jobId) throws IOException {
        String greeting = greeting(recipient);
        String mainContent = readMainContentTemplate(company, role, jobId);
        String closing = closing_with_role_company(role, company);
        String enclosure = enclosure();
        String signature = signature();

        return "<html>"
                + "<body>"
                + greeting
                + mainContent
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
        String recipient = (String) row.get(3);
        String subject = createSubject(company, role, jobId);
        String body = createHtmlEmailBody(recipient, company, role, jobId);

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
        int sourceSheetId = 1796076197;
        String sourceSheetName = "Referral";
        String targetSheetName = "Referral Applied";
        int totalCols = 8;
        GoogleSheetService.moveEntry(spreadSheetId, sourceSheetId, sourceSheetName, targetSheetName, totalCols);
    }
}
