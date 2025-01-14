package dev.app.iCheck.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "destination")
public class Destination {

    @Id
    private String id;
    private String name;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    // Konstruktor domyślny (opcja)
    public Destination() {
    }

    // Konstruktor z parametrami (opcja)
    public Destination(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
