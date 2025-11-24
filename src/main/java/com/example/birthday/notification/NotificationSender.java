package com.example.birthday.notification;

public interface NotificationSender {
    void send(String email, String subject, String body);
}
