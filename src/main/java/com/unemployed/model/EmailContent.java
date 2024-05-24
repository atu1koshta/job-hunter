package com.unemployed.model;

import java.util.ArrayList;

public class EmailContent {
    private final String to;
    private final String subject;
    private final String body;
    private final ArrayList<String> attachments;

    public EmailContent(String to, String subject, String body, ArrayList<String> attachments) {
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.attachments = attachments;
    }

    public EmailContent(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.attachments = new ArrayList<>(); // default value
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }


    public String getBody() {
        return body;
    }


    public ArrayList<String> getAttachments() {
        return attachments;
    }
}
