package com.example.practice.task.service;

import com.example.practice.task.exception.InvalidEmailException;
import com.example.practice.task.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;
    private User user;

    @BeforeEach
    void init() {
        user = new User();
        user.setFirstName("Nick");
        user.setLastName("Fury");
        user.setEmail("nick@mail.com");
        user.setPhoneNumber("+380505554466");
        user.setBirthDate(LocalDate.of(2003, 5, 25));
        user.setAddress("Kyiv");
    }

    @Test
    @Transactional
    void testCreateValidUser() {
        userService.create(user);

        assertEquals(1, userService.getAll().size());
    }

    @Test
    @Transactional
    void testCreateUserWithExistingEmail() {
        userService.create(user);
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setFirstName("New");
        newUser.setLastName("User");
        newUser.setPhoneNumber("+380505556644");
        newUser.setBirthDate(user.getBirthDate());

        assertThrows(InvalidEmailException.class, () -> userService.create(newUser));
    }

    @Test
    @Transactional
    void testReadById() {
        userService.create(user);
        User userBD = userService.readById(user.getId());

        assertEquals(user.getFirstName(), userBD.getFirstName());
        assertEquals(user, userBD);
    }

    @Test
    @Transactional
    void testUpdateUser() {
        userService.create(user);
        user.setFirstName("New");
        user.setLastName("Name");
        user.setAddress("new address");
        userService.update(user);

        User userDB = userService.readById(user.getId());

        assertEquals(user.getFirstName(), userDB.getFirstName());
        assertEquals(user.getLastName(), userDB.getLastName());
        assertEquals(user.getAddress(), userDB.getAddress());
    }

    @Test
    @Transactional
    void testDeleteUser() {
        User newUser = new User();
        newUser.setFirstName("New");
        newUser.setLastName("User");
        newUser.setEmail("newUser@mail.com");
        newUser.setPhoneNumber("+380505556644");
        newUser.setBirthDate(user.getBirthDate());
        userService.create(user);
        userService.create(newUser);

        assertEquals(2, userService.getAll().size());
        userService.delete(newUser.getId());
        assertEquals(1, userService.getAll().size());
    }

    @Test
    @Transactional
    void testGetAllUsers() {
        User newUser = new User();
        newUser.setFirstName("New");
        newUser.setLastName("User");
        newUser.setEmail("newUser@mail.com");
        newUser.setPhoneNumber("+380505556644");
        newUser.setBirthDate(user.getBirthDate());

        userService.create(user);
        userService.create(newUser);

        List<User> users = userService.getAll();

        assertEquals(2, users.size());
    }

    @ParameterizedTest
    @MethodSource("provideUsers")
    @Transactional
    void testGetByBirthDate(List<User> users) {
        users.forEach(user1 -> userService.create(user1));
        LocalDate start = LocalDate.of(2000, 6, 1);
        LocalDate end = LocalDate.of(2004, 6, 1);

        List<User> usersByBirthDate = userService.getByBirthDate(start, end);

        assertEquals(2, usersByBirthDate.size());
        assertEquals("2002-06-22", usersByBirthDate.get(0).getBirthDate().toString());
        assertEquals("2004-05-07", usersByBirthDate.get(1).getBirthDate().toString());
    }

    private static Stream<Arguments> provideUsers() {
        User user1 = new User();
        user1.setFirstName("New");
        user1.setLastName("User");
        user1.setEmail("newUser1@mail.com");
        user1.setPhoneNumber("+380505556644");
        user1.setBirthDate(LocalDate.of(2004, 5, 7));
        User user2 = new User();
        user2.setFirstName("New");
        user2.setLastName("User");
        user2.setEmail("newUser2@mail.com");
        user2.setPhoneNumber("+380505556644");
        user2.setBirthDate(LocalDate.of(2002, 6, 22));

        User user3 = new User();
        user3.setFirstName("New");
        user3.setLastName("User");
        user3.setEmail("newUser3@mail.com");
        user3.setPhoneNumber("+380505556644");
        user3.setBirthDate(LocalDate.of(2000, 1, 12));


        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);
        return Stream.of(Arguments.of(
                users
        ));
    }
}
