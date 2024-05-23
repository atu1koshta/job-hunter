package com.unemployed.mailer;

import com.unemployed.service.GoogleSheetService;
import com.unemployed.strategy.MessageSender;

public class JobMailer extends Mailer{
    private final GoogleSheetService googleSheetService;

    public JobMailer(MessageSender messageSender, GoogleSheetService googleSheetService) {
        super(messageSender);
        this.googleSheetService = googleSheetService;
    }

    @Override
    public void readSheet() {
        String sheetName = "Apply";
        String spreadSheetId = "1XwEQae9Z6-nest8okXG6kKfygGkKYdb4XVt5eptAraA";
        googleSheetService.readSheet(spreadSheetId, sheetName, "A1:Z100");
    }

    @Override
    public void moveEntries() {
        String sheetName = "Applied";
        String spreadSheetId = "1XwEQae9Z6-nest8okXG6kKfygGkKYdb4XVt5eptAraA";
        googleSheetService.moveEntries(spreadSheetId, sheetName, "A1:Z100");
    }
}
