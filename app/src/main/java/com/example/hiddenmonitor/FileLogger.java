package com.example.hiddenmonitor;

import android.content.Context;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileLogger {
    
    private static final String LOG_FILE = "monitor_log.txt";
    
    public static void log(Context context, String message) {
        try {
            File logFile = new File(context.getExternalFilesDir(null), LOG_FILE);
            FileWriter fw = new FileWriter(logFile, true);
            
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", 
                               Locale.getDefault()).format(new Date());
            
            fw.append(timestamp).append(" - ").append(message).append("\n");
            fw.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
