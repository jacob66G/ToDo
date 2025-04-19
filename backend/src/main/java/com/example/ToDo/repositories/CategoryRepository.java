package com.example.ToDo.repositories;

import com.example.ToDo.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByUser_Username(String userName);

    @Query("SELECT COUNT(*) > 0 FROM Category c WHERE LOWER(c.name) = LOWER(:name) AND c.user.username = :username")
    Boolean existsByNameAndUser_Username(@Param("name") String name, @Param("username") String username);
}
