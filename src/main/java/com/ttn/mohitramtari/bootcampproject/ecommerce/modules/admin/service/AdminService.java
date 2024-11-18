package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.admin.service;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.admin.dto.CustomerActivationDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.admin.dto.ProductActivationDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.admin.dto.SellerActivationDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.dto.CustomerDetailsInAdminDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.dto.SellerDetailsInAdminDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AdminService {

    ResponseEntity<String> activateCustomer(Long id);

    ResponseEntity<String> deActivateCustomer(Long id);

    Page<CustomerDetailsInAdminDto> retrieveAllCustomers(Integer pageSize, Integer pageOffset,
                                                         String sortProperty, String sortDirection, String email);

    ResponseEntity<String> activateSeller(Long id);

    ResponseEntity<String> deActivateSeller(Long id);

    Page<SellerDetailsInAdminDto> retrieveAllSellers(Integer pageSize, Integer pageOffset,
                                                     String sortProperty, String sortDirection, String email);

    List sellersToBeActivated();

    List<ProductActivationDto> productsToBeActivated();

    List<SellerActivationDto> sellersToBeDeActivated();

    List<CustomerActivationDto> customersToBeActivated();

    List<CustomerActivationDto> customersToBeDeActivated();
}
