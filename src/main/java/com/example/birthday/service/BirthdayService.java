package com.example.birthday.service;

import com.example.birthday.model.Friend;
import com.example.birthday.notification.NotificationSender;
import com.example.birthday.repository.FriendRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BirthdayService {

    private final FriendRepository friendRepository;
    private final NotificationSender notificationSender;

    public BirthdayService(FriendRepository friendRepository,
                           NotificationSender notificationSender) {
        this.friendRepository = friendRepository;
        this.notificationSender = notificationSender;
    }

    public List<Friend> findBirthdaysOn(LocalDate date) {
        return friendRepository.findAll().stream()
                .filter(f -> isBirthdayWithFeb29Rule(f.dateOfBirth(), date))
                .toList();
    }

    public List<Friend> findTodaysBirthdays() {
        return findBirthdaysOn(LocalDate.now());
    }

    public void sendBirthdayGreetings() {
        List<Friend> all = friendRepository.findAll();
        List<Friend> birthdayFriends = findTodaysBirthdays();

        birthdayFriends.forEach(this::sendBirthdayEmail);

        if (!birthdayFriends.isEmpty()) {
            all.forEach(friend -> sendReminderEmail(friend, birthdayFriends));
        }
    }

    public boolean isBirthdayWithFeb29Rule(LocalDate dob, LocalDate today) {
        if (dob.getMonthValue() == 2 && dob.getDayOfMonth() == 29) {
            return today.getMonthValue() == 2 && today.getDayOfMonth() == 28;
        }
        return dob.getMonthValue() == today.getMonthValue()
                && dob.getDayOfMonth() == today.getDayOfMonth();
    }

    private void sendBirthdayEmail(Friend friend) {
        notificationSender.send(
                friend.email(),
                "Happy birthday!",
                "Happy birthday, dear " + friend.firstName() + "!"
        );
    }

    private void sendReminderEmail(Friend recipient, List<Friend> birthdayFriends) {
        List<Friend> others = birthdayFriends.stream()
                .filter(f -> !f.email().equals(recipient.email()))
                .toList();

        if (others.isEmpty()) return;

        String names = others.stream()
                .map(f -> f.firstName() + " " + f.lastName())
                .collect(Collectors.joining(", "));

        notificationSender.send(
                recipient.email(),
                "Birthday Reminder",
                "Dear " + recipient.firstName() + ",\n\n" +
                        "Today is " + names + "'s birthday.\n" +
                        "Don't forget to send them a message!"
        );
    }
}
