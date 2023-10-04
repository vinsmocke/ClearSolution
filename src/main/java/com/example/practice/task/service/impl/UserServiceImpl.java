package com.example.practice.task.service.impl;

import com.example.practice.task.exception.InvalidEmailException;
import com.example.practice.task.exception.NullEntityReferenceException;
import com.example.practice.task.model.User;
import com.example.practice.task.repository.UserRepository;
import com.example.practice.task.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public User create(User user) {
        if (userRepository.existsUserByEmail(user.getEmail())) {
            throw new InvalidEmailException("User with email " + user.getEmail() + " already exist!");
        }
        return userRepository.save(user);
    }

    @Override
    public User readById(long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User with id " + id + " not found!"));
    }

    @Override
    public User update(User user) {
        if (user != null) {
            readById(user.getId());
            return userRepository.save(user);
        }
        throw new NullEntityReferenceException("User cannot be null!");
    }

    @Override
    public void delete(long id) {
        User user = readById(id);
        userRepository.delete(user);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getByBirthDate(LocalDate from, LocalDate to) {
        int value = from.compareTo(to);
        if (value < 0) {
            return userRepository.findAll().stream()
                    .filter(user -> user.getBirthDate().isAfter(from.minusDays(1))
                            && user.getBirthDate().isBefore(to.plusDays(1)))
                    .sorted(Comparator.comparing(User::getBirthDate))
                    .collect(Collectors.toList());
        }
        throw new IllegalArgumentException("The Start date should be less than the End date");
    }
}
