package com.example.CodeWar.repositories;

import com.example.CodeWar.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    boolean existsByTagNameIgnoreCase(String tagName);

    Tag findByTagNameIgnoreCase(String tagName);
}
