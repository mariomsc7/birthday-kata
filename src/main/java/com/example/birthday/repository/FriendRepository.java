package com.example.birthday.repository;

import com.example.birthday.model.Friend;
import com.example.birthday.model.FriendMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FriendRepository {

    private final FriendJpaRepository jpa;

    public FriendRepository(FriendJpaRepository jpa) {
        this.jpa = jpa;
    }

    public List<Friend> findAll() {
        return jpa.findAll().stream()
                .map(FriendMapper::toModel)
                .toList();
    }
}

