package com.example.practice.task.dto;

import com.example.practice.task.model.User;

import java.time.LocalDate;

public class UserTransformer {
    public static User convertToEntity(UserDto userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setAddress(userDto.getAddress());
        user.setBirthDate(LocalDate.of(userDto.getYearOfBirth(), userDto.getMonthOfBirth(), userDto.getDayOfMothBirth()));
        return user;
    }

    public static User convertToEntity(UserDto userDto, User user) {
        int dayOfMonth = user.getBirthDate().getDayOfMonth();
        int month = user.getBirthDate().getMonth().getValue();
        int year = user.getBirthDate().getYear();

        if (userDto.getFirstName() != null && !userDto.getFirstName().isBlank()) {
            user.setFirstName(userDto.getFirstName());
        }
        if (userDto.getLastName() != null && !userDto.getLastName().isBlank()) {
            user.setLastName(userDto.getLastName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getPhoneNumber() != null && !userDto.getPhoneNumber().isBlank()) {
            user.setPhoneNumber(userDto.getPhoneNumber());
        }
        if (userDto.getAddress() != null && !userDto.getAddress().isBlank()) {
            user.setAddress(userDto.getAddress());
        }
        if (userDto.getDayOfMothBirth() != 0) {
            user.setBirthDate(LocalDate.of(year, month, userDto.getDayOfMothBirth()));
        }
        if (userDto.getMonthOfBirth() != 0) {
            user.setBirthDate(LocalDate.of(year, userDto.getMonthOfBirth(), dayOfMonth));
        }
        if (userDto.getYearOfBirth() != 0) {
            user.setBirthDate(LocalDate.of(userDto.getYearOfBirth(), month, dayOfMonth));
        }
        return user;
    }
}
