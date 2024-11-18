package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.shaded.json.parser.JSONParser;
import com.nimbusds.jose.shaded.json.parser.ParseException;
import com.ttn.mohitramtari.bootcampproject.ecommerce.app.exception.ProductStatusException;
import com.ttn.mohitramtari.bootcampproject.ecommerce.app.exception.ResourceAlreadyExistException;
import com.ttn.mohitramtari.bootcampproject.ecommerce.app.exception.ResourcesNotFoundException;
import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.EmailSenderService;
import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.NullAwareBeanUtilsBean;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.dto.ProductVariationUpdateDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.model.Category;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.model.CategoryMetadataFieldValues;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.repository.CategoryMetadataFieldRepository;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.repository.CategoryMetadataFieldValuesRepository;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.repository.CategoryRepository;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.dto.*;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.dto.admin.ProductDetailsForAdminDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.dto.admin.VariationDetailsForAdminDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.model.Product;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.model.ProductVariation;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.repository.ProductRepository;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.repository.ProductVariationRepository;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.service.ProductService;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.model.Seller;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.repository.SellerRepository;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.imagestorage.service.ImageStorageService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {

    static final String PRIMARY_IMAGE_URL = "http://localhost:8080/download-product-image/";
    static final String SECONDARY_IMAGE_URL = "http://localhost:8080/download-product-variation-image/";
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    MessageSource messageSource;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    SellerRepository sellerRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductVariationRepository productVariationRepository;
    @Autowired
    CategoryMetadataFieldRepository categoryMetadataFieldRepository;
    @Autowired
    CategoryMetadataFieldValuesRepository categoryMetadataFieldValuesRepository;
    @Autowired
    NullAwareBeanUtilsBean nullAwareBeanUtilsBean;
    @Autowired
    EmailSenderService emailSenderService;
    @Autowired
    ImageStorageService imageStorageService;

    @Override
    public ResponseEntity<String> addNewProduct(String email, ProductDto productDto) {
        Optional<Category> category = categoryRepository.findById(productDto.getCategoryId());

        if (category.isEmpty()) {
            throw new ResourcesNotFoundException(
                    messageSource.getMessage("no.category.found", null, LocaleContextHolder.getLocale()));
        }

        List<Category> leafCategoryList = categoryRepository.getAllLeafCategories();
        if (!leafCategoryList.contains(category.get())) {
            throw new ResourcesNotFoundException(
                    messageSource.getMessage("no.leaf.category", null, LocaleContextHolder.getLocale()));
        }

        Seller seller = sellerRepository.findByUserEmail(email);

        Optional<Product> product = productRepository.productAlreadyExist(productDto.getBrand(),
                category.get().getId(), seller.getUserId());
        if (product.isPresent()) {
            if (product.get().getName().equalsIgnoreCase(productDto.getName()))
                throw new ResourceAlreadyExistException(
                        messageSource.getMessage("product.already.exist", null, LocaleContextHolder.getLocale()));
        }

        Product newProduct = modelMapper.map(productDto, Product.class);
        newProduct.setCategory(category.get());

        newProduct.setSeller(seller);

        productRepository.save(newProduct);
        emailSenderService.sendEmail(emailSenderService.sendProductActivationMail(newProduct));
        return ResponseEntity.ok("Product Created");
    }

    @Override
    public ResponseEntity<String> addNewProductVariation(String email,
                                                         ProductVariationDto productVariationDto) throws IOException {

        //Retrieving the product and category from the database
        Product product = productRepository.findById(productVariationDto.getProductId()).orElseThrow(() -> new ResourcesNotFoundException(messageSource.getMessage("no.product.found", null, LocaleContextHolder.getLocale())));

        Category category = categoryRepository.findById(product.getCategory().getId()).orElseThrow(() -> new ResourcesNotFoundException(messageSource.getMessage("no.category.found", null, LocaleContextHolder.getLocale())));

        //if product is deleted throwing an exception here
        if (Boolean.TRUE.equals(product.getIsDeleted())) {
            throw new ProductStatusException(
                    messageSource.getMessage("product.deleted", null, LocaleContextHolder.getLocale()));
        }

        //If product is not active yet, throws an exception
        if (Boolean.FALSE.equals(product.getIsActive())) {
            throw new ResourcesNotFoundException(
                    messageSource.getMessage("product.active", null, LocaleContextHolder.getLocale()));
        }

//        if price of variation is less than 0 throw an exception
        if (productVariationDto.getPrice() < 0) {
            throw new IllegalArgumentException("Price should be 0 or more than 0");
        }

        //if quantity of variation is less than 0, throw an exception
        if (productVariationDto.getQuantityAvailable() < 0) {
            throw new IllegalArgumentException("Quantity of the product should be 0 or more than 0");
        }
        List<String> metadataList = productVariationRepository.findMetadataByProductId(product.getId());

        for (String metadataToCheck : metadataList) {
            metadataToCheck = metadataToCheck.replaceAll("\\s", "");
            if (metadataToCheck.equals(productVariationDto.getMetadata()))
                throw new ResourceAlreadyExistException("A variation already exist by the given specifications");
        }

        //Retrieving the possible category metadata field values set by the admin for the category in which product belongs
        List<CategoryMetadataFieldValues> categoryMetadataFieldValuesList = categoryMetadataFieldValuesRepository.findByCategoryId(
                category.getId());

        //Creating a list to hold the names of the metadata fields created by admin
        List<String> metadataFieldList = new ArrayList<>();
        Map<String, List<String>> fieldNamesAndValues = new HashMap<>();

        //Using the loop over categoryMetadataFieldValues to retrieve their corresponding field names
        for (CategoryMetadataFieldValues c : categoryMetadataFieldValuesList) {
            //Retrieving the metadata field names
            String name = categoryMetadataFieldRepository.findById(c.getCategoryMetadataField().getId()).orElseThrow().getName();
            metadataFieldList.add(name);

            //Retrieving metadata field ids, which will be used to retrieve the values
            Long id = categoryMetadataFieldRepository.findById(c.getCategoryMetadataField().getId()).get()
                    .getId();

            //Retrieving the metadata field values
            List<String> values = List.of(
                    categoryMetadataFieldValuesRepository.findByCategoryIdAndCategoryMetadataFieldId(
                            category.getId(), id).getMetadataFieldValues().split(","));

            fieldNamesAndValues.put(name, values);
        }

        //Getting the metadata sent by the seller for the variation and storing it in the format of a map
        Map<String, String> metadataFieldAndValues = new ObjectMapper().readValue(
                productVariationDto.getMetadata(), HashMap.class);

        //if count of fields sent by seller and fields to be set for a product are not equal
        //that means seller has not set all the required fields
        if (!metadataFieldAndValues.keySet().equals(fieldNamesAndValues.keySet())) {
            throw new ResourcesNotFoundException(
                    messageSource.getMessage("not.all.fields", null, LocaleContextHolder.getLocale()));
        }

        //Retrieving the values sent by the seller for the fields, one by one field and then checking if
        //those values exist in the values set by the admin
        for (Map.Entry<String, String> entry : metadataFieldAndValues.entrySet()) {
            List<String> values = fieldNamesAndValues.get(entry.getKey());
            if (!values.contains(entry.getValue())) {
                throw new ResourcesNotFoundException(
                        messageSource.getMessage("invalid.value", null, LocaleContextHolder.getLocale())
                                + entry.getKey());
            }
        }

        if ((productVariationDto.getPrimaryImage() == null) || (productVariationDto.getPrimaryImage().isEmpty()))
            throw new ResourcesNotFoundException("You haven't provided primary image");

        //Creating an object of product variation and setting its properties and then saving it in database
        ProductVariation productVariation = new ProductVariation();
        productVariation.setProduct(product);
        productVariation.setPrice(productVariationDto.getPrice());
        productVariation.setMetadata(productVariationDto.getMetadata());
        productVariation.setQuantityAvailable(productVariationDto.getQuantityAvailable());
        productVariation.setIsActive(true);
        productVariationRepository.save(productVariation);
        imageStorageService.storeProductImage(productVariation.getId(), productVariationDto.getPrimaryImage());
        if (!productVariationDto.getSecondaryImages().isEmpty()) {
            for (int i = 0; i < productVariationDto.getSecondaryImages().size(); i++)
                imageStorageService.storeProductVariationImage(productVariation.getId() + "." + i + 1, productVariationDto.getSecondaryImages().get(i));
        }
        productVariationRepository.updateImageSet(productVariation.getId());
        return ResponseEntity.ok(messageSource.getMessage("product.variation.created", null,
                LocaleContextHolder.getLocale()));
    }

    /**
     * Method to get the product details.
     *
     * @param email of the seller
     * @param id    of the product
     * @return product details in the form of ProductView Dto
     */
    @Override
    public ResponseEntity getProduct(String email, Long id) {
        Seller seller = sellerRepository.findByUserEmail(email);
        if (seller == null) {
            throw new ResourcesNotFoundException("There is no seller associated with this email");
        }

        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            throw new ResourcesNotFoundException(
                    "There is no product associated with the provided id : " + id);
        }
        if (product.get().getSeller() != seller) {
            throw new ResourcesNotFoundException("Product for the given id is not created by you");
        }
        if (Boolean.TRUE.equals(product.get().getIsDeleted())) {
            throw new ResourcesNotFoundException("Product for the given id has been deleted");
        }
        if (Boolean.FALSE.equals(product.get().getIsActive())) {
            throw new ResourcesNotFoundException("Product is not active yet");
        }

        ProductViewDto productDto = modelMapper.map(product.get(), ProductViewDto.class);
        return ResponseEntity.ok(productDto);
    }

    /**
     * Method to retrieve variation of the product whose id is passed
     *
     * @param email of the seller
     * @param id    of the product variation
     * @return Product Variation details in the form of ProductVariationDto
     * @throws ParseException
     */
    @Override
    public ResponseEntity getProductVariation(String email, Long id) throws ParseException, IOException {
        Seller seller = sellerRepository.findByUserEmail(email);
        if (seller == null) {
            throw new ResourcesNotFoundException("No Seller found for the given email");
        }

        Optional<ProductVariation> productVariation = productVariationRepository.findById(id);

        if (productVariation.isEmpty()) {
            throw new ResourcesNotFoundException("No Product Variation found for the passed id");
        }

        if (Boolean.TRUE.equals(productVariation.get().getProduct().getIsDeleted())) {
            throw new ResourcesNotFoundException("Product has been deleted");
        }

        if (productVariation.get().getProduct().getSeller() != seller) {
            throw new ResourcesNotFoundException(
                    "The given product variation id is not associated with your account");
        }

        ProductVariationResponseDto productVariationDto = modelMapper.map(productVariation.get(),
                ProductVariationResponseDto.class);

        Product product = productVariation.get().getProduct();
        ProductDto productDto = modelMapper.map(product, ProductDto.class);
        productVariationDto.setProduct(productDto);

        //Converting the metadata from json string to json object
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(productVariation.get().getMetadata());
        productVariationDto.setMetadata(json);
        if (productVariationRepository.findById(productVariation.get().getId()).get().getIsPrimaryImageSet().booleanValue())
            productVariationDto.setPrimaryImage(PRIMARY_IMAGE_URL + productVariation.get().getId());
        else
            productVariationDto.setPrimaryImage(null);

        List<String> secondaryImagesList = imageStorageService.getProductVariationImagesId(productVariation.get().getId().toString());
        if (!secondaryImagesList.isEmpty()) {
            List<String> secondaryImagesUri = new ArrayList<>();
            for (int i = 0; i < secondaryImagesList.size(); i++) {
                secondaryImagesUri.add(SECONDARY_IMAGE_URL + secondaryImagesList.get(i));
            }
            productVariationDto.setSecondaryImages(secondaryImagesUri);
        }
        return ResponseEntity.ok(productVariationDto);
    }

    /**
     * Method to retrieve all the products associated with a seller in the forma of a page
     *
     * @param email         of the seller
     * @param pageSize
     * @param pageOffset
     * @param sortProperty
     * @param sortDirection
     * @return All the products of the seller in the form of page
     */
    @Override
    public Page getAllProducts(String email, Integer pageSize, Integer pageOffset,
                               String sortProperty, String sortDirection) {
        Sort.Direction sortingDirection =
                sortDirection.equalsIgnoreCase("ASC") ? Direction.ASC : Direction.DESC;

        Seller seller = sellerRepository.findByUserEmail(email);
        if (seller == null) {
            throw new ResourcesNotFoundException("{no.seller.found}");
        }

        Pageable pageable = PageRequest.of(pageOffset, pageSize, sortingDirection, sortProperty);
        return productRepository.findBySellerId(seller.getUserId(),
                pageable).map(product -> modelMapper.map(product, ProductViewDto.class));
    }

    /**
     * Method to get all the variations of the product in the form of a page
     *
     * @param email
     * @param id
     * @param pageSize
     * @param pageOffset
     * @param sortProperty
     * @param sortDirection
     * @return
     * @throws ParseException
     */
    @Override
    public Page getAllProductVariations(String email, Long id, Integer pageSize, Integer pageOffset,
                                        String sortProperty, String sortDirection) throws ParseException, IOException {
        Sort.Direction sortingDirection =
                sortDirection.equalsIgnoreCase("ASC") ? Direction.ASC : Direction.DESC;

        Seller seller = sellerRepository.findByUserEmail(email);
        if (seller == null) {
            throw new ResourcesNotFoundException("{no.seller.found}");
        }

        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            throw new ResourcesNotFoundException("{no.product.found}");
        }

        if (product.get().getSeller() != seller) {
            throw new ResourcesNotFoundException(
                    "{seller.no.authority}");
        }

        Pageable pageable = PageRequest.of(pageOffset, pageSize, sortingDirection, sortProperty);
        List<ProductVariation> variationList = productVariationRepository.findByProductId(id, pageable);
        List<VariationDto> variationDtoList = new ArrayList<>();
        for (ProductVariation variation : variationList) {
            VariationDto variationDto = modelMapper.map(variation, VariationDto.class);

            //Converting the metadata from json string to json object
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(variation.getMetadata());
            variationDto.setMetadata(json);
            if (productVariationRepository.findById(variation.getId()).get().getIsPrimaryImageSet().booleanValue())
                variationDto.setPrimaryImage(PRIMARY_IMAGE_URL + variation.getId());
            else
                variationDto.setPrimaryImage(null);

            List<String> secondaryImagesList = imageStorageService.getProductVariationImagesId(variation.getId().toString());
            if (!secondaryImagesList.isEmpty()) {
                List<String> secondaryImagesUri = new ArrayList<>();
                for (int i = 0; i < secondaryImagesList.size(); i++) {
                    secondaryImagesUri.add(SECONDARY_IMAGE_URL + secondaryImagesList.get(i));
                }
                variationDto.setSecondaryImages(secondaryImagesUri);
            }
            variationDtoList.add(variationDto);
        }
        return convertListToPage(variationDtoList, pageSize, pageOffset, sortProperty, sortDirection);
    }

    /**
     * Method to delete a product
     *
     * @param email of the seller
     * @param id    of the product to be deleted
     * @return Success message stating that product has been deleted
     */
    @Override
    public ResponseEntity deleteProduct(String email, Long id) {
        Seller seller = sellerRepository.findByUserEmail(email);
        if (seller == null) {
            throw new ResourcesNotFoundException(messageSource.getMessage("no.seller.found", null, LocaleContextHolder.getLocale()));
        }

        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            throw new ResourcesNotFoundException(messageSource.getMessage("no.product.found", null, LocaleContextHolder.getLocale()));
        }

        if (product.get().getSeller() != seller) {
            throw new ResourcesNotFoundException(messageSource.getMessage("seller.no.authority", null, LocaleContextHolder.getLocale()));
        }

        product.get().setIsDeleted(true);
        productRepository.save(product.get());
        return ResponseEntity.ok(messageSource.getMessage("product.got.deleted", null, LocaleContextHolder.getLocale()));
    }

    /**
     * Method to update the product by the seller
     *
     * @param email            of the seller
     * @param id               of the product to be updated
     * @param productUpdateDto product details to be updated
     * @return Success message stating that product has been updated
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @Override
    public ResponseEntity updateProduct(String email, Long id, ProductUpdateDto productUpdateDto)
            throws InvocationTargetException, IllegalAccessException {
        Seller seller = sellerRepository.findByUserEmail(email);
        if (seller == null) {
            throw new ResourcesNotFoundException(messageSource.getMessage("no.seller.found", null, LocaleContextHolder.getLocale()));
        }

        Product product = productRepository.findById(id).orElseThrow(() -> new ResourcesNotFoundException(messageSource.getMessage("no.product.found", null, LocaleContextHolder.getLocale())));

        if (product.getSeller() != seller) {
            throw new ResourcesNotFoundException(
                    messageSource.getMessage("seller.no.authority", null, LocaleContextHolder.getLocale()));
        }

        if (Boolean.TRUE.equals(product.getIsDeleted())) {
            throw new ResourcesNotFoundException(
                    "Your product has already been deleted. You can't update it");
        }

        checkUpdateFieldsAreEmpty(productUpdateDto);
        if (productUpdateDto.getName() == null && productUpdateDto.getIsReturnable() == null
                && productUpdateDto.getDescription() == null
                && productUpdateDto.getIsCancellable() == null) {
            throw new ResourcesNotFoundException(messageSource.getMessage("no.fields.for.update", null, LocaleContextHolder.getLocale()));
        }

        checkUniqueNameOrNot(productUpdateDto.getName(), product.getBrand(),
                product.getCategory().getId(), product.getSeller().getUserId());

        nullAwareBeanUtilsBean.copyProperties(product, productUpdateDto);
        productRepository.save(product);
        return ResponseEntity.ok(messageSource.getMessage("product.updated", null, LocaleContextHolder.getLocale()));
    }

    /**
     * Method to retrieve Product details for the customer
     *
     * @param id of the product
     * @return product details in the form of ProductDetailsForAdminDto
     * @throws ParseException
     */
    @Override
    public ResponseEntity getProductForCustomer(Long id) throws ParseException, IOException {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            throw new ResourcesNotFoundException("No product found for the given id");
        }
        if (Boolean.FALSE.equals(product.get().getIsActive())) {
            throw new ResourcesNotFoundException("Product is not yet activated by the admin");
        }
        if (Boolean.TRUE.equals(product.get().getIsDeleted())) {
            throw new ResourcesNotFoundException("Product has been deleted");
        }
        if (productVariationRepository.findVariantsByProductId(product.get().getId()).isEmpty()) {
            throw new ResourcesNotFoundException("Product doesn't have any variation");
        }

        ProductDetailsForCustomerAllImagesDto productDto = modelMapper.map(product.get(),
                ProductDetailsForCustomerAllImagesDto.class);

        List<ProductVariation> productVariationSet = productVariationRepository.findVariantsByProductId(product.get().getId());
        Set<VariationDetailsForCustomerAllImagesDto> variationDetailsSet = new HashSet<>();

        for (ProductVariation variation : productVariationSet) {
            VariationDetailsForCustomerAllImagesDto variationForCustomer = modelMapper.map(variation,
                    VariationDetailsForCustomerAllImagesDto.class);
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(variation.getMetadata());
            variationForCustomer.setMetadata(json);

            if (productVariationRepository.findById(variation.getId()).get().getIsPrimaryImageSet().booleanValue())
                variationForCustomer.setPrimaryImage(PRIMARY_IMAGE_URL + variation.getId());
            else
                variationForCustomer.setPrimaryImage(null);

            List<String> secondaryImagesList = imageStorageService.getProductVariationImagesId(variation.getId().toString());
            if (!secondaryImagesList.isEmpty()) {
                List<String> secondaryImagesUri = new ArrayList<>();
                for (int i = 0; i < secondaryImagesList.size(); i++) {
                    secondaryImagesUri.add(SECONDARY_IMAGE_URL + secondaryImagesList.get(i));
                }
                variationForCustomer.setSecondaryImages(secondaryImagesUri);
            }

            variationDetailsSet.add(variationForCustomer);
        }

        productDto.setProductVariationSet(variationDetailsSet);
        return ResponseEntity.ok(productDto);
    }

    /**
     * Method to retrieve product details for admin
     *
     * @param id of the product
     * @return product details in the form of ProductAdminDto
     * @throws ParseException
     */
    @Override
    public ResponseEntity getProductForAdmin(Long id) throws ParseException {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            throw new ResourcesNotFoundException("There is no product associated with the provided id");
        }

        ProductDetailsForAdminDto productForAdmin = modelMapper.map(product.get(),
                ProductDetailsForAdminDto.class);

        List<ProductVariation> productVariationSet = productVariationRepository.findVariantsByProductId(product.get().getId());
        Set<VariationDetailsForAdminDto> variationDetailsSet = new HashSet<>();

        for (ProductVariation variation : productVariationSet) {
            VariationDetailsForAdminDto variationForAdmin = new VariationDetailsForAdminDto();
            variationForAdmin.setVariationId(variation.getId());
            if (productVariationRepository.findById(variation.getId()).get().getIsPrimaryImageSet().booleanValue())
                variationForAdmin.setPrimaryImage(PRIMARY_IMAGE_URL + variation.getId());
            else
                variationForAdmin.setPrimaryImage(null);
            variationDetailsSet.add(variationForAdmin);
        }
        productForAdmin.setProductVariationSet(variationDetailsSet);
        return ResponseEntity.ok(productForAdmin);
    }

    /**
     * Method to retrieve all the product details in the form of a page
     *
     * @return Page of all the products
     * @throws ParseException
     */
    @Override
    public Page getAllProductForAdmin(Integer pageSize, Integer pageOffset, String sortProperty,
                                      String sortDirection) throws ParseException {
        List<Product> productList = productRepository.findAll();
        List<ProductDetailsForAdminDto> productDtoList = new ArrayList<>();
        for (Product product : productList) {
            ProductDetailsForAdminDto productForAdmin = modelMapper.map(product,
                    ProductDetailsForAdminDto.class);

            List<ProductVariation> productVariationSet = productVariationRepository.findVariantsByProductId(product.getId());
            Set<VariationDetailsForAdminDto> variationDetailsSet = new HashSet<>();

            for (ProductVariation variation : productVariationSet) {
                VariationDetailsForAdminDto variationForAdmin = modelMapper.map(variation,
                        VariationDetailsForAdminDto.class);
                if (productVariationRepository.findById(variation.getId()).get().getIsPrimaryImageSet().booleanValue())
                    variationForAdmin.setPrimaryImage(PRIMARY_IMAGE_URL + variation.getId());
                else
                    variationForAdmin.setPrimaryImage(null);
                variationDetailsSet.add(variationForAdmin);
            }
            productForAdmin.setProductVariationSet(variationDetailsSet);
            productDtoList.add(productForAdmin);
        }
        return convertListToPage(productDtoList, pageSize, pageOffset, sortProperty, sortDirection);
    }

    /**
     * Method to retrieve all the products for the customer
     *
     * @param id            of leaf node category or any other category
     * @param pageSize
     * @param pageOffset
     * @param sortProperty
     * @param sortDirection
     * @return products of leaf category of which id is passed, else all products of the id passed
     * @throws ParseException
     */
    @Override
    public Page getAllProductsOfLeaf(Long id, Integer pageSize, Integer pageOffset,
                                     String sortProperty, String sortDirection) throws ParseException, IOException {
        Optional<Category> category = categoryRepository.findById(id);
        List<Category> categoryList = categoryRepository.getAllLeafCategories();

        if (category.isEmpty()) {
            throw new ResourcesNotFoundException(
                    messageSource.getMessage("no.category.found", null, LocaleContextHolder.getLocale()));
        }
        if (!categoryList.contains(category.get())) {
            throw new ResourcesNotFoundException("It is not a leaf category");
        }

        List<ProductVariation> variation;
        List<Product> productList = productRepository.findByCategoryId(id);
        List<Product> productList1 = new ArrayList<>();

        List<ProductDetailsForCustomerDto> productDtoList = new ArrayList<>();
        for (Product product : productList) {
            variation = productVariationRepository.checkIfProductExist(product.getId());
            if (variation.isEmpty() || Boolean.FALSE.equals(product.getIsActive()) || Boolean.TRUE.equals(product.getIsDeleted())) {
                continue;
            }
            productList1.add(product);
        }

        if (productList1.isEmpty()) {
            throw new ResourcesNotFoundException("No products found in the category");
        }

        for (Product product1 : productList1) {
            ProductDetailsForCustomerDto productForCustomer = modelMapper.map(product1,
                    ProductDetailsForCustomerDto.class);

            List<ProductVariation> productVariationSet = productVariationRepository.findVariantsByProductId(product1.getId());
            Set<VariationDetailsForCustomerDto> variationDetailsSet = new HashSet<>();

            for (ProductVariation variation1 : productVariationSet) {
                VariationDetailsForCustomerDto variationForCustomer = new VariationDetailsForCustomerDto();
                variationForCustomer.setVariationId(variation1.getId());
                if (productVariationRepository.findById(variation1.getId()).get().getIsPrimaryImageSet().booleanValue())
                    variationForCustomer.setPrimaryImage(PRIMARY_IMAGE_URL + variation1.getId());
                else
                    variationForCustomer.setPrimaryImage(null);

                variationDetailsSet.add(variationForCustomer);
            }
            productForCustomer.setProductVariationSet(variationDetailsSet);
            productDtoList.add(productForCustomer);
        }

        //ref : https://stackoverflow.com/a/63869850 to convert List to page wit pageable object
        return convertListToPage(productDtoList, pageSize, pageOffset, sortProperty, sortDirection);
    }

    /**
     * Method to retrieve all the similar products in the form of a page. Here i am choosing similar
     * products on the basis of the category of the product whose id has been passed
     *
     * @param id            of the product of which similar products need to be found
     * @param pageSize
     * @param pageOffset
     * @param sortProperty
     * @param sortDirection
     * @return
     * @throws ParseException
     */
    @Override
    public Page getAllSimilarProducts(Long id, Integer pageSize, Integer pageOffset,
                                      String sortProperty, String sortDirection) throws ParseException {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            throw new ResourcesNotFoundException("No product found for the given id");
        }

        if (Boolean.TRUE.equals(product.get().getIsDeleted())) {
            throw new ResourcesNotFoundException("Product has been deleted for the id passed");
        }

        if (Boolean.FALSE.equals(product.get().getIsActive())) {
            throw new ResourcesNotFoundException("Product is not active");
        }

        List<Product> productList = productRepository.findByCategoryId(
                product.get().getCategory().getId());
        productList.remove(product.get());
        List<ProductDetailsForCustomerDto> productDtoList = new ArrayList<>();
        for (Product product1 : productList) {
            ProductDetailsForCustomerDto productForCustomer = modelMapper.map(product1,
                    ProductDetailsForCustomerDto.class);

            List<ProductVariation> productVariationSet = productVariationRepository.findVariantsByProductId(product1.getId());
            Set<VariationDetailsForCustomerDto> variationDetailsSet = new HashSet<>();

            for (ProductVariation variation1 : productVariationSet) {
                VariationDetailsForCustomerDto variationForCustomer = modelMapper.map(variation1,
                        VariationDetailsForCustomerDto.class);
                variationForCustomer.setVariationId(variation1.getId());

                if (productVariationRepository.findById(variation1.getId()).get().getIsPrimaryImageSet().booleanValue())
                    variationForCustomer.setPrimaryImage(PRIMARY_IMAGE_URL + variation1.getId());
                else
                    variationForCustomer.setPrimaryImage(null);

                variationDetailsSet.add(variationForCustomer);
            }
            productForCustomer.setProductVariationSet(variationDetailsSet);
            productDtoList.add(productForCustomer);
        }

        Sort.Direction sortingDirection =
                sortDirection.equalsIgnoreCase("ASC") ? Direction.ASC : Direction.DESC;
        Pageable pageable = PageRequest.of(pageOffset, pageSize, sortingDirection, sortProperty);
        int start = Math.min((int) pageable.getOffset(), productDtoList.size());
        int end = Math.min((start + pageable.getPageSize()), productDtoList.size());
        return new PageImpl<>(productDtoList.subList(start, end),
                pageable, productDtoList.size());
    }

    @Override
    public ResponseEntity updateProductVariation(String email, Long id, ProductVariationUpdateDto productVariationUpdateDto) throws JsonProcessingException {
        Seller seller = sellerRepository.findByUserEmail(email);
        if (seller == null) {
            throw new ResourcesNotFoundException(messageSource.getMessage("no.seller.found", null, LocaleContextHolder.getLocale()));
        }

        ProductVariation productVariation = productVariationRepository.findById(id).orElseThrow(() -> new ResourcesNotFoundException("No variation found by this id"));
        Product product = productRepository.findById(productVariation.getProduct().getId()).orElseThrow(() -> new ResourcesNotFoundException("Variation is not associated with any product"));

        if (product.getSeller() != seller) {
            throw new ResourcesNotFoundException(
                    messageSource.getMessage("seller.no.authority", null, LocaleContextHolder.getLocale()));
        }

        if (Boolean.TRUE.equals(product.getIsDeleted())) {
            throw new ResourcesNotFoundException(
                    "Your product has already been deleted. You can't update it");
        }

        if (Boolean.FALSE.equals(product.getIsActive()))
            throw new ResourcesNotFoundException("Product is not active ");

        checkVariationFieldsAreCorrect(productVariationUpdateDto, productVariation.getProduct().getId(), productVariation.getProduct().getCategory().getId());

        if (productVariationUpdateDto.getPrice() != null)
            productVariation.setPrice(productVariationUpdateDto.getPrice());

        if (productVariationUpdateDto.getMetadata() != null)
            productVariation.setMetadata(productVariationUpdateDto.getMetadata());

        if (productVariationUpdateDto.getQuantityAvailable() != null)
            productVariation.setQuantityAvailable(productVariationUpdateDto.getQuantityAvailable());

        if (productVariationUpdateDto.getIsActive() != null)
            productVariation.setIsActive(productVariationUpdateDto.getIsActive());

        productVariationRepository.save(productVariation);

        if (!productVariationUpdateDto.getPrimaryImage().isEmpty())
            imageStorageService.storeProductImage(productVariation.getId(), productVariationUpdateDto.getPrimaryImage());

        if (productVariationUpdateDto.getSecondaryImages() != null) {
            for (int i = 0; i < productVariationUpdateDto.getSecondaryImages().size(); i++) {
                int j = i + 1;
                imageStorageService.storeProductVariationImage(productVariation.getId() + "." + j, productVariationUpdateDto.getSecondaryImages().get(i));
            }
        }
        productVariationRepository.updateImageSet(productVariation.getId());
        return ResponseEntity.ok("Product Variation has been updated");
    }

    private void checkVariationFieldsAreCorrect(ProductVariationUpdateDto productVariationUpdateDto, Long productId, Long categoryId) throws JsonProcessingException {
        if (productVariationUpdateDto.getIsActive() == null && productVariationUpdateDto.getPrice() == null && productVariationUpdateDto.getQuantityAvailable() == null && productVariationUpdateDto.getMetadata() == null && productVariationUpdateDto.getPrimaryImage() == null)
            throw new IllegalArgumentException("You have not provided any fields to be updated");

        if (productVariationUpdateDto.getPrice() != null && productVariationUpdateDto.getPrice() < 0)
            throw new IllegalArgumentException("Price of the product's variation can't be less than 0");

        if (productVariationUpdateDto.getQuantityAvailable() != null && productVariationUpdateDto.getQuantityAvailable() < 0)
            throw new IllegalArgumentException("Quantity of the product's variation can't be less than 0");

        Category category = categoryRepository.findById(categoryId).get();

        if (productVariationUpdateDto.getMetadata() != null) {
            List<String> metadataList = productVariationRepository.findMetadataByProductId(productId);

            //Retrieving the possible category metadata field values set by the admin for the category in which product belongs
            List<CategoryMetadataFieldValues> categoryMetadataFieldValuesList = categoryMetadataFieldValuesRepository.findByCategoryId(
                    category.getId());

            //Creating a list to hold the names of the metadata fields created by admin
            List<String> metadataFieldList = new ArrayList<>();
            Map<String, List<String>> fieldNamesAndValues = new HashMap<>();

            //Using the loop over categoryMetadataFieldValues to retrieve their corresponding field names
            for (CategoryMetadataFieldValues c : categoryMetadataFieldValuesList) {
                //Retrieving the metadata field names
                String name = categoryMetadataFieldRepository.findById(c.getCategoryMetadataField().getId()).orElseThrow().getName();
                metadataFieldList.add(name);

                //Retrieving metadata field ids, which will be used to retrieve the values
                Long id = categoryMetadataFieldRepository.findById(c.getCategoryMetadataField().getId()).get()
                        .getId();

                //Retrieving the metadata field values
                List<String> values = List.of(
                        categoryMetadataFieldValuesRepository.findByCategoryIdAndCategoryMetadataFieldId(
                                category.getId(), id).getMetadataFieldValues().split(","));

                fieldNamesAndValues.put(name, values);
            }

            //Getting the metadata sent by the seller for the variation and storing it in the format of a map
            Map<String, String> metadataFieldAndValues = new ObjectMapper().readValue(
                    productVariationUpdateDto.getMetadata(), HashMap.class);

            //if count of fields sent by seller and fields to be set for a product are not equal
            //that means seller has not set all the required fields
            if (!metadataFieldAndValues.keySet().equals(fieldNamesAndValues.keySet())) {
                throw new ResourcesNotFoundException(
                        messageSource.getMessage("not.all.fields", null, LocaleContextHolder.getLocale()));
            }

            //Retrieving the values sent by the seller for the fields, one by one field and then checking if
            //those values exist in the values set by the admin
            for (Map.Entry<String, String> entry : metadataFieldAndValues.entrySet()) {
                List<String> values = fieldNamesAndValues.get(entry.getKey());
                if (!values.contains(entry.getValue())) {
                    throw new ResourcesNotFoundException(
                            messageSource.getMessage("invalid.value", null, LocaleContextHolder.getLocale())
                                    + entry.getKey());
                }
            }
        }
    }

    /**
     * Method to deactivate a product
     *
     * @param id of the product that needs to be deactivated
     * @return Success message stating that product has been deactivated
     */
    @Override
    public ResponseEntity deactivateProduct(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            throw new ResourcesNotFoundException("There is no product associated with this id");
        }

        if (Boolean.FALSE.equals(product.get().getIsActive())) {
            throw new ResourcesNotFoundException("Product is already deactivated");
        }

        product.get().setIsActive(false);
        productRepository.save(product.get());

        emailSenderService.sendEmail(emailSenderService.getSellerProductDeactivationMail(
                product.get().getSeller().getUserEmail(), product.get().toString()));
        return ResponseEntity.ok("Product Deactivated Successfully");
    }

    /**
     * Method to activate a product
     *
     * @param id of the product to be activated
     * @return Success message stating that product has been activated
     */
    @Override
    public ResponseEntity activateProduct(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            throw new ResourcesNotFoundException("There is no product associated with this id");
        }

        if (Boolean.TRUE.equals(product.get().getIsActive())) {
            throw new ResourcesNotFoundException("Product is already activated");
        }

        product.get().setIsActive(true);
        productRepository.save(product.get());

        emailSenderService.sendEmail(
                emailSenderService.getSellerProductActivationMail(product.get().getSeller().getUserEmail(),
                        product.get().toString()));
        return ResponseEntity.ok("Product Activated Successfully");
    }

    private void checkUniqueNameOrNot(String name, String brand, Long categoryId, Long sellerId) {
        List<String> productNames = productRepository.getProductNameList(brand, categoryId, sellerId);
        if (productNames.contains(name)) {
            throw new ResourceAlreadyExistException(
                    "There is already a product with the same name of same brand in the same category by you");
        }
    }

    Page convertListToPage(List listToConvert, Integer pageSize, Integer pageOffset,
                           String sortProperty, String sortDirection) {
        Sort.Direction sortingDirection =
                sortDirection.equalsIgnoreCase("ASC") ? Direction.ASC : Direction.DESC;
        Pageable pageable = PageRequest.of(pageOffset, pageSize, sortingDirection, sortProperty);
        int start = Math.min((int) pageable.getOffset(), listToConvert.size());
        int end = Math.min((start + pageable.getPageSize()), listToConvert.size());
        return new PageImpl<>(listToConvert.subList(start, end), pageable, listToConvert.size());
    }

    private void checkUpdateFieldsAreEmpty(ProductUpdateDto productUpdateDto) {
        if (productUpdateDto.getName() != null && productUpdateDto.getName().length() == 0)
            throw new IllegalArgumentException("Product name can't be empty");

        if (productUpdateDto.getDescription() != null && productUpdateDto.getDescription().length() == 0)
            throw new IllegalArgumentException("Product description can't be empty");
    }
}
