package com.fittrack.repository;

import com.fittrack.domain.User;
import com.fittrack.domain.Workout;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    @EntityGraph(attributePaths = {"exercises", "exercises.exercise"})
    List<Workout> findByUserOrderByDateDescIdDesc(User user);

    @EntityGraph(attributePaths = {"exercises", "exercises.exercise"})
    List<Workout> findByUserAndDateGreaterThanEqualOrderByDateDescIdDesc(User user, LocalDate from);

    @EntityGraph(attributePaths = {"exercises", "exercises.exercise"})
    Optional<Workout> findByIdAndUser(Long id, User user);

    @EntityGraph(attributePaths = {"exercises", "exercises.exercise"})
    List<Workout> findByUserAndDateBetweenOrderByDateAsc(User user, LocalDate from, LocalDate to);

    List<Workout> findTop5ByUserOrderByDateDescIdDesc(User user);

    long countByUser(User user);

    long countByUserAndDateGreaterThanEqual(User user, LocalDate from);
}
