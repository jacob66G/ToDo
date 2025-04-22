package com.example.ToDo.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String email;

    private String password;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private Set<Task> tasks;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Set<Category> categories;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Set<Status> statuses;

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public void addCategory(Category category) {

        if(categories == null) {
            categories = new HashSet<>();
        }

        categories.add(category);
        category.setUser(this);
    }

    public void addStatus(Status status) {

        if(statuses == null) {
            statuses = new HashSet<>();
        }

        statuses.add(status);
        status.setUser(this);
    }

    public void addTask(Task task) {

        if(tasks == null) {
            tasks = new HashSet<>();
        }

        tasks.add(task);
        task.setUser(this);
    }
}
