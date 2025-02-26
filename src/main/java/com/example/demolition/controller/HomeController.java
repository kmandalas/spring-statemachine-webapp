package com.example.demolition.controller;

import com.example.demolition.config.FormFieldConfig;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class HomeController {

    private final FormFieldConfig formFieldConfig;

    public HomeController(FormFieldConfig formFieldConfig) {
        this.formFieldConfig = formFieldConfig;
    }

    @GetMapping("/")
    public String home(Model model) {
        Map<String, FormFieldConfig.ProcessConfig> processes = formFieldConfig.getProcesses();
        model.addAttribute("processes", processes);
        return "home"; // Refers to home.html in templates
    }

}
