package com.fittrack.repository;

import com.fittrack.domain.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    List<Exercise> findByCreatedByIsNullOrCreatedByOrderByNameAsc(Long createdBy);

    List<Exercise> findByCreatedByIsNullOrderByNameAsc();
}
