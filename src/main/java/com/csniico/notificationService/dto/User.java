package com.csniico.notificationService.dto;

import lombok.Data;

@Data
public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Long userId;
}
