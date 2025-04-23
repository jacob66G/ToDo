package com.example.ToDo.repositories;

import com.example.ToDo.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByUser_Username(String userName);

    @Query("SELECT COUNT(*) > 0 FROM Task t WHERE t.user.username = :username AND LOWER(t.title) = LOWER(:title)")
    boolean existsByUser_UsernameAndTitle(@Param("username") String username, @Param("title") String title);

    @Query("SELECT COUNT(*) > 0 FROM Task t WHERE t.user.username = :username AND t.category.id = :categoryId")
    boolean existsByUser_UsernameAndCategory_Id(@Param("username") String username, @Param("categoryId") Long categoryId);

    @Query("SELECT COUNT(*) > 0 FROM Task t WHERE t.user.username = :username AND t.status.id = :statusId")
    boolean existsByUser_UsernameAndStatus_Id(@Param("username") String username, @Param("categoryId") Long statusId);
}
