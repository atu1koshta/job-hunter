package com.unemployed;

import com.unemployed.mailer.JobMailer;
import com.unemployed.mailer.Mailer;
import com.unemployed.service.GoogleSheetService;
import com.unemployed.strategy.EmailSender;
import com.unemployed.strategy.MessageSender;

public class Main {
    public static void main(String[] args) {
        GoogleSheetService googleSheetService = new GoogleSheetService();
        MessageSender messageSender = new EmailSender();
        Mailer mailer = new JobMailer(messageSender, googleSheetService);
        mailer.sendMail();
    }
}
