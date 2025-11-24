package com.example.birthday.repository;

import com.example.birthday.entity.FriendEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendJpaRepository extends JpaRepository<FriendEntity, Long> {
}
