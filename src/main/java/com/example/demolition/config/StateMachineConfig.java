package com.example.demolition.config;

import com.example.demolition.entity.Process;
import com.example.demolition.repository.ProcessRepository;
import com.example.demolition.statemachine.ProcessEvents;
import com.example.demolition.statemachine.ProcessStates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;
import java.util.Optional;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<ProcessStates, ProcessEvents> {

    private static final Logger logger = LoggerFactory.getLogger(StateMachineConfig.class);

    private final ProcessRepository processRepository;
    // private final JpaStateMachineRepository stateMachineRepository;

    public StateMachineConfig(ProcessRepository processRepository) {
        this.processRepository = processRepository;
    }

    @Override
    public void configure(StateMachineStateConfigurer<ProcessStates, ProcessEvents> states) throws Exception {
        states
                .withStates()
                .initial(ProcessStates.PROCESS_SELECTION)
                .states(EnumSet.allOf(ProcessStates.class))
                .end(ProcessStates.COMPLETED)
                .end(ProcessStates.ERROR);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ProcessStates, ProcessEvents> transitions) throws Exception {
        transitions
                .withExternal()
                .source(ProcessStates.PROCESS_SELECTION)
                .target(ProcessStates.STEP_ONE)
                .event(ProcessEvents.PROCESS_SELECTED)
                .and()
                .withExternal()
                .source(ProcessStates.STEP_ONE)
                .target(ProcessStates.STEP_TWO)
                .event(ProcessEvents.STEP_ONE_SUBMIT)
                .and()
                .withExternal()
                .source(ProcessStates.STEP_TWO)
                .target(ProcessStates.STEP_THREE)
                .event(ProcessEvents.STEP_TWO_SUBMIT)
                .and()
                .withExternal()
                .source(ProcessStates.STEP_THREE)
                .target(ProcessStates.SUBMISSION)
                .event(ProcessEvents.STEP_THREE_SUBMIT)
                .and()
                .withExternal()
                .source(ProcessStates.SUBMISSION)
                .target(ProcessStates.COMPLETED)
                .event(ProcessEvents.FINAL_SUBMIT)
                .and()

                // Add back navigation
                .withExternal()
                .source(ProcessStates.STEP_TWO)
                .target(ProcessStates.STEP_ONE)
                .event(ProcessEvents.BACK)
                .and()
                .withExternal()
                .source(ProcessStates.STEP_THREE)
                .target(ProcessStates.STEP_TWO)
                .event(ProcessEvents.BACK)
                .and()
                .withExternal()
                .source(ProcessStates.SUBMISSION)
                .target(ProcessStates.STEP_THREE)
                .event(ProcessEvents.BACK)
                .and()

                // Reset to beginning from any state
                .withExternal()
                .source(ProcessStates.STEP_ONE)
                .target(ProcessStates.PROCESS_SELECTION)
                .event(ProcessEvents.RESET)
                .and()
                .withExternal()
                .source(ProcessStates.STEP_TWO)
                .target(ProcessStates.PROCESS_SELECTION)
                .event(ProcessEvents.RESET)
                .and()
                .withExternal()
                .source(ProcessStates.STEP_THREE)
                .target(ProcessStates.PROCESS_SELECTION)
                .event(ProcessEvents.RESET)
                .and()
                .withExternal()
                .source(ProcessStates.SUBMISSION)
                .target(ProcessStates.PROCESS_SELECTION)
                .event(ProcessEvents.RESET);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<ProcessStates, ProcessEvents> config) throws Exception {
        config
                .withConfiguration()
                .autoStartup(true)
                .listener(listener());
    }

    @Bean
    public StateMachineListener<ProcessStates, ProcessEvents> listener() {
        return new StateMachineListenerAdapter<ProcessStates, ProcessEvents>() {
            @Override
            public void stateChanged(State<ProcessStates, ProcessEvents> from, State<ProcessStates, ProcessEvents> to) {
                if (from != null && to != null) {
                    logger.info("State changed from {} to {}", from.getId(), to.getId());
                }
            }
        };
    }

    @Bean
    public Action<ProcessStates, ProcessEvents> saveProcessAction() {
        return context -> {
            Long processId = (Long) context.getExtendedState().getVariables().get("processId");
            if (processId != null) {
                Optional<Process> processOpt = processRepository.findById(processId);
                if (processOpt.isPresent()) {
                    Process process = processOpt.get();
                    process.setCurrentState(context.getTarget().getId().name());
                    processRepository.save(process);
                }
            }
        };
    }

//    @Bean
//    public StateMachineRuntimePersister<ProcessStates, ProcessEvents, String> stateMachineRuntimePersister() {
//        return new JpaPersistingStateMachineInterceptor<>(stateMachineRepository);
//    }
//
//    @Bean
//    public StateMachinePersister<ProcessStates, ProcessEvents, String> stateMachinePersister(
//            StateMachineRuntimePersister<ProcessStates, ProcessEvents, String> runtimePersister) {
//        return new DefaultStateMachinePersister<>(runtimePersister);
//    }

}
