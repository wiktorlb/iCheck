package dev.app.iCheck.controller;

import dev.app.iCheck.exception.ResourceNotFoundException;
import dev.app.iCheck.model.Destination;
import dev.app.iCheck.repository.DestinationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/destinations")
@CrossOrigin(origins = "*") // Pozwala na zapytania z frontendu
public class DestinationController {

    @Autowired
    private DestinationRepository destinationRepository;

    // Endpoint do pobierania wszystkich destynacji
    @GetMapping
    public List<Destination> getAllDestinations() {
        return destinationRepository.findAll();
    }

    // Endpoint do pobierania jednej destynacji po ID
    @GetMapping("/{id}")
    public Destination getDestinationById(@PathVariable String id) {
        return destinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found with ID: " + id));
    }
}
