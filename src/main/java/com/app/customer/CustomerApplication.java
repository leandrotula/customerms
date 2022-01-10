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
		user.setName("leandro");
		user.setUsername("lea");
		RoleEntity role = new RoleEntity();
		role.setName(Roles.ADMIN_ROLE.name());
		RoleEntity savedRole = roleRepository.save(role);
		user.getRoles().add(savedRole);
		repository.save(user);

	/*	UserEntity user2 = new UserEntity();
		user2.setStatus(Status.ACTIVE.name());
		user2.setPassword(passwordEncoder.encode("test"));
		user2.setName("juan");
		user2.setUsername("juanelo");
		RoleEntity role2 = new RoleEntity();
		role2.setName(Roles.USER_ROLE.name());
		RoleEntity savedRole2 = roleRepository.save(role2);
		user2.getRoles().add(savedRole2);
		repository.save(user2);

		UserEntity user3 = new UserEntity();
		user3.setStatus(Status.ACTIVE.name());
		user3.setPassword(passwordEncoder.encode("test"));
		user3.setName("guillermo");
		user3.setUsername("guille");
		RoleEntity role3 = new RoleEntity();
		role3.setName(Roles.USER_ROLE.name());
		RoleEntity savedRole3 = roleRepository.save(role3);
		user3.getRoles().add(savedRole3);
		repository.save(user3);*/

	}
}
