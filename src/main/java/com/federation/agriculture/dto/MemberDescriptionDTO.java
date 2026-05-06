package com.federation.agriculture.dto;

public class MemberDescriptionDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String occupation;

    public MemberDescriptionDTO() {}

    public MemberDescriptionDTO(String id, String firstName, String lastName, String email, String occupation) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.occupation = occupation;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }
}