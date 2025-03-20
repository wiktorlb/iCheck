package dev.app.iCheck.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "aircraft")
public class Aircraft {
    @Id
    private String id; // np. "SP-LWC"
    private String planeId; // Referencja do modelu, np. "B737-800"
    private String airline; // np. "LOT Polish Airlines"

    public Aircraft(String id, String planeId, String airline) {
        this.id = id;
        this.planeId = planeId;
        this.airline = airline;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaneId() {
        return planeId;
    }

    public void setPlaneId(String planeId) {
        this.planeId = planeId;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }


}
