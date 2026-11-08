package com.fittrack.repository;

import com.fittrack.domain.Measurement;
import com.fittrack.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

    List<Measurement> findByUserOrderByDateDescIdDesc(User user);

    List<Measurement> findTop10ByUserOrderByDateDescIdDesc(User user);

    Optional<Measurement> findFirstByUserOrderByDateDescIdDesc(User user);

    Optional<Measurement> findByIdAndUser(Long id, User user);
}
