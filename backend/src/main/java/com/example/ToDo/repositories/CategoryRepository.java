package com.example.ToDo.repositories;

import com.example.ToDo.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.user.email = :userName")
    List<Category> findAllByUser_UserEmail(String userName);

    @Query("SELECT COUNT(*) > 0 FROM Category c WHERE c.user.username = :username AND  LOWER(c.name) = LOWER(:name)")
    boolean existsByUser_UsernameAndName(@Param("username") String username, @Param("name") String name);
}
