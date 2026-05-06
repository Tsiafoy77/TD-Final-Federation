package com.federation.agriculture.dto;

public class CollectivityLocalStatisticsDTO {
    private MemberDescriptionDTO memberDescription;
    private double earnedAmount;
    private double unpaidAmount;

    public CollectivityLocalStatisticsDTO() {}

    public CollectivityLocalStatisticsDTO(MemberDescriptionDTO memberDescription, double earnedAmount, double unpaidAmount) {
        this.memberDescription = memberDescription;
        this.earnedAmount = earnedAmount;
        this.unpaidAmount = unpaidAmount;
    }

    public MemberDescriptionDTO getMemberDescription() { return memberDescription; }
    public void setMemberDescription(MemberDescriptionDTO memberDescription) { this.memberDescription = memberDescription; }

    public double getEarnedAmount() { return earnedAmount; }
    public void setEarnedAmount(double earnedAmount) { this.earnedAmount = earnedAmount; }

    public double getUnpaidAmount() { return unpaidAmount; }
    public void setUnpaidAmount(double unpaidAmount) { this.unpaidAmount = unpaidAmount; }
}