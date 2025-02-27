package com.example.demolition.controller;

import com.example.demolition.entity.Process;
import com.example.demolition.service.ProcessService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/process")
public class ProcessController {

    private final ProcessService processService;

    public ProcessController(ProcessService processService) {
        this.processService = processService;
    }

    @PostMapping("/start")
    public String startProcess(@RequestParam String processType, Model model) {
        Process process = processService.startProcess(processType);
        // Redirect using HTMX response header
        return "redirect:/process/" + process.getId();
    }

    @GetMapping("/{processId}/form")
    public String getFormDefinition(@PathVariable Long processId, Model model) {
        Map<String, Object> formDef = processService.getFormDefinition(processId);

        // Ensure data exists even if null
        if (!formDef.containsKey("data")) {
            formDef.put("data", new HashMap<>());
        }

        model.addAttribute("form", formDef);

        if ("SUBMISSION".equals(formDef.get("currentState"))) {
            Map<String, Object> summary = processService.getProcessSummary(processId);
            model.addAttribute("summary", summary);
            return "fragments/review-form :: reviewForm";
        } else {
            return "fragments/step-form :: stepForm";
        }
    }

    @PostMapping("/{processId}/submit")
    public String submitStep(
            @PathVariable Long processId,
            @RequestParam String step,
            @RequestParam String event,
            @RequestParam Map<String, String> allParams,  // Changed from @RequestBody
            Model model) {

        // Remove step and event from the form data
        Map<String, Object> formData = new HashMap<>(allParams);
        formData.remove("step");
        formData.remove("event");

        processService.submitStep(
                processId,
                step,
                event,
                formData
        );

        // After submission, return the updated form
        return getFormDefinition(processId, model);
    }

    // API endpoint for specific operations that need JSON
    @GetMapping("/{processId}/summary-json")
    @ResponseBody
    public Map<String, Object> getProcessSummaryJson(@PathVariable Long processId) {
        return processService.getProcessSummary(processId);
    }

}