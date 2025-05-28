package dev.app.iCheck.controller;

import dev.app.iCheck.model.SSRCode;
import dev.app.iCheck.repository.SSRCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for managing SSR codes.
 * Provides endpoints for retrieving SSR codes by various criteria.
 */
@RestController
@RequestMapping("/api/ssr-codes")
@CrossOrigin(origins = "*")
public class SSRCodeController {

    @Autowired
    private SSRCodeRepository ssrCodeRepository;

    /**
     * Retrieves all active SSR codes.
     *
     * @return ResponseEntity with a list of active SSR codes.
     */
    @GetMapping
    public ResponseEntity<List<SSRCode>> getAllSSRCodes() {
        List<SSRCode> codes = ssrCodeRepository.findByIsActiveTrue();
        return ResponseEntity.ok(codes);
    }

    /**
     * Retrieves an SSR code by its unique code.
     *
     * @param code The SSR code to retrieve.
     * @return ResponseEntity with the SSR code if found, or not found status.
     */
    @GetMapping("/{code}")
    public ResponseEntity<SSRCode> getSSRCodeByCode(@PathVariable String code) {
        return ssrCodeRepository.findByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves a list of active SSR codes by category.
     *
     * @param category The category of SSR codes to retrieve.
     * @return ResponseEntity with a list of active SSR codes for the given category.
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<SSRCode>> getSSRCodesByCategory(@PathVariable String category) {
        List<SSRCode> codes = ssrCodeRepository.findByCategoryAndIsActiveTrue(category);
        return ResponseEntity.ok(codes);
    }

    /**
     * Searches for SSR codes by matching query against code or description.
     *
     * @param query The search query.
     * @return ResponseEntity with a list of SSR codes matching the query.
     */
    @GetMapping("/search")
    public ResponseEntity<List<SSRCode>> searchSSRCodes(@RequestParam String query) {
        List<SSRCode> codes = ssrCodeRepository.findByCodeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query,
                query);
        return ResponseEntity.ok(codes);
    }
}