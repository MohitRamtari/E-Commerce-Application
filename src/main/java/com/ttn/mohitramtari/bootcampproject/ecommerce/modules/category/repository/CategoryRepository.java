package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.repository;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.parentCategory IS NULL AND lower(c.categoryName)=:name")
    Category existsByCategoryNameAndNoParent(String name);

    @Query("SELECT c FROM Category c WHERE c.childCategoriesSet IS EMPTY")
    List<Category> getAllLeafCategories();

    @Query(value = "SELECT * FROM categories WHERE parent_category_id=:id", nativeQuery = true)
    List<Category> getAllImmediateChildCategories(Long id);

    @Query(value = "SELECT * from categories WHERE parent_category_id is NULL", nativeQuery = true)
    List<Category> getAllRootCategories();
}
