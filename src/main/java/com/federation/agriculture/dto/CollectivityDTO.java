package com.federation.agriculture.dto;

import com.federation.agriculture.model.Collectivity;
import com.federation.agriculture.model.Member;
import java.util.ArrayList;
import java.util.List;

public class CollectivityDTO {
    private String id;
    private String location;
    private CollectivityStructureDTO structure;
    private List<MemberDTO> members;

    public CollectivityDTO() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public CollectivityStructureDTO getStructure() {
        return structure;
    }

    public void setStructure(CollectivityStructureDTO structure) {
        this.structure = structure;
    }

    public List<MemberDTO> getMembers() {
        return members;
    }

    public void setMembers(List<MemberDTO> members) {
        this.members = members;
    }

    public static CollectivityDTO fromCollectivity(Collectivity collectivity) {
        if (collectivity == null) {
            return null;
        }

        CollectivityDTO dto = new CollectivityDTO();
        dto.setId(collectivity.getId());
        dto.setLocation(collectivity.getLocation());

        CollectivityStructureDTO structureDTO = new CollectivityStructureDTO();
        structureDTO.setPresident(MemberDTO.fromMember(collectivity.getPresident()));
        structureDTO.setVicePresident(MemberDTO.fromMember(collectivity.getVicePresident()));
        structureDTO.setTreasurer(MemberDTO.fromMember(collectivity.getTreasurer()));
        structureDTO.setSecretary(MemberDTO.fromMember(collectivity.getSecretary()));
        dto.setStructure(structureDTO);

        if (collectivity.getMembers() != null) {
            List<MemberDTO> memberDTOs = new ArrayList<>();
            for (Member member : collectivity.getMembers()) {
                memberDTOs.add(MemberDTO.fromMember(member));
            }
            dto.setMembers(memberDTOs);
        }

        return dto;
    }
}