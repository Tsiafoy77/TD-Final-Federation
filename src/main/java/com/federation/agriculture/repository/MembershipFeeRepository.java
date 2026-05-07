package com.federation.agriculture.repository;

import com.federation.agriculture.config.DatabaseConfig;
import com.federation.agriculture.dto.CreateMembershipFeeDTO;
import com.federation.agriculture.dto.MembershipFeeDTO;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class MembershipFeeRepository {

    private final DatabaseConfig dbConfig;

    public MembershipFeeRepository(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public List<MembershipFeeDTO> findByCollectivityId(String collectivityId) {
        String sql = "SELECT id, eligible_from, frequency, amount, label, status FROM membership_fee WHERE collectivity_id = ? ORDER BY eligible_from DESC";
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

    public MembershipFeeDTO findById(String id) {
        String sql = "SELECT id, eligible_from, frequency, amount, label, status FROM membership_fee WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                MembershipFeeDTO fee = new MembershipFeeDTO();
                fee.setId(rs.getString("id"));
                fee.setEligibleFrom(rs.getDate("eligible_from").toLocalDate());
                fee.setFrequency(rs.getString("frequency"));
                fee.setAmount(rs.getDouble("amount"));
                fee.setLabel(rs.getString("label"));
                fee.setStatus(rs.getString("status"));
                return fee;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<MembershipFeeDTO> saveAll(String collectivityId, List<CreateMembershipFeeDTO> feeDTOs) {
        List<MembershipFeeDTO> result = new ArrayList<>();
        String sql = "INSERT INTO membership_fee (id, collectivity_id, eligible_from, frequency, amount, label) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (CreateMembershipFeeDTO feeDTO : feeDTOs) {
                String id = UUID.randomUUID().toString();
                pstmt.setString(1, id);
                pstmt.setString(2, collectivityId);
                pstmt.setDate(3, Date.valueOf(feeDTO.getEligibleFrom()));
                pstmt.setString(4, feeDTO.getFrequency());
                pstmt.setDouble(5, feeDTO.getAmount());
                pstmt.setString(6, feeDTO.getLabel());
                pstmt.executeUpdate();

                MembershipFeeDTO fee = new MembershipFeeDTO();
                fee.setId(id);
                fee.setEligibleFrom(feeDTO.getEligibleFrom());
                fee.setFrequency(feeDTO.getFrequency());
                fee.setAmount(feeDTO.getAmount());
                fee.setLabel(feeDTO.getLabel());
                fee.setStatus("ACTIVE");
                result.add(fee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}