package CodeWithAlex.ActivityManagementSystem.controller;

import CodeWithAlex.ActivityManagementSystem.entity.Activity;
import CodeWithAlex.ActivityManagementSystem.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        super();
        this.activityService = activityService;
    }


     @GetMapping("/activities")
     public String listActivities(Model model){
     model.addAttribute("activities", activityService.getAllActivities());
     return "activities";
     }

     /**
    @GetMapping("/activities")
    public String listSelectedActivities(Model model){
        List<Activity> selectedActivities = (List<Activity>) model.getAttribute("selectedActivities");
        model.addAttribute("selectedActivities", selectedActivities);
        return "activities";
    }
    **/

    @PostMapping("/activities/new")
    public String listActivities(@RequestParam(value = "selectedActivities", required = false) List<Long> selectedActivityIds, Model model){
        List<Activity> selectedActivities = new ArrayList<>();
        if (selectedActivityIds != null) {
            for (Long id : selectedActivityIds) {
                selectedActivities.add(activityService.getActivityById(id));
            }
        }
        model.addAttribute("selectedActivities", selectedActivities);
        return "activities";
    }

    @GetMapping("/activities/new")
    public String createActivityForm(Model model, @RequestParam(name = "place", required = false) String place, @RequestParam(name = "name", required = false) String name){
        List<Activity> activities = activityService.getAllActivities();
        if (place != null && !place.isEmpty()) {
            activities = activities.stream()
                    .filter(a -> a.getPlace().toLowerCase().contains(place.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (name != null && !name.isEmpty()) {
            activities = activities.stream()
                    .filter(a -> a.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }
        model.addAttribute("activities", activities);
        model.addAttribute("selectedActivities", new ArrayList<Activity>());
        return "create_activity";
    }

    @PostMapping("/activities")
    public String saveActivity(@ModelAttribute("activity") Activity activity){
        activityService.saveActivity(activity);
        return "redirect:/activities";
    }

    @GetMapping("/activities/edit/{id}")
    public String editActivityForm(@PathVariable Long id, Model model){
        model.addAttribute("activity", activityService.getActivityById(id));
        return "edit_activity";
    }

    @PostMapping("/activities/{id}")
    public String updateActivity(@PathVariable Long id, @ModelAttribute("activity") Activity activity, Model model){

        Activity existingActivity = activityService.getActivityById(id);
        existingActivity.setNote(activity.getNote());
        activityService.updateActivity(existingActivity);
        return "redirect:/activities";
    }

    @GetMapping("/activities/{id}")
    public String deleteActivity(@PathVariable Long id){
        activityService.deleteActivityById(id);
        return "redirect:/activities";
    }
}
