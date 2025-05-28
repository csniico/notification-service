package com.csniico.notificationService.controller;

import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Data
@RestController
@RequestMapping("/api/v1")
public class EventController {

    private RestTemplate restTemplate;

    public EventController(
            RestTemplate restTemplate
    ) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/")
    public String home() {
        return "Hello World!";
    }
}
