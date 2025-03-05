package com.example.demolition.service;

import com.example.demolition.config.FormFieldConfig;
import com.example.demolition.dto.Process;
import com.example.demolition.entity.FormData;
import com.example.demolition.exception.ProcessNotFoundException;
import com.example.demolition.repository.FormDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProcessService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;

    private final FormDataRepository formDataRepository;

    private final FormFieldConfig formFieldConfig;
    private final ObjectMapper objectMapper;

    public ProcessService(RuntimeService runtimeService,
                          TaskService taskService,
                          FormDataRepository formDataRepository, FormFieldConfig formFieldConfig,
                          ObjectMapper objectMapper) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.formDataRepository = formDataRepository;
        this.formFieldConfig = formFieldConfig;
        this.objectMapper = objectMapper;
    }

    public Process startProcess(String processType) {
        String processId = UUID.randomUUID().toString();
        Map<String, Object> variables = new HashMap<>();
        variables.put("processId", processId);
        variables.put("type", processType);
        variables.put("createdAt", LocalDateTime.now());

        // Start process directly after process selection
        runtimeService.startProcessInstanceByKey(formFieldConfig.getProcesses().get(processType).getBusinessKey(), processId, variables);

        // Move straight to the first step
        return new Process(processId, "INIT"); // or whatever the actual first step is
    }

    public Process submitStep(String processId, String step, String uiEvent, Map<String, Object> formData) {
        Task task = taskService.createTaskQuery().processInstanceBusinessKey(processId).singleResult();
        if (task == null) {
            throw new ProcessNotFoundException("No active task for process ID: " + processId);
        }

        if (!"BACK".equals(uiEvent)) {
            FormData data = new FormData();
            data.setProcessId(processId);
            data.setStep(step);
            data.setFormDataJson(objectMapper.valueToTree(formData));
            formDataRepository.save(data);
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("uiEvent", uiEvent);
        taskService.complete(task.getId(), variables);

        Task nextTask = taskService.createTaskQuery().processInstanceBusinessKey(processId).singleResult();
        String newState = (nextTask != null) ? nextTask.getTaskDefinitionKey() : "COMPLETED";
        return new Process(processId, newState);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getFormDefinition(String processId) {
        Task task = taskService.createTaskQuery().processInstanceBusinessKey(processId).singleResult();
        if (task == null) {
            throw new ProcessNotFoundException("No active task for process ID: " + processId);
        }

        String processType = (String) runtimeService.getVariable(task.getProcessInstanceId(), "type");
        FormFieldConfig.ProcessConfig processConfig = formFieldConfig.getProcesses().get(processType);
        if (processConfig == null) {
            throw new RuntimeException("Process type not configured: " + processType);
        }

        String stepKey = task.getTaskDefinitionKey();
        FormFieldConfig.StepConfig stepConfig = processConfig.getSteps().get(stepKey);
        if (stepConfig == null) {
            throw new RuntimeException("Step not configured: " + stepKey);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("processId", processId);
        result.put("processType", processType);
        result.put("currentState", stepKey); // Using task definition key as the current state
        result.put("step", stepKey);
        result.put("title", stepConfig.getTitle());
        result.put("fields", stepConfig.getFields());
        result.put("actions", stepConfig.getActions());

        List<FormData> previousData = formDataRepository.findByProcessIdAndStep(processId, stepKey);
        if (!previousData.isEmpty()) {
            result.put("data", previousData.get(0).getFormDataJson());
        }

        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getProcessSummary(String processId) {
        Task task = taskService.createTaskQuery().processInstanceBusinessKey(processId).singleResult();
        if (task == null) {
            throw new ProcessNotFoundException("No active task for process ID: " + processId);
        }

        String processType = (String) runtimeService.getVariable(task.getProcessInstanceId(), "type");
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("processId", processId);
        summary.put("processType", processType);

        // Retrieve step configuration from YAML
        FormFieldConfig.ProcessConfig processConfig = formFieldConfig.getProcesses().get(processType);
        if (processConfig == null) {
            throw new RuntimeException("Process configuration not found for type: " + processType);
        }

        // Retrieve the latest form data and map it by step
        var latestFormData = formDataRepository.findLatestFormDataByProcess(processId);
        Map<String, FormData> latestFormDataMap = latestFormData.stream()
                .collect(Collectors.toMap(FormData::getStep, Function.identity()));

        // Sort and translate form data based on YAML step order
        Map<String, Object> sortedFormData = new LinkedHashMap<>();
        processConfig.getSteps().forEach((step, stepConfig) -> {
            if (latestFormDataMap.containsKey(step)) {
                String stepTitle = stepConfig.getTitle(); // YAML title
                sortedFormData.put(
                        stepTitle != null ? stepTitle : step.replace("_", " ").toUpperCase(),
                        latestFormDataMap.get(step).getFormDataJson()
                );
            }
        });

        summary.put("formData", sortedFormData);
        return summary;
    }

}
