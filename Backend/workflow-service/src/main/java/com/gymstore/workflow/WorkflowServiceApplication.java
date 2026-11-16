package com.gymstore.workflow;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** workflow-service: Camunda 7 engine + BPMN purchase process. */
@SpringBootApplication
@EnableProcessApplication("gymstore-workflow")
public class WorkflowServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkflowServiceApplication.class, args);
    }
}
