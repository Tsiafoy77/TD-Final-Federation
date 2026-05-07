package com.federation.agriculture.controller;

import com.federation.agriculture.dto.*;
import com.federation.agriculture.service.ActivityService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/collectivities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/{id}/activities")
    public List<CollectivityActivityDTO> getActivities(@PathVariable String id) {
        return activityService.getActivities(id);
    }

    @PostMapping("/{id}/activities")
    @ResponseStatus(HttpStatus.CREATED)
    public List<CollectivityActivityDTO> createActivities(@PathVariable String id,
                                                          @RequestBody List<CreateCollectivityActivityDTO> activities) {
        return activityService.createActivities(id, activities);
    }

    @PostMapping("/{id}/activities/{activityId}/attendance")
    @ResponseStatus(HttpStatus.CREATED)
    public List<ActivityMemberAttendanceDTO> createAttendance(@PathVariable String id,
                                                              @PathVariable String activityId,
                                                              @RequestBody List<CreateActivityMemberAttendanceDTO> attendances) {
        return activityService.createAttendance(id, activityId, attendances);
    }

    @GetMapping("/{id}/activities/{activityId}/attendance")
    public List<ActivityMemberAttendanceDTO> getAttendance(@PathVariable String id,
                                                           @PathVariable String activityId) {
        return activityService.getAttendance(id, activityId);
    }
}