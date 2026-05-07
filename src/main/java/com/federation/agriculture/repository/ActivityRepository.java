package com.federation.agriculture.repository;

import com.federation.agriculture.config.DatabaseConfig;
import com.federation.agriculture.dto.CollectivityActivityDTO;
import com.federation.agriculture.dto.CreateCollectivityActivityDTO;
import com.federation.agriculture.dto.MonthlyRecurrenceRuleDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class ActivityRepository {

    private final DatabaseConfig dbConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ActivityRepository(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public List<CollectivityActivityDTO> findByCollectivityId(String collectivityId) {
        String sql = "SELECT id, label, activity_type, member_occupation_concerned, " +
                "recurrence_week_ordinal, recurrence_day_of_week, executive_date " +
                "FROM activity WHERE collectivity_id = ? ORDER BY created_at DESC";
        List<CollectivityActivityDTO> activities = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, collectivityId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                CollectivityActivityDTO activity = new CollectivityActivityDTO();
                activity.setId(rs.getString("id"));
                activity.setLabel(rs.getString("label"));
                activity.setActivityType(rs.getString("activity_type"));

                String occupationsJson = rs.getString("member_occupation_concerned");
                if (occupationsJson != null && !occupationsJson.isEmpty()) {
                    List<String> occupations = objectMapper.readValue(occupationsJson, new TypeReference<List<String>>() {});
                    activity.setMemberOccupationConcerned(occupations);
                }

                Integer weekOrdinal = rs.getInt("recurrence_week_ordinal");
                if (!rs.wasNull() && weekOrdinal != null) {
                    MonthlyRecurrenceRuleDTO rule = new MonthlyRecurrenceRuleDTO();
                    rule.setWeekOrdinal(weekOrdinal);
                    rule.setDayOfWeek(rs.getString("recurrence_day_of_week"));
                    activity.setRecurrenceRule(rule);
                }

                Date executiveDate = rs.getDate("executive_date");
                if (executiveDate != null) {
                    activity.setExecutiveDate(executiveDate.toLocalDate());
                }

                activities.add(activity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activities;
    }

    public List<CollectivityActivityDTO> saveAll(String collectivityId, List<CreateCollectivityActivityDTO> activityDTOs) {
        List<CollectivityActivityDTO> result = new ArrayList<>();
        String sql = "INSERT INTO activity (id, collectivity_id, label, activity_type, " +
                "member_occupation_concerned, recurrence_week_ordinal, recurrence_day_of_week, executive_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (CreateCollectivityActivityDTO dto : activityDTOs) {
                String id = UUID.randomUUID().toString();

                pstmt.setString(1, id);
                pstmt.setString(2, collectivityId);
                pstmt.setString(3, dto.getLabel());
                pstmt.setString(4, dto.getActivityType());

                String occupationsJson = objectMapper.writeValueAsString(dto.getMemberOccupationConcerned());
                pstmt.setString(5, occupationsJson);

                if (dto.getRecurrenceRule() != null) {
                    pstmt.setInt(6, dto.getRecurrenceRule().getWeekOrdinal());
                    pstmt.setString(7, dto.getRecurrenceRule().getDayOfWeek());
                    pstmt.setNull(8, java.sql.Types.DATE);
                } else {
                    pstmt.setNull(6, java.sql.Types.INTEGER);
                    pstmt.setNull(7, java.sql.Types.VARCHAR);
                    pstmt.setDate(8, Date.valueOf(dto.getExecutiveDate()));
                }

                pstmt.executeUpdate();

                CollectivityActivityDTO activity = new CollectivityActivityDTO();
                activity.setId(id);
                activity.setLabel(dto.getLabel());
                activity.setActivityType(dto.getActivityType());
                activity.setMemberOccupationConcerned(dto.getMemberOccupationConcerned());
                activity.setRecurrenceRule(dto.getRecurrenceRule());
                activity.setExecutiveDate(dto.getExecutiveDate());
                result.add(activity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public CollectivityActivityDTO findById(String activityId) {
        String sql = "SELECT id, label, activity_type, member_occupation_concerned, " +
                "recurrence_week_ordinal, recurrence_day_of_week, executive_date " +
                "FROM activity WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, activityId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                CollectivityActivityDTO activity = new CollectivityActivityDTO();
                activity.setId(rs.getString("id"));
                activity.setLabel(rs.getString("label"));
                activity.setActivityType(rs.getString("activity_type"));

                String occupationsJson = rs.getString("member_occupation_concerned");
                if (occupationsJson != null && !occupationsJson.isEmpty()) {
                    List<String> occupations = objectMapper.readValue(occupationsJson, new TypeReference<List<String>>() {});
                    activity.setMemberOccupationConcerned(occupations);
                }

                Integer weekOrdinal = rs.getInt("recurrence_week_ordinal");
                if (!rs.wasNull() && weekOrdinal != null) {
                    MonthlyRecurrenceRuleDTO rule = new MonthlyRecurrenceRuleDTO();
                    rule.setWeekOrdinal(weekOrdinal);
                    rule.setDayOfWeek(rs.getString("recurrence_day_of_week"));
                    activity.setRecurrenceRule(rule);
                }

                Date executiveDate = rs.getDate("executive_date");
                if (executiveDate != null) {
                    activity.setExecutiveDate(executiveDate.toLocalDate());
                }

                return activity;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}