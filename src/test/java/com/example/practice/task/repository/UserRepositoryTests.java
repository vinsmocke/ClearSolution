package com.example.practice.task.repository;

import com.example.practice.task.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;
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
    void testCreateUser() {
        User createUser = new User();
        createUser.setFirstName("New");
        createUser.setLastName("User");
        createUser.setEmail("newUser@mail.com");
        createUser.setPhoneNumber("+380505556644");
        createUser.setBirthDate(user.getBirthDate());

        userRepository.save(createUser);

        assertEquals(1, userRepository.findAll().size());
        assertEquals("New", userRepository.findById(createUser.getId()).get().getFirstName());
    }
    @Test
    @Transactional
    void testRemoveUser() {
        userRepository.save(user);

        assertEquals(1, userRepository.findAll().size());

        userRepository.delete(user);
        assertEquals(0, userRepository.findAll().size());
    }
    @Test
    @Transactional
    void testExistUserByEmail() {
        userRepository.save(user);
        assertTrue(userRepository.existsUserByEmail(user.getEmail()));
    }
}
