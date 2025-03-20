package dev.app.iCheck.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import dev.app.iCheck.model.Plane;
import dev.app.iCheck.repository.PlaneRepository;

@Service
public class PlaneService {
    private static final Logger logger = LoggerFactory.getLogger(PlaneService.class);
    private final PlaneRepository planeRepository;

    public PlaneService(PlaneRepository planeRepository) {
        this.planeRepository = planeRepository;
    }

    @PostConstruct
    public void initDefaultPlanes() {
        logger.info("Sprawdzanie domyślnych samolotów...");
        if (!planeRepository.existsById("B737-800")) {
            Plane boeing737 = new Plane("B737-800", "Boeing 737-800");
            planeRepository.save(boeing737);
            logger.info("Dodano domyślny samolot: " + boeing737);
        } else {
            logger.info("Samolot B737-800 już istnieje w bazie.");
        }
    }
}

