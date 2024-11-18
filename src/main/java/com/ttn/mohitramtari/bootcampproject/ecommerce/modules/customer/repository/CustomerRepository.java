package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.repository;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByUserEmail(String email);

    @Query("SELECT b from Customer b")
    Page<Customer> findAllCustomers(Pageable pageable);

    Page<Customer> findAllByUserEmailContaining(String email, Pageable pageable);

    Boolean existsByCustomerContact(String customerContact);

    @Query("SELECT c from Customer c where c.userIsActive=0")
    List<Customer> findAllCustomersToBeActivated();

    @Query("SELECT c from Customer c where c.userIsActive=1")
    List<Customer> findAllCustomersToBeDeActivated();
}
