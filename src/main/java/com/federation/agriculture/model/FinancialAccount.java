package com.federation.agriculture.model;

import java.math.BigDecimal;

public class FinancialAccount {
    private String id;
    private String collectivityId;
    private AccountType type;
    private BigDecimal amount;

    private String holderName;
    private MobileBankingService mobileBankingService;
    private String mobileNumber;

    private Bank bankName;
    private String bankCode;
    private String bankBranchCode;
    private String bankAccountNumber;
    private String bankAccountKey;
}