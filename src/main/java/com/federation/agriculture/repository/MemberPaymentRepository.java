package com.federation.agriculture.repository;

import com.federation.agriculture.config.DatabaseConfig;
import com.federation.agriculture.model.MemberPayment;
import com.federation.agriculture.model.PaymentMode;

import java.sql.*;
import java.util.UUID;

public class MemberPaymentRepository {

    private final DatabaseConfig dbConfig;

    public MemberPaymentRepository(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    /**
     * Sauvegarde un paiement membre en base.
     */
    public MemberPayment save(MemberPayment payment) {
        String sql = "INSERT INTO member_payment " +
                "(id, member_id, membership_fee_id, amount, payment_mode, account_credited_id, creation_date) " +
                "VALUES (?, ?, ?, ?, CAST(? AS payment_mode), ?, ?)";

        if (payment.getId() == null || payment.getId().isEmpty()) {
            payment.setId(UUID.randomUUID().toString());
        }

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, payment.getId());
            pstmt.setString(2, payment.getMemberId());
            pstmt.setString(3, payment.getMembershipFeeId());
            pstmt.setBigDecimal(4, payment.getAmount());
            pstmt.setString(5, payment.getPaymentMode().name());
            pstmt.setString(6, payment.getAccountCreditedId());
            pstmt.setDate(7, java.sql.Date.valueOf(payment.getCreationDate()));

            pstmt.executeUpdate();
            return payment;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde du paiement", e);
        }
    }
}
