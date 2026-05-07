package com.federation.agriculture.service;

import com.federation.agriculture.dto.*;
import com.federation.agriculture.exception.BadRequestException;
import com.federation.agriculture.repository.ActivityRepository;
import com.federation.agriculture.repository.AttendanceRepository;
import com.federation.agriculture.repository.CollectivityRepository;
import java.util.List;

public class ActivityService {

    private final CollectivityRepository collectivityRepository;
    private final ActivityRepository activityRepository;
    private final AttendanceRepository attendanceRepository;

    public ActivityService(CollectivityRepository collectivityRepository,
                           ActivityRepository activityRepository,
                           AttendanceRepository attendanceRepository) {
        this.collectivityRepository = collectivityRepository;
        this.activityRepository = activityRepository;
        this.attendanceRepository = attendanceRepository;
    }

    public List<CollectivityActivityDTO> getActivities(String collectivityId) {
        if (collectivityRepository.findById(collectivityId) == null) {
            throw new RuntimeException("Collectivity not found: " + collectivityId);
        }
        return activityRepository.findByCollectivityId(collectivityId);
    }

    public List<CollectivityActivityDTO> createActivities(String collectivityId, List<CreateCollectivityActivityDTO> activities) {
        if (collectivityRepository.findById(collectivityId) == null) {
            throw new RuntimeException("Collectivity not found: " + collectivityId);
        }

        for (CreateCollectivityActivityDTO activity : activities) {
            if (activity.getLabel() == null || activity.getLabel().trim().isEmpty()) {
                throw new BadRequestException("Activity label is mandatory");
            }

            boolean hasRecurrenceRule = activity.getRecurrenceRule() != null;
            boolean hasExecutiveDate = activity.getExecutiveDate() != null;

            if (hasRecurrenceRule && hasExecutiveDate) {
                throw new BadRequestException("Both recurrence rule and executive date provided, not both");
            }
            if (!hasRecurrenceRule && !hasExecutiveDate) {
                throw new BadRequestException("Either recurrence rule or executive date must be provided");
            }

            if (activity.getActivityType() == null ||
                    (!activity.getActivityType().equals("MEETING") &&
                            !activity.getActivityType().equals("TRAINING") &&
                            !activity.getActivityType().equals("OTHER"))) {
                throw new BadRequestException("Invalid activity type: " + activity.getActivityType());
            }
        }

        return activityRepository.saveAll(collectivityId, activities);
    }

    public List<ActivityMemberAttendanceDTO> createAttendance(String collectivityId, String activityId,
                                                              List<CreateActivityMemberAttendanceDTO> attendances) {
        if (collectivityRepository.findById(collectivityId) == null) {
            throw new RuntimeException("Collectivity not found: " + collectivityId);
        }

        CollectivityActivityDTO activity = activityRepository.findById(activityId);
        if (activity == null) {
            throw new RuntimeException("Activity not found: " + activityId);
        }

        return attendanceRepository.saveAttendance(activityId, attendances);
    }

    public List<ActivityMemberAttendanceDTO> getAttendance(String collectivityId, String activityId) {
        if (collectivityRepository.findById(collectivityId) == null) {
            throw new RuntimeException("Collectivity not found: " + collectivityId);
        }

        CollectivityActivityDTO activity = activityRepository.findById(activityId);
        if (activity == null) {
            throw new RuntimeException("Activity not found: " + activityId);
        }

        return attendanceRepository.findByActivityId(activityId);
    }
}