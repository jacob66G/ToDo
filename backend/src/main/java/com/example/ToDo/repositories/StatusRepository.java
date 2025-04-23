package com.example.ToDo.repositories;

import com.example.ToDo.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {
    List<Status> findAllByUser_Username(String username);

    Optional<Status> findByName(String name);

    @Query("SELECT COUNT(*) > 0 FROM Status s WHERE s.user.username = :username AND LOWER(s.name) = LOWER(:name)")
    boolean existsByUser_UsernameAndName(@Param("username") String username, @Param("name") String name);
}
