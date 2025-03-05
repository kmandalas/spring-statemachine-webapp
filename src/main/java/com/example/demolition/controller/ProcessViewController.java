package com.example.demolition.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProcessViewController {

    @GetMapping("/process/{processId}")
    public String loadProcessPage(@PathVariable String processId, Model model) {
        model.addAttribute("processId", processId);
        return "process"; // This refers to process.html in src/main/resources/templates/
    }

}
