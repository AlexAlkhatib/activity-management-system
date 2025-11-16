package CodeWithAlex.ActivityManagementSystem.service;

import CodeWithAlex.ActivityManagementSystem.entity.User;
import CodeWithAlex.ActivityManagementSystem.web.dto.UserRegistrationDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User save(UserRegistrationDto registrationDto);

}
