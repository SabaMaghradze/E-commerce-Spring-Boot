package com.ecom.repository;

import com.ecom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
    User findByEmail(String email);

    List<User> findByRole(String role);

    User findByResetToken(String token);

    Boolean existsByEmail(String email);
}
