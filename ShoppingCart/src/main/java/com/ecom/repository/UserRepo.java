package com.ecom.repository;

import com.ecom.model.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<MyUser, Integer> {
    MyUser findByEmail(String email);

    List<MyUser> findByRole(String role);

    MyUser findByResetToken(String token);

    Boolean existsByEmail(String email);
}
