package com.example.demolition.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "form_data")
public class FormData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String step;

    @Type(JsonBinaryType.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "formDataJson", columnDefinition = "jsonb")
    private JsonNode formDataJson;

    @ManyToOne
    @JoinColumn(name = "process_id")
    @JsonIgnore  // This prevents infinite recursion
    private Process process;

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public JsonNode getFormDataJson() {
        return formDataJson;
    }

    public void setFormDataJson(JsonNode formDataJson) {
        this.formDataJson = formDataJson;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

}
