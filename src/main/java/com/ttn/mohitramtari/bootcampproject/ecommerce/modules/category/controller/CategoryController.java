package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.controller;

import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.GlobalVariables;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.dto.*;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.dto.category_view.CategoryDetailsDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    private PagedResourcesAssembler<MetadataFieldResponseDto> metadataFieldResponseDtoPagedResourcesAssembler;

    @Autowired
    private PagedResourcesAssembler<CategoryDetailsDto> categoryDetailsDtoPagedResourcesAssembler;

    @Operation(summary = "Add metadata fields", description = "API for the admin to add a metadata field", method = "POST")
    @PostMapping("/admin/add/metadata-field")
    public ResponseEntity addNewMetadataField(@Valid @RequestBody MetadataFieldDto metadataFieldDto) {
        return categoryService.addNewMetadataField(metadataFieldDto);
    }

    @Operation(summary = "View all metadata fields", description = "Api for the admin to view all the metadata fields that he has registered", method = "GET")
    @GetMapping("/admin/metadata-fields")
    public PagedModel<EntityModel<MetadataFieldResponseDto>> viewAllMetadataFields(
            @RequestParam(value = "page-size", defaultValue = GlobalVariables.PAGE_SIZE_DEFAULT) Integer pageSize,
            @RequestParam(value = "page-offset", defaultValue = GlobalVariables.PAGE_OFFSET_DEFAULT) Integer pageOffset,
            @RequestParam(value = "sort-property", defaultValue = GlobalVariables.SORT_PROPERTY_DEFAULT) String sortProperty,
            @RequestParam(value = "sort-direction", defaultValue = GlobalVariables.SORT_DIRECTION_DEFAULT) String sortDirection) {
        Page metadataFieldsPage = categoryService.getAllMetadataFields(pageSize, pageOffset,
                sortProperty, sortDirection);
        return metadataFieldResponseDtoPagedResourcesAssembler.toModel(metadataFieldsPage);
    }

    @Operation(summary = "Add category", description = "Admin can add a category. It can be a child category or a parent category. If no parent category id is passed then it is a parent category. If it is passed then it is a child category. Make sure that the parent category id that is passed doesn't have any product associated with it.")
    @PostMapping("/admin/add-category")
    public ResponseEntity addNewCategory(@Valid @RequestBody CategoryFieldDto categoryFIeldDto) {
        return categoryService.addNewCategory(categoryFIeldDto);
    }

    @Operation(summary = "View a category", description = "Admin can view a category details with its parent categories and child categories details by passing its category id", method = "GET")
    @GetMapping("/admin/categories/{id}")
    public ResponseEntity viewCategory(@PathVariable("id") Long id) {
        return categoryService.getCategory(id);
    }

    @Operation(summary = "View all categories", description = "Admin can view all the categories that he has created in the format of a page. Page specifications can be adjusted by passing the parameters.")
    @GetMapping("/admin/categories")
    public PagedModel viewAllCategories(
            @RequestParam(value = "page-size", defaultValue = GlobalVariables.PAGE_SIZE_DEFAULT) Integer pageSize,
            @RequestParam(value = "page-offset", defaultValue = GlobalVariables.PAGE_OFFSET_DEFAULT) Integer pageOffset,
            @RequestParam(value = "sort-property", defaultValue = GlobalVariables.SORT_PROPERTY_DEFAULT) String sortProperty,
            @RequestParam(value = "sort-direction", defaultValue = GlobalVariables.SORT_DIRECTION_DEFAULT) String sortDirection) {
        Page<CategoryDetailsDto> categoryDetailsPage = categoryService.viewAllCategories(pageSize,
                pageOffset, sortProperty, sortDirection);
        return categoryDetailsDtoPagedResourcesAssembler.toModel(categoryDetailsPage);
    }

    @Operation(summary = "Update Category", description = "Admin can update the category name by passing its id and the name that he wants to set for the category. It will check if there are no categories by the same name at root level and in its child categories tree.")
    @PutMapping("/admin/update-category")
    public ResponseEntity updateCategory(@Valid @RequestBody CategoryUpdateDto categoryUpdateDto) {
        return categoryService.updateCategory(categoryUpdateDto);
    }

    @Operation(summary = "Add Category Metadata Field Values", description = "Admin can add the category metadata field values by the combination of category id and metadata field id. It makes sure that no value is already set for the given combination of category id and metadata field id. Metadata field list and field values can be passed in a list.")
    @PostMapping("/admin/add/category-metadata")
    public ResponseEntity addCategoryMetadataValues(
            @Valid @RequestBody CategoryMetadataFieldValuesDto categoryMetadataFieldValuesDto) {
        return categoryService.addCategoryMetadataFieldValues(categoryMetadataFieldValuesDto);
    }

    @Operation(summary = "Update category metadata field values", description = "Admin can update the metadata field values that he has set for the combination of category id and metadata field id. Metadata field id and values can be passed in the format of a list")
    @PutMapping("/admin/update-category-metadata")
    public ResponseEntity updateCategoryMetadataValues(
            @Valid @RequestBody CategoryMetadataFieldValuesDto categoryMetadataFieldValuesDto) {
        return categoryService.updateMetadataFieldValues(categoryMetadataFieldValuesDto);
    }

    @Operation(summary = "View all categories for seller", description = "Seller can view all the leaf categories with their metadata fields and its possible field values and parent category chain details.")
    @GetMapping("/seller/categories")
    public ResponseEntity getAllCategoriesForSeller() {
        return categoryService.getAllCategoriesForSeller();
    }

    @Operation(summary = "View all categories for customer", description = "Customer can view all root level categories if he has passed no category id. Else if id is passed then list of its immediate child nodes is passed")
    //Optional Path variable : https://www.baeldung.com/spring-optional-path-variables
    @GetMapping(value = {"/customer/categories/{id}", "/customer/categories"})
    public ResponseEntity getAllCategoriesForCustomer(@PathVariable Optional<Long> id) {
        return categoryService.getAllCategoriesForCustomer(id);
    }

    @Operation(summary = "Filtering Details of a category", description = "Customer can view the filtering details of a category. It contains maximum and minimum price of the products in that category. Brand list from that category(if it is leaf), or from its child categories(if they are leaf) and it goes on like that. All metadata fields along with their values.")
    @GetMapping("/customer/filtering-details/{id}")
    public ResponseEntity getFilteringDetails(@PathVariable("id") Long id) {
        return categoryService.getFilteringDetails(id);
    }
}
