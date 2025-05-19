package dev.app.iCheck.controller;

import dev.app.iCheck.model.SSRCode;
import dev.app.iCheck.repository.SSRCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ssr-codes")
@CrossOrigin(origins = "*")
public class SSRCodeController {

    @Autowired
    private SSRCodeRepository ssrCodeRepository;

    @GetMapping
    public ResponseEntity<List<SSRCode>> getAllSSRCodes() {
        List<SSRCode> codes = ssrCodeRepository.findByIsActiveTrue();
        return ResponseEntity.ok(codes);
    }

    @GetMapping("/{code}")
    public ResponseEntity<SSRCode> getSSRCodeByCode(@PathVariable String code) {
        return ssrCodeRepository.findByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<SSRCode>> getSSRCodesByCategory(@PathVariable String category) {
        List<SSRCode> codes = ssrCodeRepository.findByCategoryAndIsActiveTrue(category);
        return ResponseEntity.ok(codes);
    }

    @GetMapping("/search")
    public ResponseEntity<List<SSRCode>> searchSSRCodes(@RequestParam String query) {
        List<SSRCode> codes = ssrCodeRepository.findByCodeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query,
                query);
        return ResponseEntity.ok(codes);
    }
}