package org.example;

import org.camunda.bpm.client.ExternalTaskClient;

import java.util.logging.Logger;

public class ParallelGatewayTest {

    private final static Logger LOGGER = Logger.getLogger(ParallelGatewayTest.class.getName());

    public static void main(String[] args) {
        ExternalTaskClient client = ExternalTaskClient.create()
                .baseUrl("http://localhost:8080/engine-rest")
                .build();

        client.subscribe("task-a")
                .lockDuration(30000) // 默认锁定时间为20秒
                .handler((externalTask, externalTaskService) -> {

                    // 获取流程变量
                    LOGGER.info("execute task-a ");
                    LOGGER.info("ExecutionId: "+ externalTask.getExecutionId());
                    LOGGER.info("ProcessInstanceId: "+ externalTask.getProcessInstanceId());
                    LOGGER.info("complete task-a ");

                    // 完成任务
                    externalTaskService.complete(externalTask);
                })
                .open();

        // 订阅指定的外部任务
        client.subscribe("task-b")
                .lockDuration(30000) // 默认锁定时间为20秒
                .handler((externalTask, externalTaskService) -> {

                    // 获取流程变量
                    LOGGER.info("execute msg of task-b ");
                    LOGGER.info("ExecutionId: "+ externalTask.getExecutionId());
                    LOGGER.info("ProcessInstanceId: "+ externalTask.getProcessInstanceId());
                    LOGGER.info("complete task-b ");
                    // 完成任务
                    externalTaskService.complete(externalTask);
                })
                .open();
        // 订阅指定的外部任务
        client.subscribe("task-c")
                .lockDuration(30000) // 默认锁定时间为20秒，这里修改为1秒
                .handler((externalTask, externalTaskService) -> {

                    // 获取流程变量
                    LOGGER.info("execute msg of task-c ");
                    LOGGER.info("ExecutionId: "+ externalTask.getExecutionId());
                    LOGGER.info("ProcessInstanceId: "+ externalTask.getProcessInstanceId());
                    String test_input = (String) externalTask.getVariable("test_input");
                    LOGGER.info("test_input: "+test_input);
                    String test_output = (String) externalTask.getVariable("test_output");
                    //Integer output = (Integer) externalTask.getVariable("Output");
                    LOGGER.info("test_output: "+test_output);
                    LOGGER.info("complete task-c ");
                    // 完成任务
                    externalTaskService.complete(externalTask);
                })
                .open();
    }
}