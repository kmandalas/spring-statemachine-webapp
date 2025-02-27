package com.example.demolition.service;

import com.example.demolition.config.FormFieldConfig;
import com.example.demolition.entity.FormData;
import com.example.demolition.entity.Process;
import com.example.demolition.repository.FormDataRepository;
import com.example.demolition.repository.ProcessRepository;
import com.example.demolition.statemachine.ProcessEvents;
import com.example.demolition.statemachine.ProcessStates;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProcessService {

    private static final Logger logger = LoggerFactory.getLogger(ProcessService.class);

    private final StateMachineFactory<ProcessStates, ProcessEvents> stateMachineFactory;
    private final ProcessRepository processRepository;
    private final FormDataRepository formDataRepository;
    private final FormFieldConfig formFieldConfig;
    private final ObjectMapper objectMapper;

    public ProcessService(StateMachineFactory<ProcessStates, ProcessEvents> stateMachineFactory,
                          ProcessRepository processRepository, FormDataRepository formDataRepository,
                          FormFieldConfig formFieldConfig, ObjectMapper objectMapper) {
        this.stateMachineFactory = stateMachineFactory;
        this.processRepository = processRepository;
        this.formDataRepository = formDataRepository;
        this.formFieldConfig = formFieldConfig;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Process startProcess(String processType) {
        Process process = new Process();
        process.setProcessType(processType);
        process.setCurrentState(ProcessStates.PROCESS_SELECTION.name()); // ✅ Correct starting state
        process = processRepository.save(process);

        // Start state machine
        StateMachine<ProcessStates, ProcessEvents> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.getExtendedState().getVariables().put("processId", process.getId());
        stateMachine.start();

        // 🔥 Ensure `PROCESS_SELECTED` is sent and accepted!
        boolean accepted = stateMachine.sendEvent(ProcessEvents.PROCESS_SELECTED);
        logger.info("✅ Event sent: {}", accepted);

        // Check and persist new state
        ProcessStates newState = stateMachine.getState().getId();
        logger.info("🚀 New state (after PROCESS_SELECTED): " + newState);

        process.setCurrentState(newState.name());
        return processRepository.save(process);
    }


    @Transactional
    public Process submitStep(Long processId, String step, String uiEvent, Map<String, Object> formData) {
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new RuntimeException("Process not found with id: " + processId));

        // If the event is not BACK, save form data
        if (!"BACK".equals(uiEvent)) {
            FormData data = new FormData();
            data.setProcess(process);
            data.setStep(step);
            data.setFormDataJson(objectMapper.valueToTree(formData));  // Convert Map to JsonNode
            formDataRepository.save(data);
        }

        // Get event
        ProcessEvents processEvent = getProcessEvent(uiEvent);
        logger.info("🔄 Sending event: " + processEvent);

        // Load the persisted state machine
        StateMachine<ProcessStates, ProcessEvents> stateMachine = stateMachineFactory.getStateMachine(processId.toString());
        try {
            stateMachine.stop();

            stateMachine.getStateMachineAccessor().doWithAllRegions(accessor -> {
                accessor.resetStateMachine(
                        new DefaultStateMachineContext<>(
                                ProcessStates.valueOf(process.getCurrentState()),
                                null,
                                null,
                                null
                        )
                );
            });

            stateMachine.start();
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to restore state machine", e);
        }

        // Log current state before sending event
        ProcessStates currentState = stateMachine.getState().getId();
        logger.info("🧐 Current state before event: " + currentState);

        if (!currentState.equals(ProcessStates.valueOf(process.getCurrentState()))) {
            throw new RuntimeException("❌ State machine is out of sync! DB state: "
                    + process.getCurrentState() + ", State machine: " + currentState);
        }

        // Send event
        boolean accepted = stateMachine.sendEvent(processEvent);
        logger.info("✅ Event sent: " + accepted);

        if (!accepted) {
            throw new RuntimeException("❌ Event was not accepted by the state machine! Current state: " + currentState);
        }

        // Get new state after transition
        ProcessStates newState = stateMachine.getState().getId();
        logger.info("🚀 New state (after event): " + newState);

        // Persist new state
        try {
            process.setCurrentState(newState.name());
            processRepository.save(process);
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to persist state machine", e);
        }

        logger.info("💾 State saved in DB: " + process.getCurrentState());
        return process;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getFormDefinition(Long processId) {
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new RuntimeException("Process not found with id: " + processId));

        ProcessStates currentState = ProcessStates.valueOf(process.getCurrentState());
        String stepKey = stateToStepKey(currentState);

        FormFieldConfig.ProcessConfig processConfig = formFieldConfig.getProcesses().get(process.getProcessType());
        if (processConfig == null) {
            throw new RuntimeException("Process type not configured: " + process.getProcessType());
        }

        FormFieldConfig.StepConfig stepConfig = processConfig.getSteps().get(stepKey);
        if (stepConfig == null) {
            throw new RuntimeException("Step not configured: " + stepKey);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("processId", processId);
        result.put("processType", process.getProcessType());
        result.put("currentState", currentState.name());
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
    public Map<String, Object> getProcessSummary(Long processId) {
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new RuntimeException("Process not found"));

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("processId", process.getId());
        summary.put("processType", process.getProcessType());
        summary.put("currentState", process.getCurrentState());

        // Retrieve step configuration from YAML
        FormFieldConfig.ProcessConfig processConfig = formFieldConfig.getProcesses().get(process.getProcessType());
        if (processConfig == null) {
            throw new RuntimeException("Process configuration not found for type: " + process.getProcessType());
        }

        Map<String, FormFieldConfig.StepConfig> stepConfigMap = processConfig.getSteps();

        // Keep only the latest entry per step
        Map<String, FormData> latestFormData = new HashMap<>();
        for (FormData entry : process.getFormDataList()) {
            latestFormData.put(entry.getStep(), entry);  // Always replaces with the latest entry
        }

       // Sort steps based on YAML configuration order
        Map<String, JsonNode> sortedFormData = stepConfigMap.keySet().stream()
                .filter(latestFormData::containsKey)
                .collect(Collectors.toMap(
                        step -> step,
                        step -> latestFormData.get(step).getFormDataJson(),
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

    @Transactional
    public void triggerEvent(Long processId, ProcessEvents event) {
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new RuntimeException("Process not found with id: " + processId));

        StateMachine<ProcessStates, ProcessEvents> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.getExtendedState().getVariables().put("processId", processId);

        // Start the state machine
        stateMachine.start();

        // Send the event
        stateMachine.sendEvent(event);

        // Update process state
        ProcessStates newState = stateMachine.getState().getId();
        process.setCurrentState(newState.name());
        processRepository.save(process);
    }

}
