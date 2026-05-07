package com.federation.agriculture.repository;

import com.federation.agriculture.config.DatabaseConfig;
import com.federation.agriculture.dto.FinancialAccountDTO;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class FinancialAccountRepository {

    private final DatabaseConfig dbConfig;

    public FinancialAccountRepository(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public FinancialAccountDTO findById(String id) {
        String sql = "SELECT id, account_type, holder_name, amount, mobile_banking_service, mobile_number, " +
                "bank_name, bank_code, bank_branch_code, bank_account_number, bank_account_key " +
                "FROM financial_account WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                FinancialAccountDTO account = new FinancialAccountDTO();
                account.setId(rs.getString("id"));
                account.setAccountType(rs.getString("account_type"));
                account.setHolderName(rs.getString("holder_name"));
                account.setAmount(rs.getDouble("amount"));
                account.setMobileBankingService(rs.getString("mobile_banking_service"));
                account.setMobileNumber(rs.getString("mobile_number"));
                account.setBankName(rs.getString("bank_name"));
                account.setBankCode(rs.getString("bank_code"));
                account.setBankBranchCode(rs.getString("bank_branch_code"));
                account.setBankAccountNumber(rs.getString("bank_account_number"));
                account.setBankAccountKey(rs.getString("bank_account_key"));
                return account;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<FinancialAccountDTO> findByCollectivityIdAndDate(String collectivityId, LocalDate at) {
        String sql = "SELECT id, type, holder_name, amount, mobile_banking_service, mobile_number, " +
                "bank_name, bank_code, bank_branch_code, bank_account_number, bank_account_key " +
                "FROM financial_account WHERE collectivity_id = ?";
        List<FinancialAccountDTO> accounts = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, collectivityId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                FinancialAccountDTO account = new FinancialAccountDTO();
                account.setId(rs.getString("id"));
                account.setAccountType(rs.getString("type"));
                account.setHolderName(rs.getString("holder_name"));
                account.setAmount(rs.getDouble("amount"));
                account.setMobileBankingService(rs.getString("mobile_banking_service"));
                account.setMobileNumber(rs.getString("mobile_number"));
                account.setBankName(rs.getString("bank_name"));
                account.setBankCode(rs.getString("bank_code"));
                account.setBankBranchCode(rs.getString("bank_branch_code"));
                account.setBankAccountNumber(rs.getString("bank_account_number"));
                account.setBankAccountKey(rs.getString("bank_account_key"));
                accounts.add(account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }
}