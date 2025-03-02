package com.example.demolition.config;

import com.example.demolition.statemachine.ProcessEvents;
import com.example.demolition.statemachine.ProcessStates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<ProcessStates, ProcessEvents> {

    private static final Logger logger = LoggerFactory.getLogger(StateMachineConfig.class);

    private final StateMachineRuntimePersister<ProcessStates, ProcessEvents, String> stateMachineRuntimePersister;

    public StateMachineConfig(StateMachineRuntimePersister<ProcessStates, ProcessEvents, String> stateMachineRuntimePersister) {
        this.stateMachineRuntimePersister = stateMachineRuntimePersister;
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
                .action(saveProcessAction())
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
//                .autoStartup(true)
                .listener(listener())
                .and()
                .withPersistence()
                .runtimePersister(stateMachineRuntimePersister);
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
            // add actions here...
        };
    }

    @Bean
    public StateMachineService<ProcessStates, ProcessEvents> stateMachineService(
            StateMachineFactory<ProcessStates, ProcessEvents> stateMachineFactory,
            StateMachineRuntimePersister<ProcessStates, ProcessEvents, String> stateMachineRuntimePersister) {
        return new DefaultStateMachineService<ProcessStates, ProcessEvents>(stateMachineFactory, stateMachineRuntimePersister);
    }

}
