package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.repository;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.model.Customer;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.model.ActivationToken;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface ActivationTokenRepository extends JpaRepository<ActivationToken, Long> {

    ActivationToken findByActivationToken(String activationToken);

    ActivationToken findByUser(User user);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM activation_token WHERE user_id = :userId", nativeQuery = true)
    void deleteByUserId(@Param("userId") Long userId);

    void deleteByUser(Customer customer);
}
