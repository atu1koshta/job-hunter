package com.unemployed.factory;

import com.unemployed.message.MessageSender;
public abstract class AbstractMessageSenderFactory {
    public abstract MessageSender createJobEmailSender();

    public abstract MessageSender createReferralEmailSender();
}
