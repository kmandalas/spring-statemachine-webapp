package com.example.demolition.config;

import com.example.demolition.statemachine.ProcessEvents;
import com.example.demolition.statemachine.ProcessStates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;

@Configuration
public class JpaPersisterConfig {

    @Bean
    public StateMachineRuntimePersister<ProcessStates, ProcessEvents, String> stateMachineRuntimePersister(
            JpaStateMachineRepository jpaStateMachineRepository) {
        return new JpaPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
    }

}
