package CodeWithAlex.ActivityManagementSystem.service.Impl;

import CodeWithAlex.ActivityManagementSystem.entity.Activity;
import CodeWithAlex.ActivityManagementSystem.repository.ActivityRepository;
import CodeWithAlex.ActivityManagementSystem.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    public ActivityServiceImpl(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    @Override
    public Activity saveActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    @Override
    public Activity getActivityById(Long id) {
        return activityRepository.findById(id).get();
    }

    @Override
    public Activity updateActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    @Override
    public void deleteActivityById(Long id) {
        activityRepository.deleteById(id);
    }

}
