package dev.app.iCheck.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.app.iCheck.model.Gate;
import dev.app.iCheck.model.Radio;
import dev.app.iCheck.service.GateService;
import dev.app.iCheck.service.RadioService;

@RestController
@RequestMapping("/api/support-data")
public class SupportDataController {

    private final GateService gateService;
    private final RadioService radioService;

    public SupportDataController(GateService gateService, RadioService radioService) {
        this.gateService = gateService;
        this.radioService = radioService;
    }

    @GetMapping("/gates")
    public ResponseEntity<List<Gate>> getGates() {
        return ResponseEntity.ok(gateService.getAllGates());
    }

    @GetMapping("/radios")
    public ResponseEntity<List<Radio>> getRadios() {
        return ResponseEntity.ok(radioService.getAllRadios());
    }
}
