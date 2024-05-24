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
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.*;
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

    public void readSheet(String spreadsheetId, String range) {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

//             for service account credentials.json
//             pass new HttpCredentialsAdapter(credentials)
//            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(""))
//                    .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

            Sheets service =
                    new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                            .setApplicationName(APPLICATION_NAME)
                            .build();
            ValueRange response = service.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                System.out.println("No data found.");
            } else {
                for (List<Object> row : values) {
                    System.out.printf("%s, %s\n", row.get(0), row.get(2));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void moveEntries(String spreadsheetId, String sheetName, String range) {
        System.out.println("Moving entries in Google Sheet" + spreadsheetId + " " + sheetName + " " + range);
    }
}
