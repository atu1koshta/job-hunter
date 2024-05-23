package com.unemployed.mailer;

import com.unemployed.service.GoogleSheetService;
import com.unemployed.strategy.MessageSender;

public abstract class Mailer {
    private final MessageSender messageSender;

    public Mailer(MessageSender messageSender) {
        this.messageSender = messageSender;
    }
    public final void sendMail() {
        readSheet();
        messageSender.sendMessage();
        moveEntries();
    }

    public abstract void readSheet();
    public abstract void moveEntries();
}
