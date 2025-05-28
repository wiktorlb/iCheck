package dev.app.iCheck.controller;

import dev.app.iCheck.model.Plane;
import dev.app.iCheck.repository.PlaneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for initializing default plane configurations in the database.
 */
@RestController
public class PlaneInitializationController {

    @Autowired
    private PlaneRepository planeRepository;

    /**
     * Initializes a default plane (Boeing 737-800) and saves it to the database.
     *
     * @return A confirmation message indicating the plane that was initialized.
     */
    @PostMapping("/initialize-planes")
    public String initializePlanes() {
        // Create a Plane instance for Boeing 737-800
        Plane boeing737 = new Plane("B737-800", "Boeing 737-800");

        // Save the instance to the database
        planeRepository.save(boeing737);

        return "Plane initialized: " + boeing737.getModel();
    }
}