package com.example.CodeWar.repositories;

import com.example.CodeWar.model.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tags,Long> {
}
