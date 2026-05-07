package com.federation.agriculture.dto;

public class CollectivityInformationDTO {
    private String number;
    private String name;

    public CollectivityInformationDTO() {}

    public CollectivityInformationDTO(String number, String name) {
        this.number = number;
        this.name = name;
    }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}