package com.csniico.notificationService.dto;

import lombok.Data;

@Data
public class Task {
    private int id;
    private String title;
    private String description;
    private String fileUrl;
    private String status;
    private String priority;
    private String createdBy;
    private String[] assignedTo;

}
