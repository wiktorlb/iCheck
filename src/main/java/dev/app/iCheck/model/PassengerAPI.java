package dev.app.iCheck.model;

public class PassengerAPI extends Passenger {
    private String dateOfBirth;
    private String citizenship;
    private String documentType;
    private String serialName;
    private String validUntil;
    private String issueCountry;

    // Konstruktor
    public PassengerAPI(String id, String flightId, String name, String surname, String gender, String status,
            String dateOfBirth, String citizenship, String documentType, String serialName, String validUntil,
            String issueCountry) {
        super(id, flightId, name, surname, gender, status);
        this.dateOfBirth = dateOfBirth;
        this.citizenship = citizenship;
        this.documentType = documentType;
        this.serialName = serialName;
        this.validUntil = validUntil;
        this.issueCountry = issueCountry;
    }

    // Gettery i Settery
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getSerialName() {
        return serialName;
    }

    public void setSerialName(String serialName) {
        this.serialName = serialName;
    }

    public String getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(String validUntil) {
        this.validUntil = validUntil;
    }

    public String getIssueCountry() {
        return issueCountry;
    }

    public void setIssueCountry(String issueCountry) {
        this.issueCountry = issueCountry;
    }
}