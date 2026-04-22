package com.federation.agriculture.repository;

import com.federation.agriculture.config.DatabaseConfig;
import com.federation.agriculture.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FinancialAccountRepository {

    private final DatabaseConfig dbConfig;

    public FinancialAccountRepository(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public FinancialAccount findById(String id) {
        String sql = "SELECT id, collectivity_id, type, amount, holder_name, " +
                "mobile_banking_service, mobile_number, bank_name, bank_code, " +
                "bank_branch_code, bank_account_number, bank_account_key " +
                "FROM financial_account WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du compte financier", e);
        }

        return null;
    }


    public List<FinancialAccount> findByCollectivityId(String collectivityId) {
        String sql = "SELECT id, collectivity_id, type, amount, holder_name, " +
                "mobile_banking_service, mobile_number, bank_name, bank_code, " +
                "bank_branch_code, bank_account_number, bank_account_key " +
                "FROM financial_account WHERE collectivity_id = ?";

        List<FinancialAccount> accounts = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, collectivityId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapResultSet(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des comptes financiers", e);
        }

        return accounts;
    }

    public void creditAccount(String accountId, java.math.BigDecimal amount) {
        String sql = "UPDATE financial_account SET amount = amount + ? WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBigDecimal(1, amount);
            pstmt.setString(2, accountId);

            int updated = pstmt.executeUpdate();
            if (updated == 0) {
                throw new RuntimeException("Compte financier introuvable : " + accountId);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du crédit du compte", e);
        }
    }

    private FinancialAccount mapResultSet(ResultSet rs) throws SQLException {
        FinancialAccount account = new FinancialAccount();
        account.setId(rs.getString("id"));
        account.setCollectivityId(rs.getString("collectivity_id"));
        account.setType(AccountType.valueOf(rs.getString("type")));
        account.setAmount(rs.getBigDecimal("amount"));

        String mobileBankingService = rs.getString("mobile_banking_service");
        if (mobileBankingService != null) {
            account.setMobileBankingService(MobileBankingService.valueOf(mobileBankingService));
        }
        account.setHolderName(rs.getString("holder_name"));
        account.setMobileNumber(rs.getString("mobile_number"));

        String bankName = rs.getString("bank_name");
        if (bankName != null) {
            account.setBankName(Bank.valueOf(bankName));
        }
        account.setBankCode(rs.getString("bank_code"));
        account.setBankBranchCode(rs.getString("bank_branch_code"));
        account.setBankAccountNumber(rs.getString("bank_account_number"));
        account.setBankAccountKey(rs.getString("bank_account_key"));

        return account;
    }
}