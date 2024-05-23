package com.unemployed.service;

public class GoogleSheetService {
    public void readSheet(String spreadsheetId, String sheetName, String range) {
        System.out.println("Reading Google Sheet" + spreadsheetId + " " + sheetName + " " + range);
    }

    public void moveEntries(String spreadsheetId, String sheetName, String range) {
        System.out.println("Moving entries in Google Sheet" + spreadsheetId + " " + sheetName + " " + range);
    }
}
