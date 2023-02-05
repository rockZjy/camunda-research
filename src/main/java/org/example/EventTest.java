package org.example;

import org.camunda.bpm.client.ExternalTaskClient;

import java.util.logging.Logger;

public class EventTest {

    private final static Logger LOGGER = Logger.getLogger(EventTest.class.getName());

    public static void main(String[] args) {
        ExternalTaskClient client = ExternalTaskClient.create()
                .baseUrl("http://localhost:8080/engine-rest")
                .asyncResponseTimeout(10000) // 长轮询超时时间
                .build();

        client.subscribe("start-service")
                .lockDuration(1000) // 默认锁定时间为20秒，这里修改为1秒
                .handler((externalTask, externalTaskService) -> {

                    // 获取流程变量
                    LOGGER.info("execute start-service ");
                    LOGGER.info("ExecutionId: "+ externalTask.getExecutionId());
                    LOGGER.info("ProcessInstanceId: "+ externalTask.getProcessInstanceId());
                    LOGGER.info("complete start-service ");

                    // 完成任务
                    externalTaskService.complete(externalTask);
                })
                .open();

        // 订阅指定的外部任务
        client.subscribe("demo_logic")
                .lockDuration(1000) // 默认锁定时间为20秒，这里修改为1秒
                .handler((externalTask, externalTaskService) -> {

                    // 获取流程变量
                    LOGGER.info("execute msg of demo_logic ");
                    LOGGER.info("ExecutionId: "+ externalTask.getExecutionId());
                    LOGGER.info("ProcessInstanceId: "+ externalTask.getProcessInstanceId());
                    String msg_status = (String) externalTask.getVariable("msg_status");
                    LOGGER.info("msg_status: "+msg_status);
                    //String output = (String) externalTask.getVariable("Output");
                    Integer output = (Integer) externalTask.getVariable("Output");
                    LOGGER.info("output: "+output);

                    // 完成任务
                    externalTaskService.complete(externalTask);
                })
                .open();
        // 订阅指定的外部任务
        client.subscribe("not_match")
                .lockDuration(1000) // 默认锁定时间为20秒，这里修改为1秒
                .handler((externalTask, externalTaskService) -> {

                    // 获取流程变量
                    LOGGER.info("execute msg of not_match ");
                    LOGGER.info("ExecutionId: "+ externalTask.getExecutionId());
                    LOGGER.info("ProcessInstanceId: "+ externalTask.getProcessInstanceId());
                    String msg_status = (String) externalTask.getVariable("msg_status");
                    LOGGER.info("msg_status: "+msg_status);
                    //String output = (String) externalTask.getVariable("Output");
                    Integer output = (Integer) externalTask.getVariable("Output");
                    LOGGER.info("output: "+output);

                    // 完成任务
                    externalTaskService.complete(externalTask);
                })
                .open();
    }

}
