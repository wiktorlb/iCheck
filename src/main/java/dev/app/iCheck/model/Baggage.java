package dev.app.iCheck.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "baggage")
public class Baggage {

    @Id
    private String id;
    private double weight;
    private BaggageType type;

    public Baggage(String id, double weight, BaggageType type) {
        this.id = id;
        this.weight = weight;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public BaggageType getType() {
        return type;
    }

    public void setType(BaggageType type) {
        this.type = type;
    }

    // Używamy enuma zamiast Stringa
    public enum BaggageType {
        BAG,
        HAND_LUGGAGE,
        DAA,
        SPORT_EQUIPMENT,
        WHEELCHAIR
    }
}
