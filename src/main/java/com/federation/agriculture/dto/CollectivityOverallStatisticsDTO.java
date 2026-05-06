package com.federation.agriculture.dto;

public class CollectivityOverallStatisticsDTO {
    private CollectivityInformationDTO collectivityInformation;
    private int newMembersNumber;
    private double overallMemberCurrentDuePercentage;

    public CollectivityOverallStatisticsDTO() {}

    public CollectivityOverallStatisticsDTO(CollectivityInformationDTO collectivityInformation, int newMembersNumber, double overallMemberCurrentDuePercentage) {
        this.collectivityInformation = collectivityInformation;
        this.newMembersNumber = newMembersNumber;
        this.overallMemberCurrentDuePercentage = overallMemberCurrentDuePercentage;
    }

    public CollectivityInformationDTO getCollectivityInformation() { return collectivityInformation; }
    public void setCollectivityInformation(CollectivityInformationDTO collectivityInformation) { this.collectivityInformation = collectivityInformation; }

    public int getNewMembersNumber() { return newMembersNumber; }
    public void setNewMembersNumber(int newMembersNumber) { this.newMembersNumber = newMembersNumber; }

    public double getOverallMemberCurrentDuePercentage() { return overallMemberCurrentDuePercentage; }
    public void setOverallMemberCurrentDuePercentage(double overallMemberCurrentDuePercentage) { this.overallMemberCurrentDuePercentage = overallMemberCurrentDuePercentage; }
}