package dev.app.iCheck.controller;

import dev.app.iCheck.exception.ResourceNotFoundException;
import dev.app.iCheck.model.Destination;
import dev.app.iCheck.repository.DestinationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing destination entities.
 * Provides endpoints for retrieving destination data.
 */
@RestController
@RequestMapping("/api/destinations")
@CrossOrigin(origins = "*") // Pozwala na zapytania z frontendu
public class DestinationController {

    @Autowired
    private DestinationRepository destinationRepository;

    /**
     * Retrieves all destinations.
     *
     * @return A list of all Destination objects.
     */
    @GetMapping
    public List<Destination> getAllDestinations() {
        return destinationRepository.findAll();
    }

    /**
     * Retrieves a single destination by its ID.
     *
     * @param id The ID of the destination to retrieve.
     * @return The Destination object if found.
     * @throws ResourceNotFoundException if the destination with the given ID is not found.
     */
    @GetMapping("/{id}")
    public Destination getDestinationById(@PathVariable String id) {
        return destinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found with ID: " + id));
    }
}
