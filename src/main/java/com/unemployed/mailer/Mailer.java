package com.unemployed.mailer;

import com.unemployed.strategy.MessageSender;

import java.util.List;

public abstract class Mailer {
    private final MessageSender messageSender;

    public Mailer(MessageSender messageSender) {
        this.messageSender = messageSender;
    }
    public final void sendMail() {
        List<List<Object>> rows = readSheet();

        for(List<Object> row : rows) {
            System.out.println(row);
            messageSender.sendMessage();
        }
        moveEntries();
    }

    public abstract List<List<Object>> readSheet();
    public abstract void moveEntries();
}
