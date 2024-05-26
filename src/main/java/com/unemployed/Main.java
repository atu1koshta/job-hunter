package com.unemployed;

import com.unemployed.message.email.JobEmailSender;
import com.unemployed.message.MessageSender;
import com.unemployed.message.email.ReferralEmailSender;

public class Main {
    public static void main(String[] args) {
        MessageSender jobEmailSender = new JobEmailSender();
        jobEmailSender.send();

        MessageSender referralEmailSender = new ReferralEmailSender();
        referralEmailSender.send();
    }
}
