package com.example.birthday.model;

import java.time.LocalDate;

public record Friend(
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String email
) {}
