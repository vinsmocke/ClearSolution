package com.example.practice.task.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserTests {
    private static User validUser;
    private static Validator validator;
    @BeforeAll
    static void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        validUser = new User();
        validUser.setId(0L);
        validUser.setFirstName("Nick");
        validUser.setLastName("Fury");
        validUser.setEmail("nick@mail.com");
        validUser.setBirthDate(LocalDate.of(2003, 12, 22));
        validUser.setPhoneNumber("+380505556566");
    }
    @Test
    void userWithValidEmail() {
        User user = new User();
        user.setEmail("someEmail123@mail.com");
        user.setFirstName("Firstname");
        user.setLastName("Lastname");
        user.setBirthDate(LocalDate.of(2003, 7, 27));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(0, violations.size());
    }
    @Test
    void createValidUser() {
        Set<ConstraintViolation<User>> violations = validator.validate(validUser);

        assertEquals(0, violations.size());
    }
    @ParameterizedTest
    @MethodSource("provideInvalidEmailUser")
    void constraintViolationInvalid(String input, String errorValue) {
        User user = new User();
        user.setEmail(input);
        user.setFirstName("Firstname");
        user.setLastName("Lastname");
        user.setBirthDate(LocalDate.of(2005, 5, 25));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals(errorValue, violations.iterator().next().getInvalidValue());
    }
    private static Stream<Arguments> provideInvalidEmailUser() {
        return Stream.of(
                Arguments.of("invalidEmail", "invalidEmail"),
                Arguments.of("email@", "email@"),
                Arguments.of("invalid", "invalid")
        );
    }
    @ParameterizedTest
    @MethodSource("provideInvalidEmailUser")
    void constraintViolationInvalidFirstName(String input, String errorValue) {
        User user = new User();
        user.setEmail(validUser.getEmail());
        user.setFirstName(input);
        user.setLastName(validUser.getLastName());
        user.setBirthDate(validUser.getBirthDate());

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
        assertEquals(errorValue, violations.iterator().next().getInvalidValue());
    }
    @Test
    void toStringUserTest() {
        User user = new User();
        user.setId(0L);
        user.setFirstName(validUser.getFirstName());
        user.setLastName(validUser.getLastName());
        user.setEmail(validUser.getEmail());
        user.setPhoneNumber(validUser.getPhoneNumber());
        user.setBirthDate(validUser.getBirthDate());

        String expected = String.format("User{id=%d, firstName='%s', " +
                        "lastName='%s', email='%s', phoneNumber='%s', birthDate=%s, address='%s'}",
                user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getPhoneNumber(), user.getBirthDate(), user.getAddress());
        assertEquals(expected, user.toString());
    }
}
