package com.unemployed.model;

import java.util.ArrayList;

public class EmailContent {
    private final String to;

    private final String cc;

    private final String subject;
    private final String body;
    private final ArrayList<String> attachments;

    public EmailContent(String to, String cc, String subject, String body, ArrayList<String> attachments) {
        this.to = to;
        this.cc = cc;
        this.subject = subject;
        this.body = body;
        this.attachments = attachments;
    }

    public EmailContent(String to, String subject, String body, ArrayList<String> attachments) {
        this(to, null, subject, body, attachments);
    }

    public String getTo() {
        return to;
    }

    public String getCc() {
        return cc;
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
