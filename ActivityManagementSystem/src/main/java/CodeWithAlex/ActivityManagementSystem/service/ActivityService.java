package CodeWithAlex.ActivityManagementSystem.service;

import CodeWithAlex.ActivityManagementSystem.entity.Activity;

import java.util.List;

public interface ActivityService {
    List<Activity> getAllActivities();

    Activity saveActivity(Activity activity);

    Activity getActivityById(Long id);

    Activity updateActivity(Activity activity);

    void deleteActivityById(Long id);
}
