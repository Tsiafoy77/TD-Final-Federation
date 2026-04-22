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
        String sql = &quot;INSERT INTO collectivity (id, name, location,
                agricultural_specialty, federation_approval, &quot; +
                &quot;president_id, vice_president_id, treasurer_id, secretary_id)
&quot; +
                &quot;VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)&quot;;
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
        String sql = &quot;SELECT id, name, location, agricultural_specialty,
        federation_approval, &quot; +
                &quot;president_id, vice_president_id, treasurer_id, secretary_id,
                created_at &quot; +

                &quot;FROM collectivity WHERE id = ?&quot;;
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Collectivity collectivity = new Collectivity();
                    collectivity.setId(rs.getString(&quot;id&quot;));
                    collectivity.setName(rs.getString(&quot;name&quot;));
                    collectivity.setLocation(rs.getString(&quot;location&quot;));
                    collectivity.setAgriculturalSpecialty(rs.getString(&quot;agricultural_specialty&quot;));
                    collectivity.setFederationApproval(rs.getBoolean(&quot;federation_approval&quot;));
                    collectivity.setPresidentId(rs.getString(&quot;president_id&quot;));
                    collectivity.setVicePresidentId(rs.getString(&quot;vice_president_id&quot;));
                    collectivity.setTreasurerId(rs.getString(&quot;treasurer_id&quot;));
                    collectivity.setSecretaryId(rs.getString(&quot;secretary_id&quot;));
                    collectivity.setCreatedAt(rs.getTimestamp(&quot;created_at&quot;));
                    return collectivity;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void addMembersToCollectivity(String collectivityId, List&lt;String&gt;
    memberIds) {
        if (memberIds == null || memberIds.isEmpty()) {
            return;
        }
        String sql = &quot;INSERT INTO collectivity_member (collectivity_id, member_id)
        VALUES (?, ?)&quot;;
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
    public List&lt;String&gt; findMemberIdsByCollectivityId(String collectivityId) {
        String sql = &quot;SELECT member_id FROM collectivity_member WHERE
        collectivity_id = ?&quot;;
        List&lt;String&gt; memberIds = new ArrayList&lt;&gt;();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, collectivityId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    memberIds.add(rs.getString(&quot;member_id&quot;));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return memberIds;
    }
}