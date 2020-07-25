package com.example.CodeWar.repositories;

import com.example.CodeWar.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    User findFirstByUserName(String userName);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);
}
