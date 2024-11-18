package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.admin.controller;

import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.EmailSenderService;
import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.GlobalVariables;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.admin.dto.CustomerActivationDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.admin.dto.ProductActivationDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.admin.dto.SellerActivationDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.admin.service.AdminService;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.dto.CustomerDetailsInAdminDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.dto.CustomerModel;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.mapper.CustomerModelAssembler;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.dto.SellerDetailsInAdminDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.dto.SellerModel;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.mapper.SellerModelAssembler;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
public class AdminController {

    @Autowired
    AdminService adminService;

    @Autowired
    private SellerModelAssembler sellerModelAssembler;

    @Autowired
    private CustomerModelAssembler customerModelAssembler;

    @Autowired
    private PagedResourcesAssembler<SellerDetailsInAdminDto> sellerPagedResourcesAssembler;

    @Autowired
    private PagedResourcesAssembler<CustomerDetailsInAdminDto> customerPagedResourcesAssembler;

    @Autowired
    private EmailSenderService emailSenderService;

    @Operation(summary = "List of all customers", description = "Admin can view List of all the customers which have registered in the form of page.", method = "GET")
    @GetMapping("/admin/all-customers")
    public PagedModel<CustomerModel> listAllCustomers(
            @RequestParam(value = "page-size", defaultValue = GlobalVariables.PAGE_SIZE_DEFAULT) Integer pageSize,
            @RequestParam(value = "page-offset", defaultValue = GlobalVariables.PAGE_OFFSET_DEFAULT) Integer pageOffset,
            @RequestParam(value = "sort-property", defaultValue = GlobalVariables.SORT_PROPERTY_DEFAULT) String sortProperty,
            @RequestParam(value = "sort-direction", defaultValue = GlobalVariables.SORT_DIRECTION_DEFAULT) String sortDirection,
            @Param(value = "email") String email) {
        Page<CustomerDetailsInAdminDto> customerPage = adminService.retrieveAllCustomers(pageSize,
                pageOffset, sortProperty, sortDirection, email);
        return customerPagedResourcesAssembler.toModel(customerPage, customerModelAssembler);
    }

    @Operation(summary = "List of all Sellers", description = "Admin can list down all the sellers which are registered.", method = "GET")
    @GetMapping("/admin/all-sellers")
    public PagedModel<SellerModel> listAllSellers(
            @RequestParam(value = "page-size", defaultValue = GlobalVariables.PAGE_SIZE_DEFAULT) Integer pageSize,
            @RequestParam(value = "page-offset", defaultValue = GlobalVariables.PAGE_OFFSET_DEFAULT) Integer pageOffset,
            @RequestParam(value = "sort-property", defaultValue = GlobalVariables.SORT_PROPERTY_DEFAULT) String sortProperty,
            @RequestParam(value = "sort-direction", defaultValue = GlobalVariables.SORT_DIRECTION_DEFAULT) String sortDirection,
            @Param(value = "email") String email) {
        Page<SellerDetailsInAdminDto> sellerPage = adminService.retrieveAllSellers(pageSize, pageOffset,
                sortProperty, sortDirection, email);
        return sellerPagedResourcesAssembler.toModel(sellerPage, sellerModelAssembler);
    }

    @Operation(summary = "List of sellers to be activated", description = "Admin can list down all the sellers account and their details that need their account to be activated. And it also contains a url which can be used to activate that particular seller.", method = "GET")
    @GetMapping("/admin/sellers/activate")
    List sellersToBeActivated() {
        List<SellerActivationDto> sellerList = adminService.sellersToBeActivated();

        for (SellerActivationDto seller : sellerList) {
            Link link = linkTo(AdminController.class).slash("admin").slash("sellers").slash("activate")
                    .slash(seller.getUserId()).withRel("Activate Seller");
            seller.add(link);
        }
        return sellerList;
    }

    @Operation(summary = "List of sellers to be deactivated", description = "Admin can list down all those sellers whose accounts can be deactivated.And it also contains a url which will deactivate the account of that particular seller", method = "GET")
    @GetMapping("/admin/sellers/deactivate")
    List sellersToBeDeActivated() {
        List<SellerActivationDto> sellerList = adminService.sellersToBeDeActivated();

        for (SellerActivationDto seller : sellerList) {
            Link link = linkTo(AdminController.class).slash("admin").slash("sellers").slash("deactivate")
                    .slash(seller.getUserId()).withRel("DeActivate Seller");
            seller.add(link);
        }
        return sellerList;
    }

    @Operation(summary = "List of customers to be activated", description = "Admin can list down all those customers whose accounts can be activated and it also contains a url which can be used to activate that particular user", method = "GET")
    @GetMapping("/admin/customers/activate")
    List customersToBeActivated() {
        List<CustomerActivationDto> customerList = adminService.customersToBeActivated();

        for (CustomerActivationDto seller : customerList) {
            Link link = linkTo(AdminController.class).slash("admin").slash("customers").slash("activate")
                    .slash(seller.getUserId()).withRel("Activate Customer");
            seller.add(link);
        }
        return customerList;
    }

    @Operation(summary = "List of customers to be deactivated", description = "Admin can list down all those customers whose accounts can be deactivated and also contains a url which can be used to activate that particular account", method = "GET")
    @GetMapping("/admin/customers/deactivate")
    ResponseEntity customersToBeDeActivated() {
        List<CustomerActivationDto> customerList = adminService.customersToBeDeActivated();

        for (CustomerActivationDto customer : customerList) {
            Link link = linkTo(AdminController.class).slash("admin").slash("customers")
                    .slash("deactivate").slash(customer.getUserId()).withRel("DeActivate Customer");
            customer.add(link);
        }
        return new ResponseEntity(customerList, null, HttpStatus.OK);
    }

    @Operation(summary = "Activate Customer", description = "Admin can activate the customer whose id is passed in the url and a mail is sent to the customer stating that his account has been activated", method = "PATCh")
    @PatchMapping("/admin/customers/activate/{id}")
    ResponseEntity customerActivation(@PathVariable Long id) {
        return adminService.activateCustomer(id);
    }

    @Operation(summary = "Deactivate Customer", description = "Admin can deactivate the customer whose id is passed in the url and a mail is sent to the customer stating that his account has been deactivated", method = "PATCH")
    @PatchMapping("/admin/customers/deactivate/{id}")
    ResponseEntity customerDeActivation(@PathVariable Long id) {
        return adminService.deActivateCustomer(id);
    }

    @Operation(summary = "Activate Seller", description = "Admin can activate the seller whose id is passed in the url and a mail is sent to the seller stating that his account has been activated", method = "PATCH")
    @PatchMapping("/admin/sellers/activate/{id}")
    ResponseEntity sellerActivation(@PathVariable Long id) {
        return adminService.activateSeller(id);
    }

    @Operation(summary = "Deactivate Seller", description = "Admin can deactivate the seller whose id is passed in the url and a mail is sent to the seller stating that his account has been deactivated", method = "PATCH")
    @PatchMapping("/admin/sellers/deactivate/{id}")
    ResponseEntity sellerDeActivation(@PathVariable Long id) {
        return adminService.deActivateSeller(id);
    }

    @Operation(summary = "Activate Products list", description = "Admin can list down all those products that need to be activated. And it contains a url which activates that particular product")
    @GetMapping("/admin/products/activate")
    List productsToBeActivated() {
        List<ProductActivationDto> productList = adminService.productsToBeActivated();

        for (ProductActivationDto product : productList) {
            Link link = linkTo(AdminController.class).slash("admin").slash("product").slash("activate")
                    .slash(product.getId()).withRel("Activate Product");
            product.add(link);
        }
        return productList;
    }
}
