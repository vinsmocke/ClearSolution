package com.example.practice.task.controller;

import com.example.practice.task.dto.UserDto;
import com.example.practice.task.dto.UserTransformer;
import com.example.practice.task.model.User;
import com.example.practice.task.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAll();
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody UserDto userDto) {
        return userService.create(UserTransformer.convertToEntity(userDto));
    }
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public User update(@PathVariable("id") long id, @Valid @RequestBody UserDto userDto) {
        User user = userService.readById(id);
        return userService.update(UserTransformer.convertToEntity(userDto, user));
    }
    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") long id) {
        return userService.readById(id);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<String> delete(@PathVariable("id") long id) {
        userService.delete(id);
        return ResponseEntity.accepted().body("User with id " + id + " has been removed");
    }
    @GetMapping("/range")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<User> findUserByBirthDateRange(@RequestParam LocalDate start, @RequestParam LocalDate end) {
        return userService.getByBirthDate(start, end);
    }
}
