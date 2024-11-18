package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.address.repository;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.address.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    Address findByAddressId(Long id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM address where address_id=:id", nativeQuery = true)
    void deleteByAddressId(@Param("id") Long id);
}
