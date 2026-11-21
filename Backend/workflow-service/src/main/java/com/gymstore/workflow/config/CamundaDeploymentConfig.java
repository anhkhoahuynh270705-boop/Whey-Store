package com.gymstore.workflow.config;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * Đăng ký Camunda ProcessEnginePlugin dạng @Bean để Camunda Spring Boot
 * tự động pick up và gọi postProcessEngineBuild() sau khi engine sẵn sàng.
 */
@Slf4j
@Configuration
public class CamundaDeploymentConfig {

    private static final String PROCESS_KEY = "purchase-product-process";
    private static final String BPMN_PATH   = "bpmn/purchase-product-process.bpmn";

    @Bean
    public ProcessEnginePlugin bpmnDeploymentPlugin() {
        return new ProcessEnginePlugin() {

            @Override
            public void preInit(ProcessEngineConfigurationImpl config) { }

            @Override
            public void postInit(ProcessEngineConfigurationImpl config) { }

            @Override
            public void postProcessEngineBuild(ProcessEngine engine) {
                try {
                    var repo = engine.getRepositoryService();

                    long count = repo.createProcessDefinitionQuery()
                            .processDefinitionKey(PROCESS_KEY)
                            .count();

                    if (count > 0) {
                        ProcessDefinition def = repo.createProcessDefinitionQuery()
                                .processDefinitionKey(PROCESS_KEY)
                                .latestVersion()
                                .singleResult();
                        log.info("[Camunda] Process '{}' already deployed (version {}).",
                                PROCESS_KEY, def.getVersion());
                        return;
                    }

                    ClassPathResource resource = new ClassPathResource(BPMN_PATH);
                    if (!resource.exists()) {
                        log.error("[Camunda] BPMN file not found: classpath:{}", BPMN_PATH);
                        return;
                    }

                    repo.createDeployment()
                            .name("gymstore-purchase-process")
                            .addInputStream(BPMN_PATH, resource.getInputStream())
                            .enableDuplicateFiltering(true)
                            .deploy();

                    log.info("[Camunda] Process '{}' deployed successfully from classpath:{}",
                            PROCESS_KEY, BPMN_PATH);

                } catch (IOException e) {
                    log.error("[Camunda] Cannot read BPMN '{}': {}", BPMN_PATH, e.getMessage(), e);
                } catch (Exception e) {
                    log.error("[Camunda] Deploy failed for '{}': {}", PROCESS_KEY, e.getMessage(), e);
                }
            }
        };
    }
}
