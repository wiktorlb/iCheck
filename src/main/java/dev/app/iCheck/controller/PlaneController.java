package dev.app.iCheck.controller;

import dev.app.iCheck.model.Plane;
import dev.app.iCheck.repository.PlaneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/planes")
@CrossOrigin(origins = "*") // Pozwala na zapytania z frontendu
public class PlaneController {

    @Autowired
    private PlaneRepository planeRepository;

    public PlaneController(PlaneRepository planeRepository) {
        this.planeRepository = planeRepository;
    }

    @PostMapping
    public Plane createPlane(@RequestBody Plane plane) {
        return planeRepository.save(plane);
    }

    @GetMapping
    public List<Plane> getAllPlanes() {
        return planeRepository.findAll();
    }

    @PostMapping(value = "/add", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> addPlane(@RequestBody Plane plane) {
        Plane newPlane = new Plane(plane.getId(), plane.getModel());
        planeRepository.save(newPlane);
        return ResponseEntity.ok("Plane with seats saved successfully!");
    }
}
