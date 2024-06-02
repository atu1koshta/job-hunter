package com.unemployed;

import com.unemployed.factory.AbstractMessageSenderFactory;
import com.unemployed.factory.MessageSenderFactory;
import com.unemployed.message.MessageSender;
public class Main {
    public static void main(String[] args) {
        AbstractMessageSenderFactory messageSenderFactory = new MessageSenderFactory();

        MessageSender jobEmailSender = messageSenderFactory.createJobEmailSender();
        jobEmailSender.send();

        MessageSender referralEmailSender = messageSenderFactory.createReferralEmailSender();
        referralEmailSender.send();
    }
}
