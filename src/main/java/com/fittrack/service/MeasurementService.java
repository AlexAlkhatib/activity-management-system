package com.fittrack.service;

import com.fittrack.domain.Measurement;
import com.fittrack.domain.User;
import com.fittrack.repository.MeasurementRepository;
import com.fittrack.web.dto.MeasurementForm;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

/**
 * Suivi du poids et des mensurations.
 */
@Service
public class MeasurementService {

    private final MeasurementRepository measurementRepository;
    private final UserService userService;

    public MeasurementService(MeasurementRepository measurementRepository, UserService userService) {
        this.measurementRepository = measurementRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<Measurement> listForCurrentUser() {
        return measurementRepository.findByUserOrderByDateDescIdDesc(userService.getCurrentUser());
    }

    @Transactional(readOnly = true)
    public Optional<Measurement> latest() {
        return measurementRepository.findFirstByUserOrderByDateDescIdDesc(userService.getCurrentUser());
    }

    @Transactional(readOnly = true)
    public double currentWeightOrDefault(double fallback) {
        return latest().map(Measurement::getWeightKg).orElse(fallback);
    }

    @Transactional
    public Measurement add(MeasurementForm form) {
        User user = userService.getCurrentUser();
        Measurement measurement = new Measurement(user, form.getDate(), form.getWeightKg());
        measurement.setBodyFatPct(form.getBodyFatPct());
        measurement.setWaistCm(form.getWaistCm());
        measurement.setChestCm(form.getChestCm());
        measurement.setNotes(form.getNotes());
        return measurementRepository.save(measurement);
    }

    @Transactional
    public void delete(Long id) {
        User user = userService.getCurrentUser();
        Measurement measurement = measurementRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesure introuvable."));
        measurementRepository.delete(measurement);
    }
}
