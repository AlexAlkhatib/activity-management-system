package com.fittrack.service;

import com.fittrack.domain.Role;
import com.fittrack.domain.User;
import com.fittrack.repository.RoleRepository;
import com.fittrack.repository.UserRepository;
import com.fittrack.web.dto.ProfileForm;
import com.fittrack.web.dto.RegistrationForm;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Comptes utilisateurs : inscription, profil, authentification.
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(RegistrationForm form) {
        String email = form.getEmail().trim().toLowerCase(Locale.FRENCH);
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalStateException("Un compte existe déjà avec cette adresse e-mail.");
        }
        User user = new User(form.getFirstName().trim(), form.getLastName().trim(), email,
                passwordEncoder.encode(form.getPassword()));
        Role role = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
        user.getRoles().add(role);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getName() == null || "anonymousUser".equals(authentication.getName())) {
            throw new AccessDeniedException("Authentication requise.");
        }
        return userRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable."));
    }

    @Transactional
    public User updateProfile(ProfileForm form) {
        User user = getCurrentUser();
        user.setFirstName(form.getFirstName().trim());
        user.setLastName(form.getLastName().trim());
        user.setBirthDate(form.getBirthDate());
        user.setGender(form.getGender());
        user.setHeightCm(form.getHeightCm());
        user.setTargetWeightKg(form.getTargetWeightKg());
        user.setWeeklyGoal(form.getWeeklyGoal());
        user.setActivityLevel(form.getActivityLevel());
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Identifiants invalides."));
        List<String> roles = new ArrayList<>();
        user.getRoles().forEach(role -> roles.add(role.getName()));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(roles.toArray(new String[0]))
                .build();
    }
}
