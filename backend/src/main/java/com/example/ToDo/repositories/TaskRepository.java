package com.example.ToDo.repositories;

import com.example.ToDo.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT t FROM Task t WHERE t.user.email = :email")
    List<Task> findAllByUser(@Param("email") String email);

    @Query("SELECT t FROM Task t WHERE t.user.email = :email AND LOWER(t.title) = LOWER(:title) AND (:taskId IS NULL OR t.id != :taskId)")
    List<Task> findByUserAndTitle(@Param("email") String email, @Param("title") String title, @Param("taskId") Long taskId);

    @Query("SELECT t FROM Task t WHERE t.user.email = :email AND t.category.id = :categoryId")
    List<Task> findByUserAndCategory_Id(@Param("email") String email, @Param("categoryId") Long categoryId);

    @Query("SELECT t FROM Task t WHERE t.user.email = :email AND t.status.id = :statusId")
    List<Task> findByUserAndStatus_Id(@Param("email") String email, @Param("statusId") Long statusId);
}
