package org.example;

import org.camunda.bpm.client.ExternalTaskClient;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ServiceTestException {
    /**
     * Related BPMN Name: service-test.bpmn
     */

    private final static Logger LOGGER = Logger.getLogger(ServiceTestException.class.getName());

    public static void main(String[] args) {
        ExternalTaskClient client = ExternalTaskClient.create()
                .baseUrl("http://localhost:8080/engine-rest")
                .asyncResponseTimeout(10000) // 长轮询超时时间
                .build();

        client.subscribe("start-service-test")
                .lockDuration(1000) // 默认锁定时间为20秒，这里修改为1秒
                .handler((externalTask, externalTaskService) -> {
                    Map<String, Object> params = new HashMap<>();
                    // 获取流程变量
                    LOGGER.info("execute start-service ");
                    LOGGER.info("ExecutionId: "+ externalTask.getExecutionId());
                    LOGGER.info("ProcessInstanceId: "+ externalTask.getProcessInstanceId());
                    LOGGER.info("complete start-service ");
//                    try {
                        int a = 1/0;
//                    } catch (Exception e) {
//                        throw new RuntimeException("Exception");
//                    }
                    // 完成任务
//                    params.put("service_rtn", 1);
                    externalTaskService.complete(externalTask, params);
                })
                .open();

        // 订阅指定的外部任务
        client.subscribe("demo-logic-test")
                .lockDuration(1000) // 默认锁定时间为20秒，这里修改为1秒
                .handler((externalTask, externalTaskService) -> {

                    // 获取流程变量
                    LOGGER.info("execute msg of demo_logic ");
                    LOGGER.info("ExecutionId: "+ externalTask.getExecutionId());
                    LOGGER.info("ProcessInstanceId: "+ externalTask.getProcessInstanceId());
                    String msg_status = (String) externalTask.getVariable("msg_status");
                    LOGGER.info("msg_status: "+msg_status);
                    //String output = (String) externalTask.getVariable("Output");
                    Integer serviceRtn = (Integer) externalTask.getVariable("service_rtn");
                    LOGGER.info("service_rtn: "+serviceRtn);

                    // 完成任务
                    externalTaskService.complete(externalTask);
                })
                .open();
        // 订阅指定的外部任务
        client.subscribe("not-match-test")
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
