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
    @Query("SELECT s FROM Status s WHERE s.user.email = :email")
    List<Status> findAllByUser(@Param("email") String email);

    Optional<Status> findByName(String name);

    @Query("SELECT s FROM Status s WHERE s.user.email = :email AND LOWER(s.name) = LOWER(:name) AND (:statusId IS NULL OR s.id != :statusId)")
    List<Status> findByUserAndName(@Param("email") String email, @Param("name") String name, @Param("statusId") Long statusId);
}
