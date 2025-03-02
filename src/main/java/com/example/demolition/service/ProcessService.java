package com.example.demolition.service;

import com.example.demolition.config.FormFieldConfig;
import com.example.demolition.dto.Process;
import com.example.demolition.entity.FormData;
import com.example.demolition.repository.FormDataRepository;
import com.example.demolition.statemachine.ProcessEvents;
import com.example.demolition.statemachine.ProcessStates;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProcessService {

    private static final Logger logger = LoggerFactory.getLogger(ProcessService.class);


    private final StateMachineService<ProcessStates, ProcessEvents> stateMachineService;
    private final StateMachinePersist<ProcessStates, ProcessEvents, String> stateMachinePersist;

    private final FormDataRepository formDataRepository;

    private final FormFieldConfig formFieldConfig;
    private final ObjectMapper objectMapper;

    public ProcessService(StateMachineService<ProcessStates, ProcessEvents> stateMachineService,
                          StateMachinePersist<ProcessStates, ProcessEvents, String> stateMachinePersist,
                          FormDataRepository formDataRepository,
                          FormFieldConfig formFieldConfig,
                          ObjectMapper objectMapper) {
        this.stateMachineService = stateMachineService;
        this.stateMachinePersist = stateMachinePersist;
        this.formDataRepository = formDataRepository;
        this.formFieldConfig = formFieldConfig;
        this.objectMapper = objectMapper;
    }

    public Process startProcess(String processType) {
        // Start state machine
        var processId = UUID.randomUUID().toString();
        StateMachine<ProcessStates, ProcessEvents> stateMachine = stateMachineService.acquireStateMachine(processId);

        // 🔥 Ensure `PROCESS_SELECTED` is sent and accepted!
        boolean accepted = stateMachine.sendEvent(ProcessEvents.PROCESS_SELECTED);
        logger.info("✅ Event sent: {}", accepted);

        // Check and persist new state
        ProcessStates newState = stateMachine.getState().getId();
        logger.info("🚀 New state (after PROCESS_SELECTED): " + newState);

        try {
            var ctx = stateMachinePersist.read(processId);
            ctx.getExtendedState().getVariables().put("processId", processId);
            ctx.getExtendedState().getVariables().put("type", processType);
            ctx.getExtendedState().getVariables().put("createdAt", LocalDateTime.now());
            stateMachinePersist.write(ctx, processId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new Process(processId, newState.name());
    }

    public Process submitStep(String processId, String step, String uiEvent, Map<String, Object> formData) {
        StateMachineContext<ProcessStates, ProcessEvents> stateMachineContext;
        try {
            stateMachineContext = stateMachinePersist.read(processId);
        } catch (Exception e) {
            throw new RuntimeException("Process not found with id: " + processId);
        }
        // If the event is not BACK, save form data
        if (!"BACK".equals(uiEvent)) {
            FormData data = new FormData();
            data.setProcessId(processId);
            data.setStep(step);
            data.setFormDataJson(objectMapper.valueToTree(formData));  // Convert Map to JsonNode
            formDataRepository.save(data);
        }

        // Get event
        ProcessEvents processEvent = getProcessEvent(uiEvent);
        logger.info("🔄 Sending event: " + processEvent);

        // Load the persisted state machine
        StateMachine<ProcessStates, ProcessEvents> stateMachine = stateMachineService.acquireStateMachine(processId);
        stateMachine.getExtendedState().getVariables().putAll(stateMachineContext.getExtendedState().getVariables());

        // Log current state before sending event
        ProcessStates currentState = stateMachine.getState().getId();
        logger.info("🧐 Current state before event: " + currentState);

        // Send event
        boolean accepted = stateMachine.sendEvent(processEvent);
        logger.info("✅ Event sent: " + accepted);

        if (!accepted) {
            throw new RuntimeException("❌ Event was not accepted by the state machine! Current state: " + currentState);
        }

        // Get new state after transition
        ProcessStates newState = stateMachine.getState().getId();
        logger.info("🚀 New state (after event): " + newState);

        return new Process(processId, newState.name());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getFormDefinition(String processId) {
        StateMachineContext<ProcessStates, ProcessEvents> stateMachineContext;
        try {
            stateMachineContext = stateMachinePersist.read(processId);
        } catch (Exception e) {
            throw new RuntimeException("Process not found with id: " + processId);
        }
        String stepKey = stateToStepKey(stateMachineContext.getState());

        String processType = (String) stateMachineContext.getExtendedState().getVariables().get("type");
        FormFieldConfig.ProcessConfig processConfig = formFieldConfig.getProcesses().get(processType);
        if (processConfig == null) {
            throw new RuntimeException("Process type not configured: " + processType);
        }

        FormFieldConfig.StepConfig stepConfig = processConfig.getSteps().get(stepKey);
        if (stepConfig == null) {
            throw new RuntimeException("Step not configured: " + stepKey);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("processId", processId);
        result.put("processType", processType);
        result.put("currentState", stateMachineContext.getState());
        result.put("step", stepKey);
        result.put("title", stepConfig.getTitle());
        result.put("fields", stepConfig.getFields());
        result.put("actions", stepConfig.getActions());

        // Add previously saved data if available
        List<FormData> previousData = formDataRepository.findByProcessIdAndStep(processId, stepKey);
        if (!previousData.isEmpty()) {
            result.put("data", previousData.get(0).getFormDataJson());
        }

        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getProcessSummary(String processId) {
        StateMachineContext<ProcessStates, ProcessEvents> stateMachineContext;
        try {
            stateMachineContext = stateMachinePersist.read(processId);
        } catch (Exception e) {
            throw new RuntimeException("Process not found with id: " + processId);
        }
        String processType = (String) stateMachineContext.getExtendedState().getVariables().get("type");
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("processId", processId);
        summary.put("processType", processType);
        summary.put("currentState", stateMachineContext.getState());

        // Retrieve step configuration from YAML
        FormFieldConfig.ProcessConfig processConfig = formFieldConfig.getProcesses().get(processType);
        if (processConfig == null) {
            throw new RuntimeException("Process configuration not found for type: " + processType);
        }

        Map<String, FormFieldConfig.StepConfig> stepConfigMap = processConfig.getSteps();

        // Keep only the latest entry per step
        var latestFormData = formDataRepository.findLatestFormDataByProcess(processId);
        Map<String, FormData> latestFormDataMap = latestFormData.stream()
                .collect(Collectors.toMap(FormData::getStep, Function.identity()));

       // Sort steps based on YAML configuration order
        Map<String, JsonNode> sortedFormData = stepConfigMap.keySet().stream()
                .filter(latestFormDataMap::containsKey)
                .collect(Collectors.toMap(
                        step -> step,
                        step -> latestFormDataMap.get(step).getFormDataJson(),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));

        // Translate step names using YAML titles
        Map<String, Object> translatedFormData = new LinkedHashMap<>();
        sortedFormData.forEach((step, data) -> {
            String translatedStepName = stepConfigMap.getOrDefault(step, new FormFieldConfig.StepConfig())
                    .getTitle();
            translatedFormData.put(translatedStepName != null ? translatedStepName : step.replace("_", " ").toUpperCase(), data);
        });

        summary.put("formData", translatedFormData);
        return summary;
    }

    private ProcessEvents getProcessEvent(String event) {
        return switch (event) {
            case "STEP_ONE_SUBMIT" -> ProcessEvents.STEP_ONE_SUBMIT;
            case "STEP_TWO_SUBMIT" -> ProcessEvents.STEP_TWO_SUBMIT;
            case "STEP_THREE_SUBMIT" -> ProcessEvents.STEP_THREE_SUBMIT;
            case "FINAL_SUBMIT" -> ProcessEvents.FINAL_SUBMIT;
            case "BACK" -> ProcessEvents.BACK;
            default -> throw new IllegalArgumentException("Invalid event: " + event);
        };
    }

    private String stateToStepKey(ProcessStates state) {
        return switch (state) {
            case STEP_ONE -> "step_one";
            case STEP_TWO -> "step_two";
            case STEP_THREE -> "step_three";
            case SUBMISSION -> "submission";
            case PROCESS_SELECTION -> "selection";
            default -> throw new IllegalArgumentException("No step key for state: " + state);
        };
    }

}
