package com.example.CodeWar.repositories;

import com.example.CodeWar.app.DifficultyLevel;
import com.example.CodeWar.app.ProblemStatus;
import com.example.CodeWar.model.Problem;
import com.example.CodeWar.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {

    @Query("SELECT p FROM Problem p JOIN p.authorId u WHERE u.codeBattleId = ?1")
    List<Problem> getListOfProblemsByUserName(String authorId);

    List<Problem> findByDifficultyLevel(DifficultyLevel difficultyLevel);

    List<Problem> findByDifficultyLevelAndIdNotIn(DifficultyLevel difficultyLevel, List<Long> problemsIdNotToTake);

    List<Problem> findFirstByDifficultyLevelAndIdNotIn(DifficultyLevel difficultyLevel, List<Long> problemsIdNotToTake);

    List<Problem> findFirst2ByDifficultyLevelAndIdNotIn(DifficultyLevel difficultyLevel, List<Long> problemsIdNotToTake);

    List<Problem> findFirst3ByDifficultyLevelAndIdNotIn(DifficultyLevel difficultyLevel, List<Long> problemsIdNotToTake);


    List<Problem> findFirst3ByDifficultyLevelAndProblemStatusAndIdNotIn(DifficultyLevel easy, ProblemStatus problemStatus, List<Long> problemsIdNotToTake);
    List<Problem> findFirst2ByDifficultyLevelAndProblemStatusAndIdNotIn(DifficultyLevel easy, ProblemStatus problemStatus, List<Long> problemsIdNotToTake);
    List<Problem> findFirst1ByDifficultyLevelAndProblemStatusAndIdNotIn(DifficultyLevel easy, ProblemStatus problemStatus, List<Long> problemsIdNotToTake);

}
