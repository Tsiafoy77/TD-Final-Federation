package com.federation.agriculture.dto;

public class ActivityMemberAttendanceDTO {
    private String id;
    private MemberDescriptionDTO memberDescription;
    private String attendanceStatus;

    public ActivityMemberAttendanceDTO() {}

    public ActivityMemberAttendanceDTO(String id, MemberDescriptionDTO memberDescription, String attendanceStatus) {
        this.id = id;
        this.memberDescription = memberDescription;
        this.attendanceStatus = attendanceStatus;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public MemberDescriptionDTO getMemberDescription() { return memberDescription; }
    public void setMemberDescription(MemberDescriptionDTO memberDescription) { this.memberDescription = memberDescription; }

    public String getAttendanceStatus() { return attendanceStatus; }
    public void setAttendanceStatus(String attendanceStatus) { this.attendanceStatus = attendanceStatus; }
}