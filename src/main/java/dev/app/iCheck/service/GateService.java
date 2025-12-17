package dev.app.iCheck.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dev.app.iCheck.model.Gate;
import dev.app.iCheck.repository.GateRepository;

@Service
public class GateService {

    private final GateRepository gateRepository;

    public GateService(GateRepository gateRepository) {
        this.gateRepository = gateRepository;
    }

    public List<Gate> getAllGates() {
        return gateRepository.findAll();
    }
}
