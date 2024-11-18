package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.repository;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.model.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    Seller findBySellerGstNo(String gstNo);

    Seller findByUserEmail(String email);

    Seller findBySellerCompanyName(String companyName);

    Boolean existsBySellerGstNo(String gst);

    Boolean existsBySellerCompanyName(String companyName);

    Boolean existsBySellerCompanyContact(String companyContact);

    @Query("SELECT b from Seller b")
    Page<Seller> findAllSellers(Pageable pageable);

    Page<Seller> findAllByUserEmailContaining(String email, Pageable pageable);

    @Query("SELECT s from Seller s where s.userIsActive=0")
    List<Seller> findAllSellersToBeActivated();

    @Query("SELECT s from Seller s where s.userIsActive=1")
    List<Seller> findAllSellersToBeDeActivated();
}
