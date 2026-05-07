package com.federation.agriculture.dto;

import java.time.LocalDate;

public class MembershipFeeDTO {
    private String id;
    private LocalDate eligibleFrom;
    private String frequency;
    private double amount;
    private String label;
    private String status;

    public MembershipFeeDTO() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDate getEligibleFrom() { return eligibleFrom; }
    public void setEligibleFrom(LocalDate eligibleFrom) { this.eligibleFrom = eligibleFrom; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}