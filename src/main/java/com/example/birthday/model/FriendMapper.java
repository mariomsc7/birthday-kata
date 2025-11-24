package com.example.birthday.model;

import com.example.birthday.entity.FriendEntity;

public class FriendMapper {

    public static Friend toModel(FriendEntity entity) {
        return new Friend(
                entity.getFirstName(),
                entity.getLastName(),
                entity.getDateOfBirth(),
                entity.getEmail()
        );
    }
}
