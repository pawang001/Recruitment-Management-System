package com.recruitment.system.repository;

import com.recruitment.system.model.User;
import com.recruitment.system.model.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByUserType(UserType userType);
}