package com.example.demolition.controller;

import com.example.demolition.dto.Process;
import com.example.demolition.service.ProcessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/process")
public class ProcessController {

    private final ProcessService processService;

    public ProcessController(ProcessService processService) {
        this.processService = processService;
    }

    @PostMapping("/start")
    public ResponseEntity<Process> startProcess(@RequestBody Map<String, String> request) {
        String processType = request.get("processType");
        return ResponseEntity.ok(processService.startProcess(processType));
    }

    @GetMapping("/{processId}/form")
    public ResponseEntity<Map<String, Object>> getFormDefinition(@PathVariable String processId) {
        return ResponseEntity.ok(processService.getFormDefinition(processId));
    }

    @PostMapping("/{processId}/submit")
    public ResponseEntity<Process> submitStep(
            @PathVariable String processId,
            @RequestParam String step,
            @RequestParam String event,
            @RequestBody(required = false) Map<String, Object> formData) {
        return ResponseEntity.ok(processService.submitStep(processId, step, event, formData != null ? formData : new HashMap<>()));
    }

    @GetMapping("/{processId}/summary")
    public ResponseEntity<Map<String, Object>> getProcessSummary(@PathVariable String processId) {
        return ResponseEntity.ok(processService.getProcessSummary(processId));
    }

}
