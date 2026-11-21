package com.gymstore.workflow.config;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * ProcessEnginePlugin đăng ký qua ServiceLoader (META-INF/services).
 * postProcessEngineBuild được gọi ngay sau khi Camunda engine sẵn sàng.
 */
public class CamundaDeploymentPlugin implements ProcessEnginePlugin {

    private static final Logger log = LoggerFactory.getLogger(CamundaDeploymentPlugin.class);
    private static final String PROCESS_KEY = "purchase-product-process";
    private static final String BPMN_PATH   = "bpmn/purchase-product-process.bpmn";

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
                log.info("[Camunda] Process '{}' already deployed.", PROCESS_KEY);
                return;
            }

            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            InputStream bpmn = cl.getResourceAsStream(BPMN_PATH);

            if (bpmn == null) {
                log.error("[Camunda] BPMN not found: {}", BPMN_PATH);
                return;
            }

            repo.createDeployment()
                    .name("gymstore-purchase-process")
                    .addInputStream(BPMN_PATH, bpmn)
                    .enableDuplicateFiltering(true)
                    .deploy();

            log.info("[Camunda] Process '{}' deployed successfully.", PROCESS_KEY);

        } catch (Exception e) {
            log.error("[Camunda] Deploy failed: {}", e.getMessage(), e);
        }
    }
}
