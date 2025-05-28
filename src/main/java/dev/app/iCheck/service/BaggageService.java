package dev.app.iCheck.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import dev.app.iCheck.model.BaggageId;
import dev.app.iCheck.repository.BaggageIdRepository;

/**
 * Service class for managing baggage-related operations.
 * Handles baggage ID generation and management.
 */
@Service
public class BaggageService {

    @Autowired
    private BaggageIdRepository baggageIdRepository;

    /**
     * Generates a unique baggage ID.
     * Retrieves the last used ID from the database and increments it.
     *
     * @return A unique baggage ID string
     */
    public String generateBaggageId() {
        String lastBaggageId = getLastBaggageId();
        String newBaggageId = incrementBaggageId(lastBaggageId);

        updateLastBaggageId(newBaggageId);

        return newBaggageId;
    }

    /**
     * Retrieves the last used baggage ID from the database.
     *
     * @return The last used baggage ID
     */
    private String getLastBaggageId() {
        BaggageId baggageId = baggageIdRepository.findById("lastBaggageId").orElse(new BaggageId());
        return baggageId.getLastUsedId() != null ? baggageId.getLastUsedId() : "AA000000";
    }

    /**
     * Updates the last used baggage ID in the database.
     *
     * @param newBaggageId The new baggage ID to store
     */
    private void updateLastBaggageId(String newBaggageId) {
        BaggageId baggageId = baggageIdRepository.findById("lastBaggageId").orElse(new BaggageId());
        baggageId.setId("lastBaggageId");
        baggageId.setLastUsedId(newBaggageId);
        baggageIdRepository.save(baggageId);
    }

    /**
     * Increments the baggage ID according to the specified format.
     *
     * @param currentId The current baggage ID
     * @return The incremented baggage ID
     * @throws IllegalArgumentException if the current ID format is invalid
     */
    private String incrementBaggageId(String currentId) {
        Pattern pattern = Pattern.compile("([A-Z]{2})(\\d{6})");
        Matcher matcher = pattern.matcher(currentId);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid baggage ID format: " + currentId);
        }

        String prefix = matcher.group(1);
        int number = Integer.parseInt(matcher.group(2));

        number++;

        if (number > 999999) {
            prefix = nextPrefix(prefix);
            number = 1;
        }

        return String.format("%s%06d", prefix, number);
    }

    /**
     * Generates the next prefix in sequence.
     *
     * @param prefix The current prefix
     * @return The next prefix in sequence
     */
    private String nextPrefix(String prefix) {
        char first = prefix.charAt(0);
        char second = prefix.charAt(1);

        if (second == 'Z') {
            first++;
            second = 'A';
        } else {
            second++;
        }

        return "" + first + second;
    }
}
