package com.federation.agriculture.dto;

import java.time.LocalDate;
import java.util.List;

public class CreateCollectivityActivityDTO {
    private String label;
    private String activityType;
    private List<String> memberOccupationConcerned;
    private MonthlyRecurrenceRuleDTO recurrenceRule;
    private LocalDate executiveDate;

    public CreateCollectivityActivityDTO() {}

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    public List<String> getMemberOccupationConcerned() { return memberOccupationConcerned; }
    public void setMemberOccupationConcerned(List<String> memberOccupationConcerned) { this.memberOccupationConcerned = memberOccupationConcerned; }

    public MonthlyRecurrenceRuleDTO getRecurrenceRule() { return recurrenceRule; }
    public void setRecurrenceRule(MonthlyRecurrenceRuleDTO recurrenceRule) { this.recurrenceRule = recurrenceRule; }

    public LocalDate getExecutiveDate() { return executiveDate; }
    public void setExecutiveDate(LocalDate executiveDate) { this.executiveDate = executiveDate; }
}