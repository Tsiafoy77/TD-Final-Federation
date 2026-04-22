package com.federation.agriculture.service;

import com.federation.agriculture.config.DatabaseConfig;
import com.federation.agriculture.dto.CreateCollectivityDTO;
import com.federation.agriculture.dto.CreateCollectivityStructureDTO;
import com.federation.agriculture.dto.CollectivityDTO;
import com.federation.agriculture.dto.CollectivityIdentityDTO;
import com.federation.agriculture.model.Collectivity;
import com.federation.agriculture.model.Member;
import com.federation.agriculture.repository.CollectivityRepository;
import com.federation.agriculture.repository.MemberRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class CollectivityService {

    private final CollectivityRepository collectivityRepository;
    private final MemberRepository memberRepository;
    private final DatabaseConfig databaseConfig;

    // CONSTRUCTEUR AVEC LES 3 PARAMÈTRES
    public CollectivityService(CollectivityRepository collectivityRepository,
                               MemberRepository memberRepository,
                               DatabaseConfig databaseConfig) {
        this.collectivityRepository = collectivityRepository;
        this.memberRepository = memberRepository;
        this.databaseConfig = databaseConfig;
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
                throw new RuntimeException("At least 5 members must have 6 months of seniority in the federation");
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

            String generatedName = "Collectivity_" + dto.getLocation() + "_" + System.currentTimeMillis();
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

    // METHOD J - Assign unique number and name to a collectivity
    public CollectivityDTO assignIdentity(String collectivityId, CollectivityIdentityDTO identity) {
        // Check if collectivity exists
        Collectivity collectivity = collectivityRepository.findById(collectivityId);
        if (collectivity == null) {
            throw new RuntimeException("Collectivity not found: " + collectivityId);
        }

        // Update number and name directly in database
        String sql = "UPDATE collectivity SET number = ?, name = ? WHERE id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, identity.getNumber());
            pstmt.setString(2, identity.getName());
            pstmt.setString(3, collectivityId);

            int updated = pstmt.executeUpdate();
            if (updated == 0) {
                throw new RuntimeException("Failed to update collectivity");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage());
        }

        // Reload the updated collectivity
        Collectivity updatedCollectivity = collectivityRepository.findById(collectivityId);

        if (updatedCollectivity.getMembersIds() != null && !updatedCollectivity.getMembersIds().isEmpty()) {
            updatedCollectivity.setMembers(memberRepository.findAllByIds(updatedCollectivity.getMembersIds()));
        }
        if (updatedCollectivity.getPresidentId() != null) {
            updatedCollectivity.setPresident(memberRepository.findById(updatedCollectivity.getPresidentId()));
        }
        if (updatedCollectivity.getVicePresidentId() != null) {
            updatedCollectivity.setVicePresident(memberRepository.findById(updatedCollectivity.getVicePresidentId()));
        }
        if (updatedCollectivity.getTreasurerId() != null) {
            updatedCollectivity.setTreasurer(memberRepository.findById(updatedCollectivity.getTreasurerId()));
        }
        if (updatedCollectivity.getSecretaryId() != null) {
            updatedCollectivity.setSecretary(memberRepository.findById(updatedCollectivity.getSecretaryId()));
        }

        return CollectivityDTO.fromCollectivity(updatedCollectivity);
    }

    private Member findMemberById(String id) {
        Member member = memberRepository.findById(id);
        if (member == null) {
            throw new RuntimeException("Member not found: " + id);
        }
        return member;
    }
}