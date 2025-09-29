package cyfr.ae.estimate.config;

import cyfr.ae.estimate.model.Role;
import cyfr.ae.estimate.model.User;
import cyfr.ae.estimate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("1"))
                    .email("admin@example.com")
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
        }
    }
}