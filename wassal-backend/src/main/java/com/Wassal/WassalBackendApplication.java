package com.Wassal;

import com.Wassal.exception.ResourceNotFoundException;
import com.Wassal.model.ERole;
import com.Wassal.model.Role;
import com.Wassal.model.User;
import com.Wassal.repository.RoleRepository;
import com.Wassal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class WassalBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(WassalBackendApplication.class, args);
	}

	//Temporary data seeding for roles schema -> replace with flyway later
	@Bean
	@Order(1)
	public CommandLineRunner seedRoles(RoleRepository roleRepository) {
		return args -> {
			for (ERole roleName : ERole.values()) {
				if (roleRepository.findByRole(roleName).isEmpty()) {
					var role = Role.builder()
							.role(roleName)
							.build();
					roleRepository.save(role);
				}
			}
		};
	}

	//Default system admin
	@Value("${app.admin.email}")
	String adminEmail;
	@Value("${app.admin.password}")
	String adminPassword;
	@Bean
	@Order(2)
	public CommandLineRunner defaultAdmin(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder) {
		return args -> {
			if (userRepository.existsByEmail(adminEmail)) return;
			var adminRole = roleRepository.findByRole(ERole.ROLE_ADMIN)
					.orElseThrow(() -> new ResourceNotFoundException("ROLE_ADMIN not found."));
			User systemAdmin = User.builder()
					.firstName("Wassal")
					.email(adminEmail)
					.password(encoder.encode(adminPassword))
					.roles(Set.of(adminRole))
					.build();
				userRepository.save(systemAdmin);
		};
	}

}
