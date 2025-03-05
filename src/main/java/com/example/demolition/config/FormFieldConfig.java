package com.example.demolition.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "form")
public class FormFieldConfig {

    private Map<String, ProcessConfig> processes = new HashMap<>();

    public static class ProcessConfig {
        private String name;
        private String businessKey;
        private Map<String, StepConfig> steps = new LinkedHashMap<>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBusinessKey() {
            return businessKey;
        }

        public void setBusinessKey(String businessKey) {
            this.businessKey = businessKey;
        }

        public Map<String, StepConfig> getSteps() {
            return steps;
        }

        public void setSteps(Map<String, StepConfig> steps) {
            this.steps = steps;
        }
    }

    public static class StepConfig {
        private String title;
        private List<FieldConfig> fields;
        private List<ActionConfig> actions;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<FieldConfig> getFields() {
            return fields;
        }

        public void setFields(List<FieldConfig> fields) {
            this.fields = fields;
        }

        public List<ActionConfig> getActions() {
            return actions;
        }

        public void setActions(List<ActionConfig> actions) {
            this.actions = actions;
        }
    }

    public static class FieldConfig {
        private String id;
        private String label;
        private String type;
        private boolean required;
        private String defaultValue;
        private List<OptionConfig> options;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<OptionConfig> getOptions() {
            return options;
        }

        public void setOptions(List<OptionConfig> options) {
            this.options = options;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    public static class OptionConfig {
        private String value;
        private String label;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    public static class ActionConfig {
        private String id;
        private String label;
        private String event;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }
    }

    public Map<String, ProcessConfig> getProcesses() {
        return processes;
    }

    public void setProcesses(Map<String, ProcessConfig> processes) {
        this.processes = processes;
    }

}
