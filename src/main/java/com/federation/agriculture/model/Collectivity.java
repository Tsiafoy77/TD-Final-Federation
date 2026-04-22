package com.federation.agriculture.model;

import java.sql.Timestamp;
import java.util.List;

public class Collectivity {
    private String id;
    private String name;
    private String location;
    private String agriculturalSpecialty;
    private boolean federationApproval;
    private String presidentId;
    private String vicePresidentId;
    private String treasurerId;
    private String secretaryId;
    private Timestamp createdAt;
    private List<String> membersIds;
    private Member president;
    private Member vicePresident;
    private Member treasurer;
    private Member secretary;
    private List<Member> members;
    private String number;


    public Collectivity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getAgriculturalSpecialty() { return agriculturalSpecialty; }
    public void setAgriculturalSpecialty(String agriculturalSpecialty) { this.agriculturalSpecialty = agriculturalSpecialty; }

    public boolean isFederationApproval() { return federationApproval; }
    public void setFederationApproval(boolean federationApproval) { this.federationApproval = federationApproval; }

    public String getPresidentId() { return presidentId; }
    public void setPresidentId(String presidentId) { this.presidentId = presidentId; }

    public String getVicePresidentId() { return vicePresidentId; }
    public void setVicePresidentId(String vicePresidentId) { this.vicePresidentId = vicePresidentId; }

    public String getTreasurerId() { return treasurerId; }
    public void setTreasurerId(String treasurerId) { this.treasurerId = treasurerId; }

    public String getSecretaryId() { return secretaryId; }
    public void setSecretaryId(String secretaryId) { this.secretaryId = secretaryId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public List<String> getMembersIds() { return membersIds; }
    public void setMembersIds(List<String> membersIds) { this.membersIds = membersIds; }

    public Member getPresident() { return president; }
    public void setPresident(Member president) { this.president = president; }

    public Member getVicePresident() { return vicePresident; }
    public void setVicePresident(Member vicePresident) { this.vicePresident = vicePresident; }

    public Member getTreasurer() { return treasurer; }
    public void setTreasurer(Member treasurer) { this.treasurer = treasurer; }

    public Member getSecretary() { return secretary; }
    public void setSecretary(Member secretary) { this.secretary = secretary; }

    public List<Member> getMembers() { return members; }
    public void setMembers(List<Member> members) { this.members = members; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
}