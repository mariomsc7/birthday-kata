package com.example.birthday.controller;

import com.example.birthday.model.Friend;
import com.example.birthday.service.BirthdayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/birthdays")
public class BirthdayController {

    private final BirthdayService birthdayService;

    public BirthdayController(BirthdayService birthdayService) {
        this.birthdayService = birthdayService;
    }

    @GetMapping("/today")
    public List<Friend> getTodaysBirthdays() {
        return birthdayService.findTodaysBirthdays();
    }

    @PostMapping("/send")
    public String sendBirthdayGreetings() {
        birthdayService.sendBirthdayGreetings();
        return "Birthday greetings sent!";
    }

    @PostMapping("/run")
    public ResponseEntity<String> runJobManually() {
        System.out.println("==================================================");
        System.out.println(" ENDPOINT: Avvio manuale job invio auguri");
        System.out.println("==================================================");

        birthdayService.sendBirthdayGreetings();

        System.out.println("Job manuale completato.");
        System.out.println("==================================================\n");

        return ResponseEntity.ok("Job di invio auguri eseguito correttamente.");
    }
}
