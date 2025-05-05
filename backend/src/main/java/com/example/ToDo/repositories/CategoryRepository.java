package com.example.ToDo.repositories;

import com.example.ToDo.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.user.email = :email")
    List<Category> findAllByUser(@Param("email") String email);

    @Query("SELECT c FROM Category c WHERE c.user.email = :email AND  LOWER(c.name) = LOWER(:name) AND (:categoryId IS NULL OR c.id != :categoryId)")
    List<Category> findByUserAndName(@Param("email") String email, @Param("name") String name, @Param("categoryId") Long categoryId);
}
