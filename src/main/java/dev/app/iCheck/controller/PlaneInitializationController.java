package dev.app.iCheck.controller;

import dev.app.iCheck.model.Plane;
import dev.app.iCheck.repository.PlaneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlaneInitializationController {

    @Autowired
    private PlaneRepository planeRepository;

    @PostMapping("/initialize-planes")
    public String initializePlanes() {
        // Tworzenie instancji Plane dla Boeing 737-800
        Plane boeing737 = new Plane("B737-800", "Boeing 737-800");

        // Zapisz instancję w bazie danych
        planeRepository.save(boeing737);

        return "Plane initialized: " + boeing737.getModel();
    }
}