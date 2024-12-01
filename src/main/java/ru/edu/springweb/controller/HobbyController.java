package ru.edu.springweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HobbyController {

    @GetMapping("/my-hobby")
    public String showMyHobbyPage() {
        return "my-hobby";
    }
}