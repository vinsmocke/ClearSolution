package com.example.practice.task.service;

import com.example.practice.task.model.User;

import java.time.LocalDate;
import java.util.List;

public interface UserService {
    User create(User user);

    User readById(long id);

    User update(User user);

    void delete(long id);

    List<User> getAll();

    List<User> getByBirthDate(LocalDate from, LocalDate to);
}
