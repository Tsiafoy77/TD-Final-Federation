package com.federation.agriculture.dto;

import com.federation.agriculture.model.Member;
import java.util.ArrayList;
import java.util.List;

public class MemberDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private List<MemberDTO> referees;

    public MemberDTO() {}

    public MemberDTO(String id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.referees = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<MemberDTO> getReferees() {
        return referees;
    }

    public void setReferees(List<MemberDTO> referees) {
        this.referees = referees;
    }

    public static MemberDTO fromMember(Member member) {
        if (member == null) {
            return null;
        }
        MemberDTO dto = new MemberDTO();
        dto.setId(member.getId());
        dto.setFirstName(member.getFirstName());
        dto.setLastName(member.getLastName());
        dto.setEmail(member.getEmail());

        if (member.getReferees() != null) {
            List<MemberDTO> refereeDTOs = new ArrayList<>();
            for (Member referee : member.getReferees()) {
                refereeDTOs.add(fromMember(referee));
            }
            dto.setReferees(refereeDTOs);
        }

        return dto;
    }
}