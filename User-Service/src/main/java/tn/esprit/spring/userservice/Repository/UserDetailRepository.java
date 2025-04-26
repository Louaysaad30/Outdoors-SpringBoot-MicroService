package tn.esprit.spring.userservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.userservice.Entity.UserDetail;

import java.util.Optional;

public interface UserDetailRepository extends JpaRepository<UserDetail, Long> {
        Optional<UserDetail> findByUserId(Long userId);

}
