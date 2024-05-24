package com.unemployed.mailer;

import com.unemployed.service.GoogleSheetService;
import com.unemployed.strategy.MessageSender;

import java.util.List;

public class JobMailer extends Mailer{
    private final GoogleSheetService googleSheetService;

    public JobMailer(MessageSender messageSender, GoogleSheetService googleSheetService) {
        super(messageSender);
        this.googleSheetService = googleSheetService;
    }

    @Override
    public List<List<Object>> readSheet() {
        String sheetName = "Apply";
        String spreadSheetId = "1XwEQae9Z6-nest8okXG6kKfygGkKYdb4XVt5eptAraA";
        String range = sheetName + "!A2:Z100";
        return googleSheetService.readSheet(spreadSheetId, range);
    }

    @Override
    public void moveEntries() {
        String sheetName = "Applied";
        String spreadSheetId = "1XwEQae9Z6-nest8okXG6kKfygGkKYdb4XVt5eptAraA";
        googleSheetService.moveEntries(spreadSheetId, sheetName, "A1:Z100");
    }
}
