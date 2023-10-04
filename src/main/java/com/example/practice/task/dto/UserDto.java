package com.example.practice.task.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;

    private int dayOfMothBirth;
    private int monthOfBirth;
    private int yearOfBirth;
}
