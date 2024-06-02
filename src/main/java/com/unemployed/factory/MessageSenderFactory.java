package com.unemployed.factory;

import com.unemployed.message.MessageSender;
import com.unemployed.message.email.JobEmailSender;
import com.unemployed.message.email.ReferralEmailSender;

public class MessageSenderFactory extends AbstractMessageSenderFactory{
    @Override
    public MessageSender createJobEmailSender() {
        return new JobEmailSender();
    }

    @Override
    public MessageSender createReferralEmailSender() {
        return new ReferralEmailSender();
    }
}
