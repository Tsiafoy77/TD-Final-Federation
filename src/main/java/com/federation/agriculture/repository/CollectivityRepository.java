package com.federation.agriculture.repository;

import com.federation.agriculture.config.DatabaseConfig;
import com.federation.agriculture.model.Collectivity;
import java.sql.*;
import java.util.*;

public class CollectivityRepository {

    private final DatabaseConfig dbConfig;

    public CollectivityRepository(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public Collectivity save(Collectivity collectivity) {
        String sql = "INSERT INTO collectivity (id, name, location, agricultural_specialty, federation_approval, " +
                "president_id, vice_president_id, treasurer_id, secretary_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        if (collectivity.getId() == null || collectivity.getId().isEmpty()) {
            collectivity.setId(UUID.randomUUID().toString());
        }

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, collectivity.getId());
            pstmt.setString(2, collectivity.getName());
            pstmt.setString(3, collectivity.getLocation());
            pstmt.setString(4, collectivity.getAgriculturalSpecialty());
            pstmt.setBoolean(5, collectivity.isFederationApproval());
            pstmt.setString(6, collectivity.getPresidentId());
            pstmt.setString(7, collectivity.getVicePresidentId());
            pstmt.setString(8, collectivity.getTreasurerId());
            pstmt.setString(9, collectivity.getSecretaryId());

            pstmt.executeUpdate();
            return collectivity;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Collectivity findById(String id) {
        String sql = "SELECT id, name, location, agricultural_specialty, federation_approval, " +
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

    public List<String> findMemberIdsByCollectivityId(String collectivityId) {
        String sql = "SELECT member_id FROM collectivity_member WHERE collectivity_id = ?";
        List<String> memberIds = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, collectivityId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    memberIds.add(rs.getString("member_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return memberIds;
    }
}