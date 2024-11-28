package com.ranjit.ps.repository;

import com.ranjit.ps.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    // Custom query methods can be added here if needed
    void deleteByEmail(String email);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

}
