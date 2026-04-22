package com.federation.agriculture.service;

import com.federation.agriculture.dto.CreateCollectivityDTO;
import com.federation.agriculture.dto.CreateCollectivityStructureDTO;
import com.federation.agriculture.dto.CollectivityDTO;
import com.federation.agriculture.model.Collectivity;
import com.federation.agriculture.model.Member;
import com.federation.agriculture.repository.CollectivityRepository;
import com.federation.agriculture.repository.MemberRepository;
import java.time.LocalDate;
import java.util.*;

public class CollectivityService {

    private final CollectivityRepository collectivityRepository;
    private final MemberRepository memberRepository;

    public CollectivityService(CollectivityRepository collectivityRepository,
                               MemberRepository memberRepository) {
        this.collectivityRepository = collectivityRepository;
        this.memberRepository = memberRepository;
    }

    public List<CollectivityDTO> createCollectivities(List<CreateCollectivityDTO> createDTOs) {
        List<CollectivityDTO> result = new ArrayList<>();

        for (CreateCollectivityDTO dto : createDTOs) {
            if (!dto.isFederationApproval()) {
                throw new RuntimeException("Collectivity without federation approval");
            }

            List<String> memberIds = dto.getMembers();
            if (memberIds == null || memberIds.size() < 10) {
                throw new RuntimeException("Collectivity must have at least 10 members");
            }

            List<Member> members = memberRepository.findAllByIds(memberIds);
            if (members.size() != memberIds.size()) {
                throw new RuntimeException("One or more members not found");
            }

            LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
            long seniorCount = members.stream()
                    .filter(m -> m.getMembershipDate() != null)
                    .filter(m -> m.getMembershipDate().isBefore(sixMonthsAgo))
                    .count();

            if (seniorCount < 5) {
                throw new RuntimeException("Au moins 5 membres doivent avoir une ancienneté de 6 mois dans la fédération");
            }

            CreateCollectivityStructureDTO structure = dto.getStructure();
            if (structure == null ||
                    structure.getPresident() == null ||
                    structure.getVicePresident() == null ||
                    structure.getTreasurer() == null ||
                    structure.getSecretary() == null) {
                throw new RuntimeException("All specific positions must be filled");
            }

            Collectivity collectivity = new Collectivity();
            collectivity.setLocation(dto.getLocation());
            collectivity.setAgriculturalSpecialty(dto.getSpecialty());
            collectivity.setFederationApproval(dto.isFederationApproval());
            collectivity.setPresidentId(structure.getPresident());
            collectivity.setVicePresidentId(structure.getVicePresident());
            collectivity.setTreasurerId(structure.getTreasurer());
            collectivity.setSecretaryId(structure.getSecretary());
            collectivity.setMembersIds(memberIds);

            String generatedName = "Collectivité_" + dto.getLocation() + "_" + System.currentTimeMillis();
            collectivity.setName(generatedName);

            Collectivity saved = collectivityRepository.save(collectivity);
            collectivityRepository.addMembersToCollectivity(saved.getId(), memberIds);

            saved.setMembers(members);
            saved.setPresident(findMemberById(structure.getPresident()));
            saved.setVicePresident(findMemberById(structure.getVicePresident()));
            saved.setTreasurer(findMemberById(structure.getTreasurer()));
            saved.setSecretary(findMemberById(structure.getSecretary()));

            result.add(CollectivityDTO.fromCollectivity(saved));
        }

        return result;
    }

    private Member findMemberById(String id) {
        Member member = memberRepository.findById(id);
        if (member == null) {
            throw new RuntimeException("Member not found: " + id);
        }
        return member;
    }
}