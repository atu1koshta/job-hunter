package com.unemployed.service;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GoogleSheetService {
    private static final String APPLICATION_NAME = "Job Hunter";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES =
            Collections.singletonList(SheetsScopes.SPREADSHEETS);

    private static final String CREDENTIALS_FILE_PATH = "/client_secret_735628014392-pgr1nq7k682oe6cq2gbdktdpu36pcf3o.apps.googleusercontent.com.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = GoogleSheetService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static List<List<Object>> readSheet(String spreadsheetId, String range) {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

            Sheets service =
                    new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                            .setApplicationName(APPLICATION_NAME)
                            .build();
            ValueRange response = service.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();

            return response.getValues();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void moveEntry(String spreadsheetId, String sourceSheetName, String targetSheetName, int rowIndex) {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            // Read the row data from the source sheet
            String sourceRange = sourceSheetName + "!" + rowIndex + ":" + rowIndex;
            ValueRange response = service.spreadsheets().values()
                    .get(spreadsheetId, sourceRange)
                    .execute();
            List<List<Object>> rowData = response.getValues();

            if (rowData != null && !rowData.isEmpty()) {
                // Append the current timestamp in IST to the row data
                ZoneId istZoneId = ZoneId.of("Asia/Kolkata");
                ZonedDateTime istTime = ZonedDateTime.now(istZoneId);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String timestamp = istTime.format(formatter);
                rowData.get(0).add(timestamp);

                // Append the row data to the target sheet
                String targetRange = targetSheetName + "!A1";
                ValueRange appendBody = new ValueRange()
                        .setValues(rowData);
                service.spreadsheets().values()
                        .append(spreadsheetId, targetRange, appendBody)
                        .setValueInputOption("USER_ENTERED")
                        .execute();

                // Remove the row from the source sheet
                List<Request> requests = new ArrayList<>();
                requests.add(new Request()
                        .setDeleteDimension(new DeleteDimensionRequest()
                                .setRange(new DimensionRange()
                                        .setSheetId(0) // Assuming the source sheet ID is 0
                                        .setDimension("ROWS")
                                        .setStartIndex(rowIndex - 1) // -1 because rows are 0-indexed in the API
                                        .setEndIndex(rowIndex))));
                BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
                        .setRequests(requests);
                service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest)
                        .execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
