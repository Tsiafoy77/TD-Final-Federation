package com.federation.agriculture.repository;

import com.federation.agriculture.config.DatabaseConfig;
import com.federation.agriculture.dto.ActivityMemberAttendanceDTO;
import com.federation.agriculture.dto.CreateActivityMemberAttendanceDTO;
import com.federation.agriculture.dto.MemberDescriptionDTO;
import com.federation.agriculture.model.Member;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class AttendanceRepository {

    private final DatabaseConfig dbConfig;  // ← Ce champ doit exister
    private final MemberRepository memberRepository;

    public AttendanceRepository(DatabaseConfig dbConfig, MemberRepository memberRepository) {
        this.dbConfig = dbConfig;
        this.memberRepository = memberRepository;
    }

    public List<ActivityMemberAttendanceDTO> saveAttendance(String activityId, List<CreateActivityMemberAttendanceDTO> attendances) {
        List<ActivityMemberAttendanceDTO> result = new ArrayList<>();
        String sql = "INSERT INTO attendance (id, activity_id, member_id, attendance_status, is_confirmed) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (activity_id, member_id) DO UPDATE SET " +
                "attendance_status = EXCLUDED.attendance_status, is_confirmed = TRUE " +
                "WHERE attendance.is_confirmed = FALSE";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (CreateActivityMemberAttendanceDTO dto : attendances) {
                String id = UUID.randomUUID().toString();
                pstmt.setString(1, id);
                pstmt.setString(2, activityId);
                pstmt.setString(3, dto.getMemberIdentifier());
                pstmt.setString(4, dto.getAttendanceStatus());
                pstmt.setBoolean(5, true);
                pstmt.executeUpdate();

                Member member = memberRepository.findById(dto.getMemberIdentifier());
                MemberDescriptionDTO memberDesc = null;
                if (member != null) {
                    memberDesc = new MemberDescriptionDTO(
                            member.getId(), member.getFirstName(), member.getLastName(),
                            member.getEmail(), member.getOccupation().name()
                    );
                }

                result.add(new ActivityMemberAttendanceDTO(id, memberDesc, dto.getAttendanceStatus()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<ActivityMemberAttendanceDTO> findByActivityId(String activityId) {
        String sql = "SELECT id, member_id, attendance_status FROM attendance WHERE activity_id = ?";
        List<ActivityMemberAttendanceDTO> attendances = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, activityId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String memberId = rs.getString("member_id");
                String status = rs.getString("attendance_status");
                Member member = memberRepository.findById(memberId);

                MemberDescriptionDTO memberDesc = null;
                if (member != null) {
                    memberDesc = new MemberDescriptionDTO(
                            member.getId(), member.getFirstName(), member.getLastName(),
                            member.getEmail(), member.getOccupation().name()
                    );
                }

                attendances.add(new ActivityMemberAttendanceDTO(rs.getString("id"), memberDesc, status));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendances;
    }

    public double getAssiduityPercentageForMember(String memberId, LocalDate from, LocalDate to) {
        String sql = "SELECT " +
                "  COUNT(*) as total, " +
                "  SUM(CASE WHEN attendance_status = 'ATTENDED' THEN 1 ELSE 0 END) as attended " +
                "FROM attendance " +
                "WHERE member_id = ? " +
                "  AND activity_date BETWEEN ? AND ? " +
                "  AND attendance_status IN ('ATTENDED', 'MISSING')";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memberId);
            pstmt.setDate(2, java.sql.Date.valueOf(from));
            pstmt.setDate(3, java.sql.Date.valueOf(to));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int total = rs.getInt("total");
                int attended = rs.getInt("attended");
                if (total == 0) {
                    return 0.0;
                }
                return attended * 100.0 / total;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public double getTotalPaidByMemberForFee(String memberId, String feeId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM member_payment " +
                "WHERE member_id = ? AND membership_fee_id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, memberId);
            pstmt.setString(2, feeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}