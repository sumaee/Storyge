package com.example.project.db.repository;

import com.example.project.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String name);

    Optional<User> findByNickname(String nickname);

    Optional<User> findByEmail(String email);

    List<User> findByNicknameContainingAndNicknameNotLike(String nickname, String myNickname);

}
