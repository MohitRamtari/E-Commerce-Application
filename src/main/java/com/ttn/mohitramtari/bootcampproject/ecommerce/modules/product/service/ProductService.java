package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.shaded.json.parser.ParseException;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.dto.ProductVariationUpdateDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.dto.ProductDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.dto.ProductUpdateDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.dto.ProductVariationDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface ProductService {

    ResponseEntity addNewProduct(String name, ProductDto productDto);

    ResponseEntity addNewProductVariation(String email, ProductVariationDto productVariationDto)
            throws IOException;

    ResponseEntity getProduct(String email, Long id);

    ResponseEntity getProductVariation(String email, Long id) throws ParseException, IOException;

    Page getAllProducts(String name, Integer pageSize, Integer pageOffset, String sortProperty,
                        String sortDirection);

    Page getAllProductVariations(String email, Long id, Integer pageSize, Integer pageOffset,
                                 String sortProperty, String sortDirection) throws ParseException, IOException;

    ResponseEntity deleteProduct(String email, Long id);

    ResponseEntity updateProduct(String email, Long id, ProductUpdateDto productUpdateDto)
            throws InvocationTargetException, IllegalAccessException;

    ResponseEntity getProductForCustomer(Long id) throws ParseException, IOException;

    ResponseEntity getProductForAdmin(Long id) throws ParseException;

    ResponseEntity deactivateProduct(Long id);

    ResponseEntity activateProduct(Long id);

    Page getAllProductForAdmin(Integer pageSize, Integer pageOffset, String sortProperty,
                               String sortDirection) throws ParseException;

    Page getAllProductsOfLeaf(Long id, Integer pageSize, Integer pageOffset, String sortProperty,
                              String sortDirection) throws ParseException, IOException;

    Page getAllSimilarProducts(Long id, Integer pageSize, Integer pageOffset, String sortProperty,
                               String sortDirection) throws ParseException;

    ResponseEntity updateProductVariation(String email, Long id, ProductVariationUpdateDto productVariationDto) throws JsonProcessingException;
}
