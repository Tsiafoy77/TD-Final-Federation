package com.federation.agriculture.dto;

import java.util.List;

public class CreateCollectivityDTO {
    private String location;
    private String specialty;
    private List<String> members;
    private boolean federationApproval;
    private CreateCollectivityStructureDTO structure;

    public CreateCollectivityDTO() {}

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public boolean isFederationApproval() {
        return federationApproval;
    }

    public void setFederationApproval(boolean federationApproval) {
        this.federationApproval = federationApproval;
    }

    public CreateCollectivityStructureDTO getStructure() {
        return structure;
    }

    public void setStructure(CreateCollectivityStructureDTO structure) {
        this.structure = structure;
    }
}