package CodeWithAlex.ActivityManagementSystem;

import CodeWithAlex.ActivityManagementSystem.entity.Activity;
import CodeWithAlex.ActivityManagementSystem.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class ActivityManagementSystemApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ActivityManagementSystemApplication.class, args);
	}

	@Autowired
	private ActivityRepository activityRepository;

	@Override
	public void run(String... args) throws Exception {
		/**
		Activity activity1 = new Activity("Football", "Tours", "Palais des Sports", "U14-U15", 20, 5);
		activityRepository.save(activity1);
		Activity activity2 = new Activity("Basketball", "Tours", "Palais des Sports", "U15-U16", 25, 5);
		activityRepository.save(activity2);
		Activity activity3 = new Activity("Tennis", "Tours", "Palais des Sports", "U16-U17", 50, 5);
		activityRepository.save(activity3);

		Activity activity4 = new Activity("Volleyball", "Orléans", "ASJ Volley-Ball", "U16-U17", 70, 5);
		activityRepository.save(activity3);
		Activity activity5 = new Activity("Test", "Test", "Test", "U16-U17", 90, 5);
		activityRepository.save(activity3);
		 **/
	}
}
