package com.federation.agriculture.repository;

import com.federation.agriculture.config.DatabaseConfig;
import com.federation.agriculture.model.Member;
import com.federation.agriculture.model.Gender;
import com.federation.agriculture.model.MemberOccupation;
import java.sql.*;
import java.util.*;

public class MemberRepository {

    private final DatabaseConfig dbConfig;

    public MemberRepository(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public Member save(Member member) {
        String sql = "INSERT INTO member (id, first_name, last_name, birth_date, gender, " +
                "address, profession, phone_number, email, occupation, " +
                "registration_fee_paid, membership_dues_paid, membership_date) " +
                "VALUES (?, ?, ?, ?, CAST(? AS gender), ?, ?, ?, ?, CAST(? AS member_occupation), ?, ?, ?)";

        if (member.getId() == null || member.getId().isEmpty()) {
            member.setId(UUID.randomUUID().toString());
        }

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, member.getId());
            pstmt.setString(2, member.getFirstName());
            pstmt.setString(3, member.getLastName());
            pstmt.setDate(4, java.sql.Date.valueOf(member.getBirthDate()));
            pstmt.setString(5, member.getGender().name());
            pstmt.setString(6, member.getAddress());
            pstmt.setString(7, member.getProfession());
            pstmt.setInt(8, member.getPhoneNumber());
            pstmt.setString(9, member.getEmail());
            pstmt.setString(10, member.getOccupation().name());
            pstmt.setBoolean(11, member.isRegistrationFeePaid());
            pstmt.setBoolean(12, member.isMembershipDuesPaid());

            if (member.getMembershipDate() != null) {
                pstmt.setDate(13, java.sql.Date.valueOf(member.getMembershipDate()));
            } else {
                pstmt.setNull(13, java.sql.Types.DATE);
            }

            pstmt.executeUpdate();
            return member;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Member findById(String id) {
        String sql = "SELECT id, first_name, last_name, birth_date, gender, " +
                "address, profession, phone_number, email, occupation, " +
                "collectivity_id, registration_fee_paid, membership_dues_paid, membership_date " +
                "FROM member WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
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

                    // AJOUTER CETTE LIGNE
                    member.setCollectivityId(rs.getString("collectivity_id"));

                    member.setRegistrationFeePaid(rs.getBoolean("registration_fee_paid"));
                    member.setMembershipDuesPaid(rs.getBoolean("membership_dues_paid"));
                    java.sql.Date membershipDateSql = rs.getDate("membership_date");
                    if (membershipDateSql != null) {
                        member.setMembershipDate(membershipDateSql.toLocalDate());
                    }
                    return member;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Member> findAllByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        String sql = "SELECT id, first_name, last_name, birth_date, gender, " +
                "address, profession, phone_number, email, occupation, " +
                "collectivity_id, registration_fee_paid, membership_dues_paid, membership_date " +
                "FROM member WHERE id = ANY(?)";

        List<Member> members = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            Array array = conn.createArrayOf("VARCHAR", ids.toArray());
            pstmt.setArray(1, array);

            try (ResultSet rs = pstmt.executeQuery()) {
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

                    // AJOUTER CETTE LIGNE
                    member.setCollectivityId(rs.getString("collectivity_id"));

                    member.setRegistrationFeePaid(rs.getBoolean("registration_fee_paid"));
                    member.setMembershipDuesPaid(rs.getBoolean("membership_dues_paid"));
                    java.sql.Date membershipDateSql = rs.getDate("membership_date");
                    if (membershipDateSql != null) {
                        member.setMembershipDate(membershipDateSql.toLocalDate());
                    }
                    members.add(member);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }
}