package com.example.birthday.notification;

import org.springframework.stereotype.Service;

@Service
public class ConsoleNotificationSender implements NotificationSender {

    @Override
    public void send(String email, String subject, String body) {
        System.out.println("===== EMAIL SENT =====");
        System.out.println("To: " + email);
        System.out.println("Subject: " + subject);
        System.out.println(body);
        System.out.println("======================");
    }
}
