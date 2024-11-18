package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.service.impl;

import com.ttn.mohitramtari.bootcampproject.ecommerce.app.exception.CommonValidationFailedException;
import com.ttn.mohitramtari.bootcampproject.ecommerce.app.exception.ResourceAlreadyExistException;
import com.ttn.mohitramtari.bootcampproject.ecommerce.app.exception.ResourcesNotFoundException;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.dto.*;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.dto.category_view.CategoryDetailsDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.model.Category;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.model.CategoryMetadataField;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.model.CategoryMetadataFieldValues;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.model.CategoryMetadataFieldValuesId;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.repository.CategoryMetadataFieldRepository;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.repository.CategoryMetadataFieldValuesRepository;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.repository.CategoryRepository;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.service.CategoryService;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.model.Product;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.repository.ProductRepository;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.repository.ProductVariationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryMetadataFieldRepository categoryMetadataFieldRepository;

    @Autowired
    MessageSource messageSource;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CategoryMetadataFieldValuesRepository categoryMetadataFieldValuesRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductVariationRepository productVariationRepository;

    /**
     * Method to add a new metadata field to the repository. Taking input in the form of
     * MetadataFieldDto where it has only one property "name". If a metadata field already exist by
     * the same name then throwing an error.
     *
     * @param metadataFieldDto
     * @return Success message stating that metadata field has been saved
     */
    @Override
    public ResponseEntity<String> addNewMetadataField(MetadataFieldDto metadataFieldDto) {
        if (Boolean.TRUE.equals(
                categoryMetadataFieldRepository.existsByName(metadataFieldDto.getName()))) {
            throw new ResourceAlreadyExistException(
                    messageSource.getMessage("metadata.already.exist", null,
                            LocaleContextHolder.getLocale()));
        }

        CategoryMetadataField categoryMetadataField = modelMapper.map(metadataFieldDto,
                CategoryMetadataField.class);
        categoryMetadataFieldRepository.save(categoryMetadataField);
        return ResponseEntity.ok(
                messageSource.getMessage("metadata.success", null, LocaleContextHolder.getLocale())
                        + categoryMetadataField.getId());
    }

    /**
     * Method to return all the metadata fields stored in the database in page form. Takes input of
     * the page fields
     *
     * @param pageSize
     * @param pageOffset
     * @param sortProperty
     * @param sortDirection
     * @return All metadata field names with their ids in the form of a page.
     * @throws throws an exception when there are no metadata fields in the repository
     */
    @Override
    public Page<MetadataFieldResponseDto> getAllMetadataFields(Integer pageSize, Integer pageOffset,
                                                               String sortProperty, String sortDirection) {

        Sort.Direction sortingDirection =
                sortDirection.equalsIgnoreCase("ASC") ? Direction.ASC : Direction.DESC;
        Page categoryMetadataFields = categoryMetadataFieldRepository.findAll(
                PageRequest.of(pageOffset, pageSize, sortingDirection, sortProperty));

        if (categoryMetadataFields.isEmpty()) {
            throw new ResourcesNotFoundException("There are no metadata fields available");
        }

        return categoryMetadataFields.map(
                metadataField -> modelMapper.map(metadataField, MetadataFieldResponseDto.class));
    }

    /**
     * Method to add a new category to the database
     *
     * @param categoryFIeldDto
     * @return
     */
    @Override
    public ResponseEntity<String> addNewCategory(CategoryFieldDto categoryFIeldDto) {

        //Checking if there is a root category with the same name
        Category categoryCheck = categoryRepository.existsByCategoryNameAndNoParent(
                categoryFIeldDto.getCategoryName().toLowerCase());
        if (categoryCheck != null) {
            throw new ResourceAlreadyExistException(
                    messageSource.getMessage("category.already.exist", null,
                            LocaleContextHolder.getLocale()));
        }

        //Creating a new category and setting its parent null for now
        Category newCategory = new Category();
        Optional<Category> parentCategory;

        //if user has passed parent category id then go inside this method
        if (categoryFIeldDto.getParentCategoryId() != null) {

            //Find the parent category in the database by the id passed by the user
            //if no parent category exist then throw exception
            parentCategory = categoryRepository.findById(categoryFIeldDto.getParentCategoryId());
            if (parentCategory == null) {
                throw new ResourcesNotFoundException(
                        messageSource.getMessage("no.parent.category.found", null,
                                LocaleContextHolder.getLocale()));
            }

            List<Product> products = productRepository.findByProductId(parentCategory.get().getId());
            if (products.size() != 0)
                throw new ResourcesNotFoundException("Given parent category has a product associated with it. That means it is already a leaf category, so it can't become a parent category.");

            //Checking if there is a category by the same name in the child tree of that parent category
            checkCategoryUniquenessAtBreadthAndDepthLevel(categoryFIeldDto.getCategoryName(),
                    parentCategory.get());

            //Setting the parent category and adding the child category in the set of that particular
            //parent category
            newCategory.setParentCategory(parentCategory.get());
            parentCategory.get().addChildCategory(newCategory);
        }

        //Setting the name of the category
        newCategory.setCategoryName(categoryFIeldDto.getCategoryName());

        //Then saving the category in the database
        categoryRepository.save(newCategory);
        return ResponseEntity.ok(
                messageSource.getMessage("category.added", null, LocaleContextHolder.getLocale())
                        + newCategory.getId());
    }

    /**
     * Method to retrieve a category from the database.
     *
     * @param categoryId
     * @return Category details in the form of a dto
     */
    @Override
    public ResponseEntity<CategoryDetailsDto> getCategory(Long categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            throw new ResourcesNotFoundException(
                    messageSource.getMessage("no.category.found", null, LocaleContextHolder.getLocale()));
        }

        CategoryDetailsDto categoryDetailsDto = modelMapper.map(category.get(),
                CategoryDetailsDto.class);
        return ResponseEntity.ok(categoryDetailsDto);
    }

    /**
     * Method to retrieve all the categories available in the form of a page. First, getting all the
     * categories from the repository in the form of a list then converting it to dto and then
     * converting that list to a page.
     *
     * @param pageSize
     * @param pageOffset
     * @param sortProperty
     * @param sortDirection
     * @return categories in the form of a page
     */
    @Override
    public Page viewAllCategories(Integer pageSize, Integer pageOffset, String sortProperty,
                                  String sortDirection) {
        List<Category> categoryList = categoryRepository.findAll();
        List<CategoryDetailsDto> categoryDetailsDtoList = categoryList.stream()
                .map(category -> modelMapper.map(category, CategoryDetailsDto.class))
                .collect(Collectors.toList());
        return convertListToPage(categoryDetailsDtoList, pageSize, pageOffset, sortProperty,
                sortDirection);
    }

    /**
     * Method to update the category name. Details need to be passed in the format of
     * CategoryUpdateDto. It contains two properties id and categoryName. CategoryName must be unique
     * among all the root level category names and must be unique inside the tree of its parent
     * category
     *
     * @param categoryUpdateDto
     * @return
     */
    @Override
    public ResponseEntity<String> updateCategory(CategoryUpdateDto categoryUpdateDto) {
        //Retrieving the category from the repository
        Category category = categoryRepository.findById(categoryUpdateDto.getId()).orElseThrow(() -> new ResourcesNotFoundException(
                messageSource.getMessage("no.category.found", null, LocaleContextHolder.getLocale())));

        //Checking if a category already exist by the same name that we are passing in the dto at the root level
        if (categoryRepository.existsByCategoryNameAndNoParent(categoryUpdateDto.getCategoryName())
                != null) {
            throw new ResourceAlreadyExistException(
                    messageSource.getMessage("root.category.already.exist", null,
                            LocaleContextHolder.getLocale()));
        }

        //Checking if a category already exist by the same name that we are updating by,
        //at the child level of the parent
        checkCategoryUniquenessAtBreadthAndDepthLevel(categoryUpdateDto.getCategoryName(), category);

        //Setting up the new name of the category
        category.setCategoryName(categoryUpdateDto.getCategoryName());

        //Saving it in the database
        categoryRepository.save(category);

        return ResponseEntity.ok(
                messageSource.getMessage("category.updated", null, LocaleContextHolder.getLocale()));
    }

    @Override
    public ResponseEntity<String> addCategoryMetadataFieldValues(
            CategoryMetadataFieldValuesDto metadataFieldValuesDto) {

        if (metadataFieldValuesDto.getFieldValuesList().size() != metadataFieldValuesDto.getMetadataFieldIdList().size()) {
            throw new ResourcesNotFoundException("You haven't provided values for all the fields");
        }

        //Retrieving the category and category metadata field from the repositories.
        Category category = categoryRepository.findById(
                metadataFieldValuesDto.getCategoryId()).orElseThrow(() -> new ResourcesNotFoundException(messageSource.getMessage("no.category.found", null, LocaleContextHolder.getLocale())));

        //if category does have child categories, that means it is a parent category
        //And there is no sense to add field values to a parent category
        //So throwing an exception
        if (!category.getChildCategoriesSet().isEmpty()) {
            throw new CommonValidationFailedException(
                    messageSource.getMessage("no.leaf.category", null, LocaleContextHolder.getLocale()));
        }

        List<Optional<CategoryMetadataField>> categoryMetadataFieldList = new ArrayList<>();
        for (int i = 0; i < metadataFieldValuesDto.getMetadataFieldIdList().size(); i++)
            categoryMetadataFieldList.add(categoryMetadataFieldRepository.findById(metadataFieldValuesDto.getMetadataFieldIdList().get(i)));

        //Throwing an exception if no metadata field found
        for (int i = 0; i < categoryMetadataFieldList.size(); i++) {
            System.out.println(categoryMetadataFieldList.get(i));
            if (categoryMetadataFieldList.get(i).isEmpty()) {
                throw new ResourcesNotFoundException(messageSource.getMessage("no.metadata.field.found", null,
                        LocaleContextHolder.getLocale()) + ": " + metadataFieldValuesDto.getMetadataFieldIdList().get(i));
            }
        }

        List<String[]> metadataFieldValuesList = new ArrayList<>();
        for (int i = 0; i < metadataFieldValuesDto.getFieldValuesList().size(); i++) {
            if (metadataFieldValuesDto.getFieldValuesList().get(i).isEmpty())
                throw new ResourcesNotFoundException("You haven't provided any field values for the field : " + categoryMetadataFieldList.get(i).get().getName());
            String[] fieldValues = metadataFieldValuesDto.getFieldValuesList().get(i).split(",");
            if (Arrays.stream(fieldValues).distinct().count() != fieldValues.length) {
                throw new ResourcesNotFoundException("You haven't provided unique values for the field : " + categoryMetadataFieldList.get(i).get().getName());
            }
            metadataFieldValuesList.add(fieldValues);
        }


        CategoryMetadataFieldValuesId categoryMetadataFieldValuesId = new CategoryMetadataFieldValuesId();
        categoryMetadataFieldValuesId.setCategoryId(metadataFieldValuesDto.getCategoryId());

        for (int i = 0; i < metadataFieldValuesDto.getMetadataFieldIdList().size(); i++) {
            categoryMetadataFieldValuesId.setCategoryMetadataFieldId(metadataFieldValuesDto.getMetadataFieldIdList().get(i));
            if (categoryMetadataFieldValuesRepository.findById(categoryMetadataFieldValuesId).isPresent()) {
                throw new ResourceAlreadyExistException(
                        messageSource.getMessage("field.values.already.exist", null,
                                LocaleContextHolder.getLocale()) + " : Category Id : " + category.getId() + " And " + "Metadata Field id : " + categoryMetadataFieldValuesId.getCategoryMetadataFieldId());
            }
        }

        for (int i = 0; i < categoryMetadataFieldList.size(); i++) {
            CategoryMetadataFieldValues categoryMetadataFieldValues = new CategoryMetadataFieldValues();
            categoryMetadataFieldValues.setCategory(category);
            categoryMetadataFieldValues.setCategoryMetadataField(categoryMetadataFieldList.get(i).get());
            categoryMetadataFieldValues.setMetadataFieldValues(metadataFieldValuesDto.getFieldValuesList().get(i));
            categoryMetadataFieldValuesRepository.save(categoryMetadataFieldValues);
        }

        return ResponseEntity.ok(
                messageSource.getMessage("field.values.success", null, LocaleContextHolder.getLocale()));
    }

    /**
     * Updating the metadata field values
     *
     * @param metadataFieldValuesDto
     * @return
     */
    @Override
    public ResponseEntity<String> updateMetadataFieldValues(
            CategoryMetadataFieldValuesDto metadataFieldValuesDto) {

        if (metadataFieldValuesDto.getFieldValuesList().size() != metadataFieldValuesDto.getMetadataFieldIdList().size()) {
            throw new ResourcesNotFoundException("You haven't provided values for all the fields");
        }
        //Retrieving category and category metadata field from the database
        Category category = categoryRepository.findById(
                metadataFieldValuesDto.getCategoryId()).orElseThrow(() -> new ResourcesNotFoundException(messageSource.getMessage("no.category.found", null, LocaleContextHolder.getLocale())));

        //if category doesn't have child categories, that means it is a parent category
        //And there is no sense to add field values to a parent category
        //So throwing an exception
        if (!category.getChildCategoriesSet().isEmpty()) {
            throw new CommonValidationFailedException(
                    messageSource.getMessage("no.leaf.category", null, LocaleContextHolder.getLocale()));
        }

        //If no values are set for the combination of category id and metadata field id then throw an exception
        for (int i = 0; i < metadataFieldValuesDto.getMetadataFieldIdList().size(); i++) {
            if (categoryMetadataFieldValuesRepository.findByCategoryIdAndCategoryMetadataFieldId(category.getId(), metadataFieldValuesDto.getMetadataFieldIdList().get(i)) == null)
                throw new ResourcesNotFoundException("There is no Field values set for the combination of category id : " + category.getId() + " and Metadata Field id : " + metadataFieldValuesDto.getMetadataFieldIdList().get(i));
        }

        List<Optional<CategoryMetadataField>> categoryMetadataFieldList = new ArrayList<>();
        for (int i = 0; i < metadataFieldValuesDto.getMetadataFieldIdList().size(); i++)
            categoryMetadataFieldList.add(categoryMetadataFieldRepository.findById(metadataFieldValuesDto.getMetadataFieldIdList().get(i)));

        //if metadata field is empty then throw an exception
        for (int i = 0; i < categoryMetadataFieldList.size(); i++) {
            if (categoryMetadataFieldList.get(i).isEmpty()) {
                throw new ResourcesNotFoundException(messageSource.getMessage("no.metadata.field.found", null,
                        LocaleContextHolder.getLocale()) + ": " + metadataFieldValuesDto.getMetadataFieldIdList().get(i));
            }
        }

        List<String[]> newMetadataFieldValuesList = new ArrayList<>();
        for (int i = 0; i < metadataFieldValuesDto.getFieldValuesList().size(); i++) {
            if (metadataFieldValuesDto.getFieldValuesList().get(i).isEmpty())
                throw new ResourcesNotFoundException("You haven't provided any field values for the field : " + categoryMetadataFieldList.get(i).get().getName());
            String[] fieldValues = metadataFieldValuesDto.getFieldValuesList().get(i).split(",");
            if (Arrays.stream(fieldValues).distinct().count() != fieldValues.length) {
                throw new ResourcesNotFoundException("You haven't provided unique values for the field : " + categoryMetadataFieldList.get(i).get().getName());
            }
            newMetadataFieldValuesList.add(fieldValues);
        }

        CategoryMetadataFieldValuesId categoryMetadataFieldValuesId = new CategoryMetadataFieldValuesId();
        categoryMetadataFieldValuesId.setCategoryId(metadataFieldValuesDto.getCategoryId());

        List<CategoryMetadataFieldValues> oldFieldValues = new ArrayList<>();
        for (int i = 0; i < metadataFieldValuesDto.getMetadataFieldIdList().size(); i++) {
            oldFieldValues.add(categoryMetadataFieldValuesRepository.findByCategoryIdAndCategoryMetadataFieldId(category.getId(), metadataFieldValuesDto.getMetadataFieldIdList().get(i)));
        }

        //Then combining the old and new values
        Map<Long, Set<String>> commonFieldValuesMap = new HashMap<>();
        for (int i = 0; i < oldFieldValues.size(); i++) {
            Set<String> commonValues = new HashSet();
            commonValues.addAll(List.of(oldFieldValues.get(i).getMetadataFieldValues().split(",")));
            commonValues.addAll(List.of(newMetadataFieldValuesList.get(i)));
            commonFieldValuesMap.put(oldFieldValues.get(i).getId().getCategoryMetadataFieldId(), commonValues);
        }

        List<String> commonFieldValuesList = new ArrayList<>();
        for (Long i : metadataFieldValuesDto.getMetadataFieldIdList()) {
            commonFieldValuesList.add(String.join(",", commonFieldValuesMap.get(i)));
        }

        for (int i = 0; i < commonFieldValuesList.size(); i++) {
            oldFieldValues.get(i).setMetadataFieldValues(commonFieldValuesList.get(i));
            categoryMetadataFieldValuesRepository.save(oldFieldValues.get(i));
        }

        return ResponseEntity.ok(messageSource.getMessage("field.values.updated", null, LocaleContextHolder.getLocale()));
    }

    /**
     * Retrieving list of all leaf categories for the seller
     *
     * @return
     */
    @Override
    public ResponseEntity getAllCategoriesForSeller() {
        //Getting all leaf categories from the repository
        List<Category> leafCategoryList = categoryRepository.getAllLeafCategories();

        //Converting it to dto
        List<CategoryDetailsForSellerDto> leafCategoryListDto = leafCategoryList.stream()
                .map(leafCategory -> modelMapper.map(leafCategory, CategoryDetailsForSellerDto.class))
                .collect(Collectors.toList());

        //Returning it
        return ResponseEntity.ok(leafCategoryListDto);
    }

    /**
     * Method to retrieve list of all root level categories if no id is passed and if id is passed
     * then list of its immediate child nodes is passed
     *
     * @param id
     * @return
     */
    @Override
    public ResponseEntity getAllCategoriesForCustomer(Optional<Long> id) {

        //if no id is passed in the url then showing the list of all the leaf categories
        if (id.isEmpty()) {
            List<Category> rootCategoriesList = categoryRepository.getAllRootCategories();
            List<RootCategoriesDetailsDto> rootCategoriesDtoList = rootCategoriesList.stream()
                    .map(rootCategory -> modelMapper.map(rootCategory, RootCategoriesDetailsDto.class))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(rootCategoriesDtoList);
        }
        //Now if id is passed then following code will run
        else {

            //Checking if the provided id exist or not in the database
            if (!categoryRepository.existsById(id.get())) {
                throw new ResourcesNotFoundException(
                        messageSource.getMessage("no.category.found", null, LocaleContextHolder.getLocale()));
            }

            //getting all the immediate child categories of the given id
            List<Category> immediateChildCategoriesList = categoryRepository.getAllImmediateChildCategories(
                    id.get());

            //Throwing an exception if there are no child categories
            if (immediateChildCategoriesList.isEmpty()) {
                throw new ResourcesNotFoundException(
                        messageSource.getMessage("no.immediate.categories", null,
                                LocaleContextHolder.getLocale()));
            }

            //Converting the child categories list to dto list and then returning it
            List<ImmediateChildCategoriesDto> immediateChildCategoriesListDto = immediateChildCategoriesList.stream()
                    .map(immediateChildCategory -> modelMapper.map(immediateChildCategory,
                            ImmediateChildCategoriesDto.class)).collect(Collectors.toList());
            return ResponseEntity.ok(immediateChildCategoriesListDto);
        }
    }

    /**
     * Method to retrieve filtering details for a category.
     *
     * @param id
     * @return All metadata fields with their possible values.
     * @return List of brands in that category and its child categories
     * @return Minimum and Maximum price in that category
     */
    @Override
    public ResponseEntity getFilteringDetails(Long id) {
        //getting List of metadata fields id from the database
        List<Long> metadataFieldsId = categoryMetadataFieldValuesRepository.findMetadataFieldsByCategoryId(
                id);

        //Getting List of their corresponding metadata field values
        List<String> metadataFieldValues = categoryMetadataFieldValuesRepository.findMetadataValuesByCategoryId(
                id);

        //Making a list to get the field names from the ids retrieved above
        List<String> metadataFieldsName = new ArrayList<>();

        //Populating it using the field ids
        for (Long fieldId : metadataFieldsId) {
            metadataFieldsName.add(categoryMetadataFieldRepository.findFieldNameById(fieldId));
        }

        //Map of category metadata field names and their values
        Map<String, String> map = new HashMap<>();

        //Populating it
        for (int i = 0; i < metadataFieldsName.size(); i++) {
            map.put(metadataFieldsName.get(i), metadataFieldValues.get(i));
        }

        //Now the 2nd work to do is retrieve brands list
        //To do that first work to do is retrieve all the leaf categories of the category passed
        //First thing doing is getting parent category
        Optional<Category> parentCategory = categoryRepository.findById(id);

        //Throwing an exception if category is not found
        if (parentCategory.isEmpty()) {
            throw new ResourcesNotFoundException("No Category found for the given id");
        }

        //Retrieving all the leaf categories
        Set<Category> visitedSubCategories = new HashSet<>();
        Deque<Category> categoryStack = new LinkedList<>();
        Set<Category> leafCategories = new HashSet<>();

        //Pushing the parent category into the stack
        categoryStack.push(parentCategory.get());

        //Then getting a list of all the leaf categories
        List<Category> leafCategoriesList = categoryRepository.getAllLeafCategories();

        //Step by step visiting all the categories and if any category is a leaf category then pushing
        //it to leaf categories list
        while (!categoryStack.isEmpty()) {
            Category currentCategory = categoryStack.pop();

            if (!visitedSubCategories.contains(currentCategory)) {
                visitedSubCategories.add(currentCategory);
                if (leafCategoriesList.contains(currentCategory)) {
                    leafCategories.add(currentCategory);
                }
            }

            Set<Category> subCategories = currentCategory.getChildCategoriesSet();
            for (Category category : subCategories) {
                if (!visitedSubCategories.contains(category)) {
                    categoryStack.push(category);
                }
            }
        }

        //Created a list of brands and productId
        //Brand list is for the second task given
        List<String> brandList = new ArrayList<>();
        List<Long> productIdList = new ArrayList<>();

        //Getting all the product ids and brand names
        for (Category category : leafCategories) {
            brandList.addAll(productRepository.getBrandNameById(category.getId()));
            productIdList.addAll(productRepository.getProductIdById(category.getId()));
        }

        List price = new ArrayList();
        //Getting price of all the variations of that product for which id is passed
        for (Long productId : productIdList) {
            price.addAll(productVariationRepository.findPrice(productId));
        }

        //if price list is empty that means there were no products in the category
        if (price.isEmpty()) {
            throw new ResourcesNotFoundException("No product exist in the category");
        }

        //Creating a map to send the response to the user
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("Metadata Fields and Response", map);
        result.put("Brand List", brandList);
        result.put("Maximum Price", Collections.max(price));
        result.put("Minimum Price", Collections.min(price));
        return ResponseEntity.ok(result);
    }

    private void checkCategoryUniquenessAtBreadthAndDepthLevel(String categoryName,
                                                               Category parentCategory) {
        Set<Category> visitedSubCategories = new HashSet<>();
        Deque<Category> categoryStack = new LinkedList<>();
        categoryStack.push(parentCategory);

        while (!categoryStack.isEmpty()) {
            Category currentCategory = categoryStack.pop();

            if (!visitedSubCategories.contains(currentCategory)) {
                visitedSubCategories.add(currentCategory);
                if (currentCategory.getCategoryName().equalsIgnoreCase(categoryName)) {
                    throw new ResourceAlreadyExistException(
                            messageSource.getMessage("category.already.exist", null,
                                    LocaleContextHolder.getLocale()));
                }
            }

            Set<Category> subCategories = currentCategory.getChildCategoriesSet();
            for (Category category : subCategories) {
                if (!visitedSubCategories.contains(category)) {
                    categoryStack.push(category);
                }
            }
        }
    }

    Page convertListToPage(List listToConvert, Integer pageSize, Integer pageOffset,
                           String sortProperty, String sortDirection) {
        Sort.Direction sortingDirection =
                sortDirection.equalsIgnoreCase("ASC") ? Direction.ASC : Direction.DESC;
        Pageable pageable = PageRequest.of(pageOffset, pageSize, sortingDirection, sortProperty);
        int start = Math.min((int) pageable.getOffset(), listToConvert.size());
        int end = Math.min((start + pageable.getPageSize()), listToConvert.size());
        Page page = new PageImpl<>(listToConvert.subList(start, end), pageable, listToConvert.size());
        return page;
    }
}