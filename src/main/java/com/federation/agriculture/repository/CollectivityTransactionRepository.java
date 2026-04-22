package com.federation.agriculture.repository;

import com.federation.agriculture.config.DatabaseConfig;
import com.federation.agriculture.model.CollectivityTransaction;
import com.federation.agriculture.model.PaymentMode;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CollectivityTransactionRepository {

    private final DatabaseConfig dbConfig;

    public CollectivityTransactionRepository(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    /**
     * Sauvegarde une nouvelle transaction en base.
     * Appelée automatiquement à chaque paiement membre.
     */
    public CollectivityTransaction save(CollectivityTransaction transaction) {
        String sql = "INSERT INTO collectivity_transaction " +
                "(id, collectivity_id, creation_date, amount, payment_mode, account_credited_id, member_debited_id) " +
                "VALUES (?, ?, ?, ?, CAST(? AS payment_mode), ?, ?)";

        if (transaction.getId() == null || transaction.getId().isEmpty()) {
            transaction.setId(UUID.randomUUID().toString());
        }

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, transaction.getId());
            pstmt.setString(2, transaction.getCollectivityId());
            pstmt.setDate(3, java.sql.Date.valueOf(transaction.getCreationDate()));
            pstmt.setBigDecimal(4, transaction.getAmount());
            pstmt.setString(5, transaction.getPaymentMode().name());
            pstmt.setString(6, transaction.getAccountCreditedId());
            pstmt.setString(7, transaction.getMemberDebitedId());

            pstmt.executeUpdate();
            return transaction;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde de la transaction", e);
        }
    }

    /**
     * Récupère les transactions d'une collectivité filtrées par période.
     * "from" et "to" sont inclusifs (BETWEEN en SQL).
     */
    public List<CollectivityTransaction> findByCollectivityIdAndPeriod(
            String collectivityId, LocalDate from, LocalDate to) {

        String sql = "SELECT id, collectivity_id, creation_date, amount, payment_mode, " +
                "account_credited_id, member_debited_id " +
                "FROM collectivity_transaction " +
                "WHERE collectivity_id = ? AND creation_date BETWEEN ? AND ? " +
                "ORDER BY creation_date DESC";

        List<CollectivityTransaction> transactions = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, collectivityId);
            pstmt.setDate(2, java.sql.Date.valueOf(from));
            pstmt.setDate(3, java.sql.Date.valueOf(to));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSet(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des transactions", e);
        }

        return transactions;
    }

    private CollectivityTransaction mapResultSet(ResultSet rs) throws SQLException {
        CollectivityTransaction tx = new CollectivityTransaction();
        tx.setId(rs.getString("id"));
        tx.setCollectivityId(rs.getString("collectivity_id"));

        java.sql.Date date = rs.getDate("creation_date");
        if (date != null) {
            tx.setCreationDate(date.toLocalDate());
        }

        tx.setAmount(rs.getBigDecimal("amount"));
        tx.setPaymentMode(PaymentMode.valueOf(rs.getString("payment_mode")));
        tx.setAccountCreditedId(rs.getString("account_credited_id"));
        tx.setMemberDebitedId(rs.getString("member_debited_id"));
        return tx;
    }
}