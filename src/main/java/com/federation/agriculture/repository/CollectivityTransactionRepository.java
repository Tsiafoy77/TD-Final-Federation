package com.federation.agriculture.repository;

import com.federation.agriculture.config.DatabaseConfig;
import com.federation.agriculture.dto.CollectivityTransactionDTO;
import com.federation.agriculture.dto.FinancialAccountDTO;
import com.federation.agriculture.dto.MemberDTO;
import com.federation.agriculture.model.Member;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class CollectivityTransactionRepository {

    private final DatabaseConfig dbConfig;
    private final MemberRepository memberRepository;
    private final FinancialAccountRepository financialAccountRepository;

    public CollectivityTransactionRepository(DatabaseConfig dbConfig,
                                             MemberRepository memberRepository,
                                             FinancialAccountRepository financialAccountRepository) {
        this.dbConfig = dbConfig;
        this.memberRepository = memberRepository;
        this.financialAccountRepository = financialAccountRepository;
    }

    public List<CollectivityTransactionDTO> findByCollectivityIdAndDateRange(String collectivityId, LocalDate from, LocalDate to) {
        String sql = "SELECT id, member_debited_id, amount, payment_mode, account_credited_id, creation_date " +
                "FROM collectivity_transaction WHERE collectivity_id = ? AND creation_date BETWEEN ? AND ? " +
                "ORDER BY creation_date DESC";
        List<CollectivityTransactionDTO> transactions = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, collectivityId);
            pstmt.setDate(2, Date.valueOf(from));
            pstmt.setDate(3, Date.valueOf(to));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                CollectivityTransactionDTO transaction = new CollectivityTransactionDTO();
                transaction.setId(rs.getString("id"));
                transaction.setCreationDate(rs.getDate("creation_date").toLocalDate());
                transaction.setAmount(rs.getDouble("amount"));
                transaction.setPaymentMode(rs.getString("payment_mode"));

                String accountId = rs.getString("account_credited_id");
                if (accountId != null) {
                    transaction.setAccountCredited(financialAccountRepository.findById(accountId));
                }

                String memberId = rs.getString("member_debited_id");
                Member member = memberRepository.findById(memberId);
                if (member != null) {
                    transaction.setMemberDebited(MemberDTO.fromMember(member));
                }

                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public void save(String collectivityId, String memberId, double amount, String paymentMode,
                     String accountCreditedId, LocalDate creationDate) {
        String sql = "INSERT INTO collectivity_transaction (id, collectivity_id, member_debited_id, amount, payment_mode, account_credited_id, creation_date) " +
                "VALUES (?, ?, ?, ?, CAST(? AS payment_mode), ?, ?)";
        String id = UUID.randomUUID().toString();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            pstmt.setString(2, collectivityId);
            pstmt.setString(3, memberId);
            pstmt.setDouble(4, amount);
            pstmt.setString(5, paymentMode);

            // Gérer le cas où accountCreditedId est null
            if (accountCreditedId != null && !accountCreditedId.isEmpty()) {
                pstmt.setString(6, accountCreditedId);
            } else {
                pstmt.setNull(6, java.sql.Types.VARCHAR);
            }

            pstmt.setDate(7, Date.valueOf(creationDate));
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}