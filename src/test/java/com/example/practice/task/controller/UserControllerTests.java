package com.example.practice.task.controller;

import com.example.practice.task.converter.ConverterToJSON;
import com.example.practice.task.dto.UserDto;
import com.example.practice.task.dto.UserTransformer;
import com.example.practice.task.model.User;
import com.example.practice.task.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    private User user;
    @BeforeEach
    void init() {
        user = new User();
        user.setId(1L);
        user.setFirstName("Nick");
        user.setLastName("Fury");
        user.setEmail("nick@mail.com");
        user.setPhoneNumber("+380505558888");
        user.setBirthDate(LocalDate.of(2003, 5, 25));
        user.setAddress("Kyiv");
    }
    @Test
    public void testGetAllUsers() throws Exception {
        List<User> users = List.of(user);

        when(userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is(user.getFirstName())));
    }
    @Test
    public void testCreateUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setAddress(user.getAddress());
        userDto.setDayOfMothBirth(user.getBirthDate().getDayOfMonth());
        userDto.setMonthOfBirth(user.getBirthDate().getMonthValue());
        userDto.setYearOfBirth(user.getBirthDate().getYear());

        User user = UserTransformer.convertToEntity(userDto);
        when(userService.create(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/users")
                        .content(ConverterToJSON.asJsonString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is(user.getFirstName())));

        verify(userService, times(1)).create(any());
    }
    @Test
    public void testUpdateUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setFirstName("New");
        userDto.setLastName("Name");
        userDto.setEmail("newName@mail.com");
        userDto.setPhoneNumber("+380988889977");
        userDto.setAddress("Ukraine, Kyiv");
        userDto.setDayOfMothBirth(16);
        userDto.setMonthOfBirth(2);
        userDto.setYearOfBirth(2002);

        when(userService.readById(anyLong())).thenReturn(user);
        User updatedUser = UserTransformer.convertToEntity(userDto, user);
        when(userService.update(any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/{id}", updatedUser.getId())
                        .content(ConverterToJSON.asJsonString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.email", is(updatedUser.getEmail())));
    }
    @Test
    public void testUpdateUserFewFields() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("johnDoe@mail.com");

        when(userService.readById(anyLong())).thenReturn(user);
        User updatedUser = UserTransformer.convertToEntity(userDto, user);
        when(userService.update(any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/{id}", updatedUser.getId())
                        .content(ConverterToJSON.asJsonString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.firstName", is(updatedUser.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(updatedUser.getLastName())))
                .andExpect(jsonPath("$.email", is(updatedUser.getEmail())));
    }
    @Test
    public void testDeleteUser() throws Exception {
        long id = user.getId();

        mockMvc.perform(delete("/api/users/{id}", id))
                .andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(content().string("User with id " + id + " has been removed"));
    }
    @Test
    public void testGetUserById() throws Exception {
        when(userService.readById(anyLong())).thenReturn(user);
        mockMvc.perform(get("/api/users/{id}", user.getId()))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName", is(user.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(user.getLastName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }
    @Test
    public void testGetUsersByBirthDateRange() throws Exception {
        LocalDate start = LocalDate.of(2003, 1, 1);
        LocalDate end = LocalDate.of(2005, 9, 1);
        List<User> users = List.of(new User(), new User(), new User());

        when(userService.getByBirthDate(start, end)).thenReturn(users);

        mockMvc.perform(get("/api/users/range")
                        .param("start", start.toString())
                        .param("end", end.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.length()").value(users.size()));
    }
}
