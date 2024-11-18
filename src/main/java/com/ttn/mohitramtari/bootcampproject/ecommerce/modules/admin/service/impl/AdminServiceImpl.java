package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.admin.service.impl;

import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.EmailSenderService;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.admin.dto.CustomerActivationDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.admin.dto.ProductActivationDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.admin.dto.SellerActivationDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.admin.service.AdminService;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.dto.CustomerDetailsInAdminDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.model.Customer;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.repository.CustomerRepository;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.model.Product;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.repository.ProductRepository;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.dto.SellerDetailsInAdminDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.model.Seller;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.repository.SellerRepository;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.repository.ActivationTokenRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    SellerRepository sellerRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    EmailSenderService emailSenderService;

    @Autowired
    ActivationTokenRepository activationTokenRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MessageSource messageSource;

    @Autowired
    ProductRepository productRepository;

    /**
     * A method to activate the customer. Takes input of the user id and there can be three results.
     * First result is customer gets activated, second result customer is already activated and third
     * if customer doesn't exist.
     *
     * @param
     */
    public ResponseEntity<String> activateCustomer(Long id) {
        Customer customer = customerRepository.findById(id).get();
        if (customer != null) {
            if (!customer.getUserIsActive()) {
                activationTokenRepository.deleteByUserId(id);
                customer.setUserIsActive(true);
                customerRepository.save(customer);
                emailSenderService.sendEmail(
                        emailSenderService.getCustomerAccountActivatedMail(customer.getUserEmail()));
                return new ResponseEntity<>(
                        messageSource.getMessage("customer.activate", null, LocaleContextHolder.getLocale()),
                        null, HttpStatus.OK);
            }
            return new ResponseEntity<>(messageSource.getMessage("customer.already.active", null,
                    LocaleContextHolder.getLocale()), null, HttpStatus.OK);
        } else {
            throw new UsernameNotFoundException(
                    messageSource.getMessage("user.not.found", null, LocaleContextHolder.getLocale()));
        }
    }

    /**
     * A method to deactivate the customer. Takes input of the user id and there can be three results.
     * First result is customer gets deactivated, second customer is already deactivated and third if
     * customer doesn't exist.
     *
     * @param id
     * @return
     */
    public ResponseEntity<String> deActivateCustomer(Long id) {
        Customer customer = customerRepository.findById(id).get();
        if (customer != null) {
            if (customer.getUserIsActive()) {
                customer.setUserIsActive(false);
                customerRepository.save(customer);
                emailSenderService.sendEmail(
                        emailSenderService.getCustomerAccountDeActivatedMail(customer.getUserEmail()));
                return new ResponseEntity<>(
                        messageSource.getMessage("customer.deactivate", null, LocaleContextHolder.getLocale()),
                        null, HttpStatus.OK);
            }
            return new ResponseEntity<>(messageSource.getMessage("customer.already.deactive", null,
                    LocaleContextHolder.getLocale()), null, HttpStatus.OK);
        } else {
            throw new UsernameNotFoundException("{user.not.found}");
        }
    }

    /**
     * A method to activate the seller. Takes input of the user id and there can be three results.
     * First result is seller gets activated, second result is seller is already active and third if
     * seller doesn't exist.
     *
     * @param id
     */
    public ResponseEntity<String> activateSeller(Long id) {
        Seller seller = sellerRepository.findById(id).get();
        if (seller != null) {
            if (!seller.getUserIsActive()) {
                seller.setUserIsActive(true);
                sellerRepository.save(seller);
                emailSenderService.sendEmail(
                        emailSenderService.getSellerAccountActivatedMail(seller.getUserEmail()));
                return new ResponseEntity<>(
                        messageSource.getMessage("seller.activate", null, LocaleContextHolder.getLocale()),
                        null, HttpStatus.OK);
            }
            return new ResponseEntity<>(
                    messageSource.getMessage("seller.already.active", null, LocaleContextHolder.getLocale()),
                    null, HttpStatus.OK);
        } else {
            throw new UsernameNotFoundException(
                    messageSource.getMessage("user.not.found", null, LocaleContextHolder.getLocale()));
        }
    }

    /**
     * A method to deactivate the seller. Takes input of the user id and there can be three results.
     * First result is seller gets deactivated, second result is seller is already deactivated and
     * third if seller doesn't exist.
     *
     * @param id
     */
    public ResponseEntity<String> deActivateSeller(Long id) {
        Seller seller = sellerRepository.findById(id).get();
        if (seller != null) {
            if (seller.getUserIsActive()) {
                seller.setUserIsActive(false);
                sellerRepository.save(seller);
                emailSenderService.sendEmail(
                        emailSenderService.getSellerAccountDeActivatedMail(seller.getUserEmail()));
                return new ResponseEntity<>(
                        messageSource.getMessage("seller.deactivate", null, LocaleContextHolder.getLocale()),
                        null, HttpStatus.OK);
            }
            return new ResponseEntity<>(messageSource.getMessage("seller.already.deactive", null,
                    LocaleContextHolder.getLocale()), null, HttpStatus.OK);
        } else {
            throw new UsernameNotFoundException(
                    messageSource.getMessage("user.not.found", null, LocaleContextHolder.getLocale()));
        }
    }

    /**
     * Method to retrieve all the customers stored in the database in the form of pagination. Here the
     * page's properties are being taken as input.
     *
     * @param pageSize
     * @param pageOffset
     * @param sortProperty
     * @param sortDirection
     * @param email
     * @return
     */
    @Override
    public Page<CustomerDetailsInAdminDto> retrieveAllCustomers(Integer pageSize, Integer pageOffset,
                                                                String sortProperty, String sortDirection, String email) {

        Page customerPage;
        Sort.Direction sortingDirection =
                sortDirection.equalsIgnoreCase("ASC") ? Direction.ASC : Direction.DESC;
        Pageable pageable = PageRequest.of(pageOffset, pageSize, sortingDirection, sortProperty);

        if (email != null) {
            customerPage = customerRepository.findAllByUserEmailContaining(email, pageable)
                    .map(customer -> modelMapper.map(customer, CustomerDetailsInAdminDto.class));
        } else {
            customerPage = customerRepository.findAllCustomers(pageable)
                    .map(customer -> modelMapper.map(customer, CustomerDetailsInAdminDto.class));
        }

        return customerPage;
    }

    /**
     * Method to retrieve all the sellers stored in the database in the form of pagination. Page's
     * properties are being taken as input
     *
     * @param pageSize
     * @param pageOffset
     * @param sortProperty
     * @param sortDirection
     * @param email
     * @return List of all those sellers in the form of page
     */
    public Page<SellerDetailsInAdminDto> retrieveAllSellers(Integer pageSize, Integer pageOffset,
                                                            String sortProperty, String sortDirection, String email) {

        Page sellerPage;
        Sort.Direction sortingDirection =
                sortDirection.equalsIgnoreCase("ASC") ? Direction.ASC : Direction.DESC;
        Pageable pageable = PageRequest.of(pageOffset, pageSize, sortingDirection, sortProperty);

        if (email == null) {
            sellerPage = sellerRepository.findAllSellers(pageable)
                    .map(seller -> modelMapper.map(seller, SellerDetailsInAdminDto.class));
        } else {
            sellerPage = sellerRepository.findAllByUserEmailContaining(email, pageable)
                    .map(seller -> modelMapper.map(seller, SellerDetailsInAdminDto.class));
        }

        return sellerPage;
    }

    /**
     * Method to retrieve all those sellers that need to be activated by the admin.
     *
     * @return List of all those sellers whose account is deactivated
     */
    @Override
    public List<SellerActivationDto> sellersToBeActivated() {
        List<Seller> sellerList = sellerRepository.findAllSellersToBeActivated();
        List<SellerActivationDto> sellerActivationDtoList = new ArrayList<>();
        for (Seller seller : sellerList) {
            sellerActivationDtoList.add(modelMapper.map(seller, SellerActivationDto.class));
        }
        return sellerActivationDtoList;
    }

    @Override
    public List<ProductActivationDto> productsToBeActivated() {
        List<Product> productList = productRepository.findAllProductsToBeActivated();
        List<ProductActivationDto> productActivationDtoList = new ArrayList<>();
        for (Product product : productList) {
            productActivationDtoList.add(modelMapper.map(product, ProductActivationDto.class));
        }
        return productActivationDtoList;
    }

    /**
     * Method to retrieve List of all those sellers that can be deactivated by the admin.
     *
     * @return List of all the Active Sellers
     */
    @Override
    public List<SellerActivationDto> sellersToBeDeActivated() {
        List<Seller> sellerList = sellerRepository.findAllSellersToBeDeActivated();
        List<SellerActivationDto> sellerActivationDtoList = new ArrayList<>();
        for (Seller seller : sellerList) {
            sellerActivationDtoList.add(modelMapper.map(seller, SellerActivationDto.class));
        }
        return sellerActivationDtoList;
    }

    /**
     * Method to return the list of all those customers that can be activated by the admin.
     *
     * @return List of all those customers which are not active
     */
    @Override
    public List<CustomerActivationDto> customersToBeActivated() {
        List<Customer> customerList = customerRepository.findAllCustomersToBeActivated();
        List<CustomerActivationDto> customerActivationDtoList = new ArrayList<>();
        for (Customer customer : customerList) {
            customerActivationDtoList.add(modelMapper.map(customer, CustomerActivationDto.class));
        }
        return customerActivationDtoList;
    }

    /**
     * Method to retrieve list of all those customers that can be deactivated by the admin
     *
     * @return List of all those customers which are active
     */
    @Override
    public List<CustomerActivationDto> customersToBeDeActivated() {
        List<Customer> customerList = customerRepository.findAllCustomersToBeDeActivated();
        List<CustomerActivationDto> customerActivationDtoList = new ArrayList<>();
        for (Customer customer : customerList) {
            customerActivationDtoList.add(modelMapper.map(customer, CustomerActivationDto.class));
        }
        return customerActivationDtoList;
    }
}
