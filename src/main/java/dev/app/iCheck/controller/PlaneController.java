package dev.app.iCheck.controller;

import dev.app.iCheck.model.Plane;
import dev.app.iCheck.repository.PlaneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing plane entities.
 * Provides endpoints for creating, retrieving, and adding planes.
 */
@RestController
@RequestMapping("api/planes")
@CrossOrigin(origins = "*") // Pozwala na zapytania z frontendu
public class PlaneController {

    @Autowired
    private PlaneRepository planeRepository;

    public PlaneController(PlaneRepository planeRepository) {
        this.planeRepository = planeRepository;
    }

    /**
     * Creates a new plane.
     *
     * @param plane The Plane object to create.
     * @return The created Plane object.
     */
    @PostMapping
    public Plane createPlane(@RequestBody Plane plane) {
        return planeRepository.save(plane);
    }

    /**
     * Retrieves all planes.
     *
     * @return A list of all Plane objects.
     */
    @GetMapping
    public List<Plane> getAllPlanes() {
        return planeRepository.findAll();
    }

    /**
     * Adds a new plane with details.
     *
     * @param plane The Plane object to add.
     * @return ResponseEntity indicating the success or failure of the operation.
     */
    @PostMapping(value = "/add", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> addPlane(@RequestBody Plane plane) {
        Plane newPlane = new Plane(plane.getId(), plane.getModel());
        planeRepository.save(newPlane);
        return ResponseEntity.ok("Plane with seats saved successfully!");
    }
}
