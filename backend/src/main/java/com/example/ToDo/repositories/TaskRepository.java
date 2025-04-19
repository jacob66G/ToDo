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

    @Query("SELECT COUNT(*) > 0 FROM Task t WHERE LOWER(t.title) = LOWER(:title) AND t.user.username = :username")
    Boolean existsByTitleAndUser_Username(@Param("title") String title, @Param("username") String username);


}
