package com.example.CodeWar.repositories;

import com.example.CodeWar.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ProblemRepository extends JpaRepository<Problem,Long> {
}
