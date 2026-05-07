package com.federation.agriculture.dto;

public class CollectivityOverallStatisticsDTO {
    private CollectivityInformationDTO collectivityInformation;
    private int newMembersNumber;
    private double overallMemberCurrentDuePercentage;
    private double overallMemberAssiduityPercentage;   // ← NOUVEAU

    public CollectivityOverallStatisticsDTO() {}

    public CollectivityOverallStatisticsDTO(CollectivityInformationDTO collectivityInformation,
                                            int newMembersNumber,
                                            double overallMemberCurrentDuePercentage,
                                            double overallMemberAssiduityPercentage) {
        this.collectivityInformation = collectivityInformation;
        this.newMembersNumber = newMembersNumber;
        this.overallMemberCurrentDuePercentage = overallMemberCurrentDuePercentage;
        this.overallMemberAssiduityPercentage = overallMemberAssiduityPercentage;
    }

    // Getters et setters
    public CollectivityInformationDTO getCollectivityInformation() { return collectivityInformation; }
    public void setCollectivityInformation(CollectivityInformationDTO collectivityInformation) { this.collectivityInformation = collectivityInformation; }

    public int getNewMembersNumber() { return newMembersNumber; }
    public void setNewMembersNumber(int newMembersNumber) { this.newMembersNumber = newMembersNumber; }

    public double getOverallMemberCurrentDuePercentage() { return overallMemberCurrentDuePercentage; }
    public void setOverallMemberCurrentDuePercentage(double overallMemberCurrentDuePercentage) { this.overallMemberCurrentDuePercentage = overallMemberCurrentDuePercentage; }

    public double getOverallMemberAssiduityPercentage() { return overallMemberAssiduityPercentage; }
    public void setOverallMemberAssiduityPercentage(double overallMemberAssiduityPercentage) { this.overallMemberAssiduityPercentage = overallMemberAssiduityPercentage; }
}