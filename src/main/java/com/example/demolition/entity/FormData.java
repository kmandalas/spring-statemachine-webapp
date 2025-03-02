package com.example.demolition.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "form_data")
public class FormData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String processId;

    private String step;

    // @Type(JsonBinaryType.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "formDataJson", columnDefinition = "jsonb")
    private JsonNode formDataJson;

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

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

}
