package com.app.customer;

import com.app.customer.domain.RoleEntity;
import com.app.customer.domain.Status;
import com.app.customer.domain.UserEntity;
import com.app.customer.repository.RoleRepository;
import com.app.customer.repository.UserRepository;
import com.app.customer.security.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
public class CustomerApplication implements CommandLineRunner {

	@Autowired
	private UserRepository repository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;


	public static void main(String[] args) {
		SpringApplication.run(CustomerApplication.class, args);
	}

	@Override
	@Transactional
	public void run(String... args) throws Exception {

		UserEntity user = new UserEntity();
		user.setStatus(Status.ACTIVE.name());
		user.setPassword(passwordEncoder.encode("test"));
		user.setName("admin");
		user.setUsername("admin");
		RoleEntity role = new RoleEntity();
		role.setName(Roles.ADMIN_ROLE.name());
		RoleEntity savedRole = roleRepository.save(role);
		user.getRoles().add(savedRole);
		repository.save(user);

	}
}
