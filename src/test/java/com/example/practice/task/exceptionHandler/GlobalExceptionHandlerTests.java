package com.example.practice.task.exceptionHandler;

import com.example.practice.task.converter.ConverterToJSON;
import com.example.practice.task.dto.UserDto;
import com.example.practice.task.dto.UserTransformer;
import com.example.practice.task.exception.InvalidEmailException;
import com.example.practice.task.exception.NullEntityReferenceException;
import com.example.practice.task.model.User;
import com.example.practice.task.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class GlobalExceptionHandlerTests {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    private UserDto userDto;
    @BeforeEach
    public void init() {
        userDto = new UserDto();
        userDto.setFirstName("Nick");
        userDto.setLastName("Fury");
        userDto.setEmail("nick@mail.com");
        userDto.setPhoneNumber("+380505558888");
        userDto.setAddress("Kyiv");
        userDto.setYearOfBirth(2003);
        userDto.setMonthOfBirth(5);
        userDto.setDayOfMothBirth(25);
    }
    @Test
    public void testHandleNullEntityReferenceException() throws Exception {
        NullEntityReferenceException ex = new NullEntityReferenceException("Cannot be null");
        when(userService.create(UserTransformer.convertToEntity(userDto))).thenThrow(ex);

        mockMvc.perform(post("/api/users")
                        .content(ConverterToJSON.asJsonString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(ex.getMessage()));
    }
    @Test
    public void testHandleConstraintViolationException() throws Exception {
        userDto.setFirstName("nick");
        User user = UserTransformer.convertToEntity(userDto);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        ConstraintViolationException ex = new ConstraintViolationException(violations);

        when(userService.create(user)).thenThrow(ex);

        mockMvc.perform(post("/api/users")
                        .content(ConverterToJSON.asJsonString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(ex.getClass().getSimpleName()));
    }
    @Test
    public void testHandleEntityNotFoundException() throws Exception {
        EntityNotFoundException ex = new EntityNotFoundException("Not Found!");
        when(userService.create(UserTransformer.convertToEntity(userDto))).thenThrow(ex);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ConverterToJSON.asJsonString(userDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(HttpStatus.NOT_FOUND.name()))
                .andExpect(jsonPath("$.error").value(ex.getClass().getSimpleName()))
                .andExpect(jsonPath("$.message").value(ex.getMessage()));
    }
    @Test
    public void testHandleIllegalArgumentException() throws Exception {
        IllegalArgumentException ex = new IllegalArgumentException("The Start date should be less than the End date");
        when(userService.getByBirthDate(LocalDate.now(), LocalDate.now())).thenThrow(ex);

        mockMvc.perform(get("/api/users/range")
                        .param("start", LocalDate.now().toString())
                        .param("end", LocalDate.now().toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.error").value(ex.getClass().getSimpleName()))
                .andExpect(jsonPath("$.message").value(ex.getMessage()));
    }
    @Test
    public void testHandleInvalidEmailException() throws Exception {
        InvalidEmailException ex = new InvalidEmailException("User with email " + userDto.getEmail() + " already exist!");
        when(userService.create(UserTransformer.convertToEntity(userDto))).thenThrow(ex);

        mockMvc.perform(post("/api/users")
                        .content(ConverterToJSON.asJsonString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.error").value(ex.getClass().getSimpleName()))
                .andExpect(jsonPath("$.message").value(ex.getMessage()));
    }
}
