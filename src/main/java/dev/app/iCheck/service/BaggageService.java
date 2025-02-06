package dev.app.iCheck.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import dev.app.iCheck.model.BaggageId;
import dev.app.iCheck.repository.BaggageIdRepository;

@Service
public class BaggageService {

    @Autowired
    private BaggageIdRepository baggageIdRepository;

    // Generowanie nowego ID bagażu
    public String generateBaggageId() {
        String lastBaggageId = getLastBaggageId(); // Pobierz ostatnie ID z bazy
        String newBaggageId = incrementBaggageId(lastBaggageId); // Inkrementuj ID

        // Zaktualizuj ostatnie ID w bazie
        updateLastBaggageId(newBaggageId);

        return newBaggageId;
    }

    // Pobierz ostatnie ID z bazy
    private String getLastBaggageId() {
        BaggageId baggageId = baggageIdRepository.findById("lastBaggageId").orElse(new BaggageId());
        return baggageId.getLastUsedId() != null ? baggageId.getLastUsedId() : "AA000000"; // Domyślne ID
    }

    // Zaktualizuj ostatnie ID w bazie
    private void updateLastBaggageId(String newBaggageId) {
        BaggageId baggageId = baggageIdRepository.findById("lastBaggageId").orElse(new BaggageId());
        baggageId.setId("lastBaggageId");
        baggageId.setLastUsedId(newBaggageId);
        baggageIdRepository.save(baggageId);
    }

    // Funkcja do inkrementacji ID
    private String incrementBaggageId(String currentId) {
        // Twoja logika inkrementowania ID bagażu
        Pattern pattern = Pattern.compile("([A-Z]{2})(\\d{6})");
        Matcher matcher = pattern.matcher(currentId);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid baggage ID format: " + currentId);
        }

        String prefix = matcher.group(1);
        int number = Integer.parseInt(matcher.group(2));

        number++; // Zwiększ liczbową część

        if (number > 999999) { // Jeśli przekroczono limit, zmień prefiks
            prefix = nextPrefix(prefix);
            number = 1; // Reset numeracji
        }

        return String.format("%s%06d", prefix, number);
    }

    // Funkcja do zmiany prefiksu
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
