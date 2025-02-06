package dev.app.iCheck.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "baggageId")
public class BaggageId {
    @Id
    private String id; // Jedno unikalne ID, np. "lastBaggageId"
    private String lastUsedId; // Ostatnie użyte ID bagażu

    // Gettery i settery
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastUsedId() {
        return lastUsedId;
    }

    public void setLastUsedId(String lastUsedId) {
        this.lastUsedId = lastUsedId;
    }
}
