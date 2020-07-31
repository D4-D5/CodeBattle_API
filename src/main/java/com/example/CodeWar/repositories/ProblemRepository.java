package com.example.CodeWar.repositories;

import com.example.CodeWar.model.Problem;
import com.example.CodeWar.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {

    @Query("SELECT p FROM Problem p JOIN p.authorId u WHERE u.codeBattleId = ?1")
    List<Problem> getListOfUsers(String authorId);
}
