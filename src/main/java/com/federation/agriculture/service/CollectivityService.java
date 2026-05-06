package com.federation.agriculture.service;

import com.federation.agriculture.config.DatabaseConfig;
import com.federation.agriculture.dto.*;
import com.federation.agriculture.exception.BadRequestException;
import com.federation.agriculture.model.Collectivity;
import com.federation.agriculture.model.Member;
import com.federation.agriculture.repository.CollectivityRepository;
import com.federation.agriculture.repository.CollectivityTransactionRepository;
import com.federation.agriculture.repository.MemberRepository;
import com.federation.agriculture.repository.MembershipFeeRepository;
import com.federation.agriculture.repository.MemberPaymentRepository;
import com.federation.agriculture.dto.MembershipFeeDTO;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import com.federation.agriculture.repository.FinancialAccountRepository;
import com.federation.agriculture.dto.CollectivityLocalStatisticsDTO;
import com.federation.agriculture.dto.CollectivityOverallStatisticsDTO;
import com.federation.agriculture.dto.CollectivityInformationDTO;
import com.federation.agriculture.dto.MemberDescriptionDTO;

public class CollectivityService {

    private final CollectivityRepository collectivityRepository;
    private final MemberRepository memberRepository;
    private final DatabaseConfig databaseConfig;
    private final MembershipFeeRepository membershipFeeRepository;
    private final CollectivityTransactionRepository collectivityTransactionRepository;
    private final FinancialAccountRepository financialAccountRepository;
    private final MemberPaymentRepository memberPaymentRepository;

    public CollectivityService(CollectivityRepository collectivityRepository,
                               MemberRepository memberRepository,
                               DatabaseConfig databaseConfig,
                               MembershipFeeRepository membershipFeeRepository,
                               CollectivityTransactionRepository collectivityTransactionRepository,
                               FinancialAccountRepository financialAccountRepository,
                               MemberPaymentRepository memberPaymentRepository) {
        this.collectivityRepository = collectivityRepository;
        this.memberRepository = memberRepository;
        this.databaseConfig = databaseConfig;
        this.membershipFeeRepository = membershipFeeRepository;
        this.collectivityTransactionRepository = collectivityTransactionRepository;
        this.financialAccountRepository = financialAccountRepository;
        this.memberPaymentRepository = memberPaymentRepository;
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
                throw new BadRequestException("Amount must be greater than 0");
            }

            String frequency = fee.getFrequency();
            if (frequency == null || (!frequency.equals("WEEKLY") && !frequency.equals("MONTHLY") &&
                    !frequency.equals("ANNUALLY") && !frequency.equals("PUNCTUALLY"))) {
                throw new BadRequestException("Invalid frequency: " + frequency);
            }
        }

        return membershipFeeRepository.saveAll(collectivityId, fees);
    }

    // FONCTIONNALITÉ D - Transactions des collectivités
    public List<CollectivityTransactionDTO> getTransactions(String collectivityId, LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new BadRequestException("Query parameters 'from' and 'to' are mandatory");
        }

        if (from.isAfter(to)) {
            throw new BadRequestException("'from' date must be before or equal to 'to' date");
        }

        Collectivity collectivity = collectivityRepository.findById(collectivityId);
        if (collectivity == null) {
            throw new RuntimeException("Collectivity not found: " + collectivityId);
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

    public List<FinancialAccountDTO> getFinancialAccounts(String collectivityId, LocalDate at) {
        if (at == null) {
            throw new BadRequestException("Query parameter 'at' is mandatory");
        }

        Collectivity collectivity = collectivityRepository.findById(collectivityId);
        if (collectivity == null) {
            throw new RuntimeException("Collectivity not found: " + collectivityId);
        }

        return financialAccountRepository.findByCollectivityIdAndDate(collectivityId, at);
    }

    public CollectivityDTO getCollectivityById(String collectivityId) {
        Collectivity collectivity = collectivityRepository.findById(collectivityId);
        if (collectivity == null) {
            throw new RuntimeException("Collectivity not found: " + collectivityId);
        }

        if (collectivity.getMembersIds() != null && !collectivity.getMembersIds().isEmpty()) {
            collectivity.setMembers(memberRepository.findAllByIds(collectivity.getMembersIds()));
        }

        if (collectivity.getPresidentId() != null) {
            collectivity.setPresident(memberRepository.findById(collectivity.getPresidentId()));
        }
        if (collectivity.getVicePresidentId() != null) {
            collectivity.setVicePresident(memberRepository.findById(collectivity.getVicePresidentId()));
        }
        if (collectivity.getTreasurerId() != null) {
            collectivity.setTreasurer(memberRepository.findById(collectivity.getTreasurerId()));
        }
        if (collectivity.getSecretaryId() != null) {
            collectivity.setSecretary(memberRepository.findById(collectivity.getSecretaryId()));
        }

        return CollectivityDTO.fromCollectivity(collectivity);
    }

    public List<CollectivityDTO> getAllCollectivities() {
        List<Collectivity> collectivities = collectivityRepository.findAll();
        List<CollectivityDTO> result = new ArrayList<>();

        for (Collectivity collectivity : collectivities) {
            if (collectivity.getMembersIds() != null && !collectivity.getMembersIds().isEmpty()) {
                collectivity.setMembers(memberRepository.findAllByIds(collectivity.getMembersIds()));
            }
            result.add(CollectivityDTO.fromCollectivity(collectivity));
        }
        return result;
    }

    public List<CollectivityLocalStatisticsDTO> getMemberStatistics(String collectivityId, LocalDate from, LocalDate to) {
        // Vérification de l'existence de la collectivité
        Collectivity collectivity = collectivityRepository.findById(collectivityId);
        if (collectivity == null) {
            throw new RuntimeException("Collectivity not found: " + collectivityId);
        }
        // Vérification des paramètres
        if (from == null || to == null) {
            throw new BadRequestException("Query parameters 'from' and 'to' are mandatory");
        }
        if (from.isAfter(to)) {
            throw new BadRequestException("'from' date must be before or equal to 'to' date");
        }

        List<Member> members = collectivityRepository.findMembersByCollectivityId(collectivityId);
        List<MembershipFeeDTO> activeFees = collectivityRepository.findActiveMembershipFeesByCollectivityId(collectivityId);
        List<CollectivityLocalStatisticsDTO> result = new ArrayList<>();

        for (Member member : members) {
            double totalPaid = memberPaymentRepository.getTotalPaidByMemberAndPeriod(member.getId(), from, to);
            double totalUnpaid = 0;
            for (MembershipFeeDTO fee : activeFees) {
                if (!fee.getEligibleFrom().isAfter(to)) {
                    double paidForFee = getPaidForMembershipFee(member.getId(), fee.getId(), from, to);
                    if (paidForFee < fee.getAmount()) {
                        totalUnpaid += (fee.getAmount() - paidForFee);
                    }
                }
            }
            MemberDescriptionDTO memberDesc = new MemberDescriptionDTO(
                    member.getId(), member.getFirstName(), member.getLastName(),
                    member.getEmail(), member.getOccupation().name()
            );
            result.add(new CollectivityLocalStatisticsDTO(memberDesc, totalPaid, totalUnpaid));
        }
        return result;
    }

    public List<CollectivityOverallStatisticsDTO> getAllCollectivitiesStatistics(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new BadRequestException("Query parameters 'from' and 'to' are mandatory");
        }
        if (from.isAfter(to)) {
            throw new BadRequestException("'from' date must be before or equal to 'to' date");
        }

        List<Collectivity> collectivities = collectivityRepository.findAll();
        List<CollectivityOverallStatisticsDTO> result = new ArrayList<>();

        for (Collectivity collectivity : collectivities) {
            List<Member> members = collectivityRepository.findMembersByCollectivityId(collectivity.getId());
            List<MembershipFeeDTO> activeFees = collectivityRepository.findActiveMembershipFeesByCollectivityId(collectivity.getId());

            int membersUpToDate = 0;
            int newMembersCount = 0;

            for (Member member : members) {
                boolean isUpToDate = true;
                for (MembershipFeeDTO fee : activeFees) {
                    double paid = getPaidForMembershipFee(member.getId(), fee.getId(), from, to);
                    if (paid < fee.getAmount()) {
                        isUpToDate = false;
                        break;
                    }
                }
                if (isUpToDate) {
                    membersUpToDate++;
                }
                if (member.getMembershipDate() != null &&
                        !member.getMembershipDate().isBefore(from) &&
                        !member.getMembershipDate().isAfter(to)) {
                    newMembersCount++;
                }
            }

            double percentage = members.isEmpty() ? 0 : (membersUpToDate * 100.0 / members.size());
            CollectivityInformationDTO info = new CollectivityInformationDTO(collectivity.getNumber(), collectivity.getName());
            result.add(new CollectivityOverallStatisticsDTO(info, newMembersCount, percentage));
        }
        return result;
    }
    // UNE SEULE méthode getPaidForMembershipFee (supprime l'autre)
    private double getPaidForMembershipFee(String memberId, String feeId, LocalDate from, LocalDate to) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM member_payment " +
                "WHERE member_id = ? AND membership_fee_id = ? AND creation_date BETWEEN ? AND ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, memberId);
            pstmt.setString(2, feeId);
            pstmt.setDate(3, java.sql.Date.valueOf(from));
            pstmt.setDate(4, java.sql.Date.valueOf(to));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}