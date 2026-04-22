package com.federation.agriculture.dto;

public class CollectivityStructureDTO {
    private MemberDTO president;
    private MemberDTO vicePresident;
    private MemberDTO treasurer;
    private MemberDTO secretary;

    public CollectivityStructureDTO() {}

    public MemberDTO getPresident() {
        return president;
    }

    public void setPresident(MemberDTO president) {
        this.president = president;
    }

    public MemberDTO getVicePresident() {
        return vicePresident;
    }

    public void setVicePresident(MemberDTO vicePresident) {
        this.vicePresident = vicePresident;
    }

    public MemberDTO getTreasurer() {
        return treasurer;
    }

    public void setTreasurer(MemberDTO treasurer) {
        this.treasurer = treasurer;
    }

    public MemberDTO getSecretary() {
        return secretary;
    }

    public void setSecretary(MemberDTO secretary) {
        this.secretary = secretary;
    }
}