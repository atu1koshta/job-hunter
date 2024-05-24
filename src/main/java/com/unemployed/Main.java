package com.unemployed;

import com.unemployed.message.JobEmailSender;
import com.unemployed.message.MessageSender;

public class Main {
    public static void main(String[] args) {
        MessageSender messageSender = new JobEmailSender();
        messageSender.send();
    }
}
