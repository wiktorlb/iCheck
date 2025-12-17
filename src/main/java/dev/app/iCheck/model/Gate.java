package dev.app.iCheck.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "gate")
public class Gate {

    @Id
    private String id;
    private String gateNumber;

    public Gate() {
    }

    public Gate(String id, String gateNumber) {
        this.id = id;
        this.gateNumber = gateNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGateNumber() {
        return gateNumber;
    }

    public void setGateNumber(String gateNumber) {
        this.gateNumber = gateNumber;
    }

    @Override
    public String toString() {
        return "Gate{" +
                "id='" + id + '\'' +
                ", gateNumber='" + gateNumber + '\'' +
                '}';
    }
}
