package dev.app.iCheck.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "radio")
public class Radio {

    @Id
    private String id;
    private String radioNumber;

    public Radio() {
    }

    public Radio(String id, String radioNumber) {
        this.id = id;
        this.radioNumber = radioNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRadioNumber() {
        return radioNumber;
    }

    public void setRadioNumber(String radioNumber) {
        this.radioNumber = radioNumber;
    }

    @Override
    public String toString() {
        return "Radio{" +
                "id='" + id + '\'' +
                ", radioNumber='" + radioNumber + '\'' +
                '}';
    }
}
