package com.federation.agriculture.repository;

import com.federation.agriculture.config.DatabaseConfig;
import com.federation.agriculture.dto.CreateMemberPaymentDTO;
import com.federation.agriculture.dto.FinancialAccountDTO;
import com.federation.agriculture.dto.MemberPaymentDTO;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class MemberPaymentRepository {

    private final DatabaseConfig dbConfig;
    private final FinancialAccountRepository financialAccountRepository;

    public MemberPaymentRepository(DatabaseConfig dbConfig, FinancialAccountRepository financialAccountRepository) {
        this.dbConfig = dbConfig;
        this.financialAccountRepository = financialAccountRepository;
    }

    public MemberPaymentDTO save(String memberId, CreateMemberPaymentDTO paymentDTO, String membershipFeeId) {
        String sql = "INSERT INTO member_payment (id, member_id, membership_fee_id, amount, payment_mode, account_credited_id, creation_date) " +
                "VALUES (?, ?, ?, ?, CAST(? AS payment_mode), ?, ?)";
        String id = UUID.randomUUID().toString();
        LocalDate creationDate = LocalDate.now();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            pstmt.setString(2, memberId);
            pstmt.setString(3, membershipFeeId);
            pstmt.setDouble(4, paymentDTO.getAmount());
            pstmt.setString(5, paymentDTO.getPaymentMode());  // Ceci sera CAST en enum
            pstmt.setString(6, paymentDTO.getAccountCreditedIdentifier());
            pstmt.setDate(7, Date.valueOf(creationDate));

            pstmt.executeUpdate();

            MemberPaymentDTO payment = new MemberPaymentDTO();
            payment.setId(id);
            payment.setAmount(paymentDTO.getAmount());
            payment.setPaymentMode(paymentDTO.getPaymentMode());
            payment.setCreationDate(creationDate);

            if (paymentDTO.getAccountCreditedIdentifier() != null) {
                FinancialAccountDTO account = financialAccountRepository.findById(paymentDTO.getAccountCreditedIdentifier());
                payment.setAccountCredited(account);
            }

            return payment;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Ajouter cette méthode pour obtenir le total payé par un membre pour une période
    public double getTotalPaidByMemberAndPeriod(String memberId, LocalDate from, LocalDate to) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM member_payment " +
                "WHERE member_id = ? AND creation_date BETWEEN ? AND ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, memberId);
            pstmt.setDate(2, java.sql.Date.valueOf(from));
            pstmt.setDate(3, java.sql.Date.valueOf(to));
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