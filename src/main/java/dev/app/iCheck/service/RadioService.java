package dev.app.iCheck.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dev.app.iCheck.model.Radio;
import dev.app.iCheck.repository.RadioRepository;

@Service
public class RadioService {

    private final RadioRepository radioRepository;

    public RadioService(RadioRepository radioRepository) {
        this.radioRepository = radioRepository;
    }

    public List<Radio> getAllRadios() {
        return radioRepository.findAll();
    }
}
