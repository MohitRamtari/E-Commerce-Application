package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.repository;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUserEmail(String email);

    Boolean existsByUserEmail(String userEmail);

    @Query(value = "SELECT * from users where user_id=:userId", nativeQuery = true)
    Optional<User> findById(Long userId);
}
