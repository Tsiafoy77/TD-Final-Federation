package com.federation.agriculture.service;

import com.federation.agriculture.config.DatabaseConfig;
import com.federation.agriculture.dto.*;
import com.federation.agriculture.model.Collectivity;
import com.federation.agriculture.model.Member;
import com.federation.agriculture.repository.CollectivityRepository;
import com.federation.agriculture.repository.CollectivityTransactionRepository;
import com.federation.agriculture.repository.MemberRepository;
import com.federation.agriculture.repository.MembershipFeeRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class CollectivityService {

    private final CollectivityRepository collectivityRepository;
    private final MemberRepository memberRepository;
    private final DatabaseConfig databaseConfig;
    private final MembershipFeeRepository membershipFeeRepository;
    private final CollectivityTransactionRepository collectivityTransactionRepository;

    public CollectivityService(CollectivityRepository collectivityRepository,
                               MemberRepository memberRepository,
                               DatabaseConfig databaseConfig,
                               MembershipFeeRepository membershipFeeRepository,
                               CollectivityTransactionRepository collectivityTransactionRepository) {
        this.collectivityRepository = collectivityRepository;
        this.memberRepository = memberRepository;
        this.databaseConfig = databaseConfig;
        this.membershipFeeRepository = membershipFeeRepository;
        this.collectivityTransactionRepository = collectivityTransactionRepository;
    }

    // FONCTIONNALITÉ A - Création d'une collectivité
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


    // FONCTIONNALITÉ J - Attribution d'un numéro et nom unique

    public CollectivityDTO assignIdentity(String collectivityId, CollectivityIdentityDTO identity) {
        Collectivity collectivity = collectivityRepository.findById(collectivityId);
        if (collectivity == null) {
            throw new RuntimeException("Collectivity not found: " + collectivityId);
        }

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


    // FONCTIONNALITÉ C - Gestion des frais d'adhésion

    public List<MembershipFeeDTO> getMembershipFees(String collectivityId) {
        Collectivity collectivity = collectivityRepository.findById(collectivityId);
        if (collectivity == null) {
            throw new RuntimeException("Collectivity not found: " + collectivityId);
        }
        return membershipFeeRepository.findByCollectivityId(collectivityId);
    }

    public List<MembershipFeeDTO> createMembershipFees(String collectivityId, List<CreateMembershipFeeDTO> fees) {
        Collectivity collectivity = collectivityRepository.findById(collectivityId);
        if (collectivity == null) {
            throw new RuntimeException("Collectivity not found: " + collectivityId);
        }

        for (CreateMembershipFeeDTO fee : fees) {
            if (fee.getAmount() <= 0) {
                throw new RuntimeException("Amount must be greater than 0");
            }
            String frequency = fee.getFrequency();
            if (frequency == null || (!frequency.equals("WEEKLY") && !frequency.equals("MONTHLY") &&
                    !frequency.equals("ANNUALLY") && !frequency.equals("PUNCTUALLY"))) {
                throw new RuntimeException("Invalid frequency: " + frequency);
            }
        }

        return membershipFeeRepository.saveAll(collectivityId, fees);
    }

    // FONCTIONNALITÉ D - Transactions des collectivités
    public List<CollectivityTransactionDTO> getTransactions(String collectivityId, LocalDate from, LocalDate to) {
        Collectivity collectivity = collectivityRepository.findById(collectivityId);
        if (collectivity == null) {
            throw new RuntimeException("Collectivity not found: " + collectivityId);
        }

        if (from == null || to == null) {
            throw new RuntimeException("Query parameters 'from' and 'to' are mandatory");
        }

        if (from.isAfter(to)) {
            throw new RuntimeException("'from' date must be before or equal to 'to' date");
        }

        return collectivityTransactionRepository.findByCollectivityIdAndDateRange(collectivityId, from, to);
    }


    private Member findMemberById(String id) {
        Member member = memberRepository.findById(id);
        if (member == null) {
            throw new RuntimeException("Member not found: " + id);
        }
        return member;
    }
}