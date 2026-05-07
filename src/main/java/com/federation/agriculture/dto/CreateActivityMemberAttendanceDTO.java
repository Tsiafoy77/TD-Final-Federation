package com.federation.agriculture.dto;

public class CreateActivityMemberAttendanceDTO {
    private String memberIdentifier;
    private String attendanceStatus;

    public CreateActivityMemberAttendanceDTO() {}

    public String getMemberIdentifier() { return memberIdentifier; }
    public void setMemberIdentifier(String memberIdentifier) { this.memberIdentifier = memberIdentifier; }

    public String getAttendanceStatus() { return attendanceStatus; }
    public void setAttendanceStatus(String attendanceStatus) { this.attendanceStatus = attendanceStatus; }
}