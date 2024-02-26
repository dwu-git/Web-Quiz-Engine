package engine.repository;

import engine.model.SolvedQuiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface SolvedQuizRepository extends CrudRepository<SolvedQuiz, Long> {
    long count();
    Page<SolvedQuiz> findAllByUserId(Long id, Pageable pageable);

//    for JPQL (Java Persistence Query Language) practice
//    @Query("SELECT cq FROM SolvedQuiz cq WHERE cq.user.id = :id")
//    Page<SolvedQuiz> findAllByUserId(@Param("id") Long id, Pageable pageable);
}
