package com.federation.agriculture.repository;

import com.federation.agriculture.config.DatabaseConfig;
import com.federation.agriculture.model.Collectivity;
import com.federation.agriculture.model.Gender;
import com.federation.agriculture.model.Member;
import com.federation.agriculture.model.MemberOccupation;
import com.federation.agriculture.dto.MembershipFeeDTO;
import java.sql.*;
import java.util.*;

public class CollectivityRepository {

    private final DatabaseConfig dbConfig;

    public CollectivityRepository(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public Collectivity save(Collectivity collectivity) {
        String sql = "INSERT INTO collectivity (id, name, number, location, agricultural_specialty, federation_approval, " +
                "president_id, vice_president_id, treasurer_id, secretary_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET " +
                "name = EXCLUDED.name, number = EXCLUDED.number, location = EXCLUDED.location, " +
                "agricultural_specialty = EXCLUDED.agricultural_specialty, " +
                "federation_approval = EXCLUDED.federation_approval, " +
                "president_id = EXCLUDED.president_id, " +
                "vice_president_id = EXCLUDED.vice_president_id, " +
                "treasurer_id = EXCLUDED.treasurer_id, " +
                "secretary_id = EXCLUDED.secretary_id";

        if (collectivity.getId() == null || collectivity.getId().isEmpty()) {
            collectivity.setId(UUID.randomUUID().toString());
        }

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, collectivity.getId());
            pstmt.setString(2, collectivity.getName());
            pstmt.setString(3, collectivity.getNumber());
            pstmt.setString(4, collectivity.getLocation());
            pstmt.setString(5, collectivity.getAgriculturalSpecialty());
            pstmt.setBoolean(6, collectivity.isFederationApproval());
            pstmt.setString(7, collectivity.getPresidentId());
            pstmt.setString(8, collectivity.getVicePresidentId());
            pstmt.setString(9, collectivity.getTreasurerId());
            pstmt.setString(10, collectivity.getSecretaryId());

            pstmt.executeUpdate();
            return collectivity;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Collectivity findById(String id) {
        String sql = "SELECT id, name, number, location, agricultural_specialty, federation_approval, " +
                "president_id, vice_president_id, treasurer_id, secretary_id, created_at " +
                "FROM collectivity WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Collectivity collectivity = new Collectivity();
                    collectivity.setId(rs.getString("id"));
                    collectivity.setName(rs.getString("name"));
                    collectivity.setNumber(rs.getString("number"));
                    collectivity.setLocation(rs.getString("location"));
                    collectivity.setAgriculturalSpecialty(rs.getString("agricultural_specialty"));
                    collectivity.setFederationApproval(rs.getBoolean("federation_approval"));
                    collectivity.setPresidentId(rs.getString("president_id"));
                    collectivity.setVicePresidentId(rs.getString("vice_president_id"));
                    collectivity.setTreasurerId(rs.getString("treasurer_id"));
                    collectivity.setSecretaryId(rs.getString("secretary_id"));
                    collectivity.setCreatedAt(rs.getTimestamp("created_at"));

                    // Charger les membres
                    collectivity.setMembersIds(findMemberIdsByCollectivityId(id));

                    return collectivity;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Collectivity findByName(String name) {
        String sql = "SELECT id, name, number, location, agricultural_specialty, federation_approval, " +
                "president_id, vice_president_id, treasurer_id, secretary_id, created_at " +
                "FROM collectivity WHERE name = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCollectivity(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Collectivity findByNumber(String number) {
        String sql = "SELECT id, name, number, location, agricultural_specialty, federation_approval, " +
                "president_id, vice_president_id, treasurer_id, secretary_id, created_at " +
                "FROM collectivity WHERE number = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, number);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCollectivity(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addMembersToCollectivity(String collectivityId, List<String> memberIds) {
        if (memberIds == null || memberIds.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO collectivity_member (collectivity_id, member_id) VALUES (?, ?)";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (String memberId : memberIds) {
                pstmt.setString(1, collectivityId);
                pstmt.setString(2, memberId);
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour trouver les membres d'une collectivité
    // Trouver les membres complets d'une collectivité
    public List<Member> findMembersByCollectivityId(String collectivityId) {
        String sql = "SELECT m.id, m.first_name, m.last_name, m.birth_date, m.gender, " +
                "m.address, m.profession, m.phone_number, m.email, m.occupation, " +
                "m.collectivity_id, m.registration_fee_paid, m.membership_dues_paid, m.membership_date " +
                "FROM member m INNER JOIN collectivity_member cm ON m.id = cm.member_id " +
                "WHERE cm.collectivity_id = ?";
        List<Member> members = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, collectivityId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Member member = new Member();
                member.setId(rs.getString("id"));
                member.setFirstName(rs.getString("first_name"));
                member.setLastName(rs.getString("last_name"));
                java.sql.Date birthDateSql = rs.getDate("birth_date");
                if (birthDateSql != null) {
                    member.setBirthDate(birthDateSql.toLocalDate());
                }
                member.setGender(Gender.valueOf(rs.getString("gender")));
                member.setAddress(rs.getString("address"));
                member.setProfession(rs.getString("profession"));
                member.setPhoneNumber(rs.getInt("phone_number"));
                member.setEmail(rs.getString("email"));
                member.setOccupation(MemberOccupation.valueOf(rs.getString("occupation")));
                member.setCollectivityId(rs.getString("collectivity_id"));
                member.setRegistrationFeePaid(rs.getBoolean("registration_fee_paid"));
                member.setMembershipDuesPaid(rs.getBoolean("membership_dues_paid"));
                java.sql.Date membershipDateSql = rs.getDate("membership_date");
                if (membershipDateSql != null) {
                    member.setMembershipDate(membershipDateSql.toLocalDate());
                }
                members.add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    public List<String> findMemberIdsByCollectivityId(String collectivityId) {
        String sql = "SELECT member_id FROM collectivity_member WHERE collectivity_id = ?";
        List<String> memberIds = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, collectivityId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                memberIds.add(rs.getString("member_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return memberIds;
    }


    // Méthode pour trouver les cotisations actives d'une collectivité
    public List<MembershipFeeDTO> findActiveMembershipFeesByCollectivityId(String collectivityId) {
        String sql = "SELECT id, eligible_from, frequency, amount, label, status " +
                "FROM membership_fee " +
                "WHERE collectivity_id = ? AND status = 'ACTIVE' " +
                "AND frequency IN ('ANNUALLY', 'MONTHLY', 'WEEKLY')";
        List<MembershipFeeDTO> fees = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, collectivityId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                MembershipFeeDTO fee = new MembershipFeeDTO();
                fee.setId(rs.getString("id"));
                fee.setEligibleFrom(rs.getDate("eligible_from").toLocalDate());
                fee.setFrequency(rs.getString("frequency"));
                fee.setAmount(rs.getDouble("amount"));
                fee.setLabel(rs.getString("label"));
                fee.setStatus(rs.getString("status"));
                fees.add(fee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fees;
    }

    private Collectivity mapResultSetToCollectivity(ResultSet rs) throws SQLException {
        Collectivity collectivity = new Collectivity();
        collectivity.setId(rs.getString("id"));
        collectivity.setName(rs.getString("name"));
        collectivity.setNumber(rs.getString("number"));
        collectivity.setLocation(rs.getString("location"));
        collectivity.setAgriculturalSpecialty(rs.getString("agricultural_specialty"));
        collectivity.setFederationApproval(rs.getBoolean("federation_approval"));
        collectivity.setPresidentId(rs.getString("president_id"));
        collectivity.setVicePresidentId(rs.getString("vice_president_id"));
        collectivity.setTreasurerId(rs.getString("treasurer_id"));
        collectivity.setSecretaryId(rs.getString("secretary_id"));
        collectivity.setCreatedAt(rs.getTimestamp("created_at"));
        return collectivity;
    }

    public List<Collectivity> findAll() {
        String sql = "SELECT id, name, number, location, agricultural_specialty, federation_approval, " +
                "president_id, vice_president_id, treasurer_id, secretary_id, created_at " +
                "FROM collectivity";
        List<Collectivity> collectivities = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Collectivity collectivity = new Collectivity();
                collectivity.setId(rs.getString("id"));
                collectivity.setName(rs.getString("name"));
                collectivity.setNumber(rs.getString("number"));
                collectivity.setLocation(rs.getString("location"));
                collectivity.setAgriculturalSpecialty(rs.getString("agricultural_specialty"));
                collectivity.setFederationApproval(rs.getBoolean("federation_approval"));
                collectivity.setPresidentId(rs.getString("president_id"));
                collectivity.setVicePresidentId(rs.getString("vice_president_id"));
                collectivity.setTreasurerId(rs.getString("treasurer_id"));
                collectivity.setSecretaryId(rs.getString("secretary_id"));
                collectivity.setCreatedAt(rs.getTimestamp("created_at"));

                // Charger les IDs des membres
                collectivity.setMembersIds(findMemberIdsByCollectivityId(collectivity.getId()));

                collectivities.add(collectivity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return collectivities;
    }
}