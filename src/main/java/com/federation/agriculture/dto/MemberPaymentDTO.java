package com.federation.agriculture.dto;

import java.time.LocalDate;

public class MemberPaymentDTO {
    private String id;
    private double amount;
    private String paymentMode;
    private FinancialAccountDTO accountCredited;
    private LocalDate creationDate;

    public MemberPaymentDTO() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public FinancialAccountDTO getAccountCredited() {
        return accountCredited;
    }

    public void setAccountCredited(FinancialAccountDTO accountCredited) {
        this.accountCredited = accountCredited;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }
}