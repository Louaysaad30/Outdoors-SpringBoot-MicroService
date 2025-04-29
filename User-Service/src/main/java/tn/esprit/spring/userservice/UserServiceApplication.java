package tn.esprit.spring.userservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import tn.esprit.spring.userservice.Entity.Role;
import tn.esprit.spring.userservice.Enum.RoleType;
import tn.esprit.spring.userservice.Repository.RoleRepository;

@EnableDiscoveryClient
@SpringBootApplication
@EnableAsync
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}
	@Bean
	public CommandLineRunner runner(RoleRepository roleRepository) {
		return args -> {
			if (roleRepository.findByRoleType(RoleType.ADMIN).isEmpty()) {
				roleRepository.save(Role.builder().roleType(RoleType.ADMIN).build());
			}
			if (roleRepository.findByRoleType(RoleType.AGENCE).isEmpty()) {
				roleRepository.save(Role.builder().roleType(RoleType.AGENCE).build());
			}
			if (roleRepository.findByRoleType(RoleType.EVENT_MANAGER).isEmpty()) {
				roleRepository.save(Role.builder().roleType(RoleType.EVENT_MANAGER).build());
			}
			if (roleRepository.findByRoleType(RoleType.FORMATEUR).isEmpty()) {
				roleRepository.save(Role.builder().roleType(RoleType.FORMATEUR).build());
			}
			if (roleRepository.findByRoleType(RoleType.OWNER).isEmpty()) {
				roleRepository.save(Role.builder().roleType(RoleType.OWNER).build());
			}
			if (roleRepository.findByRoleType(RoleType.ADMIN).isEmpty()) {
				roleRepository.save(Role.builder().roleType(RoleType.ADMIN).build());
			}
			if (roleRepository.findByRoleType(RoleType.AGENCE).isEmpty()) {
				roleRepository.save(Role.builder().roleType(RoleType.AGENCE).build());
			}
			if (roleRepository.findByRoleType(RoleType.EVENT_MANAGER).isEmpty()) {
				roleRepository.save(Role.builder().roleType(RoleType.EVENT_MANAGER).build());
			}
			if (roleRepository.findByRoleType(RoleType.FORMATEUR).isEmpty()) {
				roleRepository.save(Role.builder().roleType(RoleType.FORMATEUR).build());
			}
			if (roleRepository.findByRoleType(RoleType.LIVREUR).isEmpty()) {
				roleRepository.save(Role.builder().roleType(RoleType.LIVREUR).build());
			}
			if (roleRepository.findByRoleType(RoleType.OWNER).isEmpty()) {
				roleRepository.save(Role.builder().roleType(RoleType.OWNER).build());
			}
		};
	}
}
