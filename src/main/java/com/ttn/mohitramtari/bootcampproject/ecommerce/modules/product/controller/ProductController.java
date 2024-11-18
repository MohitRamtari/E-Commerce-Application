package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.shaded.json.parser.ParseException;
import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.GlobalVariables;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.dto.ProductVariationUpdateDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.dto.*;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.dto.admin.ProductDetailsForAdminDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.Principal;

@RestController
public class ProductController {

    @Autowired
    ProductService productService;

    @Autowired
    private PagedResourcesAssembler<ProductViewDto> productViewDtoPagedResourcesAssembler;

    @Autowired
    private PagedResourcesAssembler<ProductDetailsForAdminDto> productDetailsForAdminDtoPagedResourcesAssembler;

    @Autowired
    private PagedResourcesAssembler<ProductDetailsForCustomerDto> productDetailsForCustomerDtoPagedResourcesAssembler;

    @Autowired
    private PagedResourcesAssembler<VariationDto> productVariationResponseDtoPagedResourcesAssembler;

    @Operation(summary = "Add Product", description = "Api for the seller to add a product. Product will be inactive by default. When seller creates the product, a mail will be sent to the admin with the details of the product and a url to activate that product.", method = "POST")
    @PostMapping("/seller/add-product")
    public ResponseEntity createProduct(Principal principal,
                                        @Valid @RequestBody ProductDto productDto) {
        return productService.addNewProduct(principal.getName(), productDto);
    }

    @Operation(summary = "Create Product Variation", description = "This api can be used to create the variation of the product. Here primary image and secondary images will also be added for the product variation along with its price and quantity. Variation specification must be unique.", method = "POST")
    @RequestMapping(path = "/seller/product/add-variation", method = RequestMethod.POST,
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity createProductVariation(Principal principal, @Valid ProductVariationDto productVariationDto) throws IOException {
        return productService.addNewProductVariation(principal.getName(), productVariationDto);
    }

    @Operation(summary = "View product", description = "Api for the seller to view the product that he has created along with the details of the category that the product belongs to.Product id should be passed here", method = "GET")
    @GetMapping("/seller/product/{id}")
    public ResponseEntity getProduct(Principal principal, @PathVariable("id") Long id) {
        return productService.getProduct(principal.getName(), id);
    }

    @Operation(summary = "View product variation", description = "")
    @GetMapping("/seller/product/variation/{id}")
    public ResponseEntity getProductVariation(Principal principal, @PathVariable("id") Long id)
            throws ParseException, IOException {
        return productService.getProductVariation(principal.getName(), id);
    }

    @Operation(summary = "View all products", description = "Here seller can view all the products that he has created in the form of a page. Page's specifications need to be passed while accessing this api. Product id need to be passed in the url", method = "GET")
    @GetMapping("/seller/products")
    public PagedModel<EntityModel<ProductViewDto>> getAllProducts(Principal principal,
                                                                  @RequestParam(value = "page-size", defaultValue = GlobalVariables.PAGE_SIZE_DEFAULT) Integer pageSize,
                                                                  @RequestParam(value = "page-offset", defaultValue = GlobalVariables.PAGE_OFFSET_DEFAULT) Integer pageOffset,
                                                                  @RequestParam(value = "sort-property", defaultValue = GlobalVariables.SORT_PROPERTY_DEFAULT) String sortProperty,
                                                                  @RequestParam(value = "sort-direction", defaultValue = GlobalVariables.SORT_DIRECTION_DEFAULT) String sortDirection) {
        Page productPage = productService.getAllProducts(principal.getName(), pageSize, pageOffset,
                sortProperty, sortDirection);
        return productViewDtoPagedResourcesAssembler.toModel(productPage);
    }

    @Operation(summary = "View all product's variations", description = "Here seller can view all the variation of a product that he has created.Product id need to be passed in the url", method = "GET")
    @GetMapping("/seller/products/variations/{id}")
    public PagedModel getAllProductVariations(Principal principal, @PathVariable("id") Long id,
                                              @RequestParam(value = "page-size", defaultValue = GlobalVariables.PAGE_SIZE_DEFAULT) Integer pageSize,
                                              @RequestParam(value = "page-offset", defaultValue = GlobalVariables.PAGE_OFFSET_DEFAULT) Integer pageOffset,
                                              @RequestParam(value = "sort-property", defaultValue = GlobalVariables.SORT_PROPERTY_DEFAULT) String sortProperty,
                                              @RequestParam(value = "sort-direction", defaultValue = GlobalVariables.SORT_DIRECTION_DEFAULT) String sortDirection)
            throws ParseException, IOException {
        Page productVariationsPage = productService.getAllProductVariations(principal.getName(), id,
                pageSize, pageOffset, sortProperty, sortDirection);
        return productVariationResponseDtoPagedResourcesAssembler.toModel(productVariationsPage);
    }

    @Operation(summary = "Delete Product", description = "API for the seller to delete his product", method = "DELETE")
    @DeleteMapping("/seller/delete-product/{id}")
    public ResponseEntity deleteProduct(Principal principal, @PathVariable Long id) {
        return productService.deleteProduct(principal.getName(), id);
    }

    @Operation(summary = "Update Product", description = "Seller can update the name and description of the product along with deciding on making it returnable or cancellable", method = "PATCH")
    @PatchMapping("/seller/update-product/{id}")
    public ResponseEntity updateProduct(Principal principal, @PathVariable Long id,
                                        @RequestBody ProductUpdateDto productUpdateDto)
            throws InvocationTargetException, IllegalAccessException {
        return productService.updateProduct(principal.getName(), id, productUpdateDto);
    }

    @Operation(summary = "Update Product Variation", description = "Seller can update the variation fields of the product. He needs to pass the product variation id", method = "PATCH")
    @RequestMapping(path = "/seller/update/product/variation/{id}", method = RequestMethod.PATCH,
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity updateProductVariation(Principal principal, @PathVariable Long id, ProductVariationUpdateDto productVariationUpdateDto) throws JsonProcessingException {
        return productService.updateProductVariation(principal.getName(), id, productVariationUpdateDto);
    }

    @Operation(summary = "View Product", description = "Customer can view the product details with its categories and its variations also", method = "GET")
    @GetMapping("/customer/product/{id}")
    public ResponseEntity viewProduct(Principal principal, @PathVariable("id") Long id)
            throws ParseException, IOException {
        return productService.getProductForCustomer(id);
    }

    @Operation(summary = "View All Products for customer", description = "Customer can view all the products in the form of a page. Category id is passed in the url. And it must be a leaf category else if it is not a leaf category then products of its child categories are returned.", method = "GET")
    @GetMapping("/customer/products/{id}")
    public PagedModel viewAllProducts(Principal principal, @PathVariable("id") Long id,
                                      @RequestParam(value = "page-size", defaultValue = GlobalVariables.PAGE_SIZE_DEFAULT) Integer pageSize,
                                      @RequestParam(value = "page-offset", defaultValue = GlobalVariables.PAGE_OFFSET_DEFAULT) Integer pageOffset,
                                      @RequestParam(value = "sort-property", defaultValue = GlobalVariables.SORT_PROPERTY_DEFAULT) String sortProperty,
                                      @RequestParam(value = "sort-direction", defaultValue = GlobalVariables.SORT_DIRECTION_DEFAULT) String sortDirection)
            throws ParseException, IOException {
        Page productPage = productService.getAllProductsOfLeaf(id, pageSize, pageOffset, sortProperty,
                sortDirection);
        return productDetailsForAdminDtoPagedResourcesAssembler.toModel(productPage);
    }

    @Operation(summary = "Similar Products", description = "Customer can view all the similar products related to the product id passed in the url. All those products in the same category of the product id passed are returned", method = "GET")
    @GetMapping("/customer/similar/products/{id}")
    public PagedModel viewAllSimilarProducts(Principal principal, @PathVariable("id") Long id,
                                             @RequestParam(value = "page-size", defaultValue = GlobalVariables.PAGE_SIZE_DEFAULT) Integer pageSize,
                                             @RequestParam(value = "page-offset", defaultValue = GlobalVariables.PAGE_OFFSET_DEFAULT) Integer pageOffset,
                                             @RequestParam(value = "sort-property", defaultValue = GlobalVariables.SORT_PROPERTY_DEFAULT) String sortProperty,
                                             @RequestParam(value = "sort-direction", defaultValue = GlobalVariables.SORT_DIRECTION_DEFAULT) String sortDirection)
            throws ParseException {
        Page productPage = productService.getAllSimilarProducts(id, pageSize, pageOffset, sortProperty,
                sortDirection);
        return productDetailsForCustomerDtoPagedResourcesAssembler.toModel(productPage);
    }

    @Operation(summary = "View Product", description = "Admin can view the product along with its category details and its variations primary images", method = "GET")
    @GetMapping("/admin/product/{id}")
    public ResponseEntity viewProductForAdmin(Principal principal, @PathVariable("id") Long id)
            throws ParseException {
        return productService.getProductForAdmin(id);
    }

    @Operation(summary = "View all products", description = "Admin can use this api to view all the products no matter if those products are active or not.")
    @GetMapping("/admin/products")
    public PagedModel viewAllProductForAdmin(Principal principal,
                                             @RequestParam(value = "page-size", defaultValue = GlobalVariables.PAGE_SIZE_DEFAULT) Integer pageSize,
                                             @RequestParam(value = "page-offset", defaultValue = GlobalVariables.PAGE_OFFSET_DEFAULT) Integer pageOffset,
                                             @RequestParam(value = "sort-property", defaultValue = GlobalVariables.SORT_PROPERTY_DEFAULT) String sortProperty,
                                             @RequestParam(value = "sort-direction", defaultValue = GlobalVariables.SORT_DIRECTION_DEFAULT) String sortDirection)
            throws ParseException {
        Page productsPage = productService.getAllProductForAdmin(pageSize, pageOffset, sortProperty,
                sortDirection);
        return productDetailsForAdminDtoPagedResourcesAssembler.toModel(productsPage);
    }

    @Operation(summary = "Activate Product", description = "Admin can activate the product by passing the id in the url. When admin activates the product, a mail is sent to the seller of the product informing him that his product has been activated", method = "PATCH")
    @PatchMapping("/admin/products/activate/{id}")
    public ResponseEntity activateProduct(Principal principal, @PathVariable("id") Long id) {
        return productService.activateProduct(id);
    }

    @Operation(summary = "Deactivate Product", description = "Admin can deactivate the product by passing the id in the url. When admin deactivates the product, a mail is sent to the seller of the product informing him that his product has been deactivated", method = "PATCH")
    @PatchMapping("/admin/products/deactivate/{id}")
    public ResponseEntity deactivateProduct(Principal principal, @PathVariable("id") Long id) {
        return productService.deactivateProduct(id);
    }
}
