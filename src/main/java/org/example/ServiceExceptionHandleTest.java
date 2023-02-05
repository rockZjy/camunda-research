package org.example;

import org.camunda.bpm.client.ExternalTaskClient;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ServiceExceptionHandleTest {
    /**
     * Related BPMN Name: service-exception-handle-test.bpmn
     */

    private final static Logger LOGGER = Logger.getLogger(ServiceExceptionHandleTest.class.getName());

    public static void main(String[] args) {
        ExternalTaskClient client = ExternalTaskClient.create()
                .baseUrl("http://localhost:8080/engine-rest")
                .asyncResponseTimeout(10000) // 长轮询超时时间
                .build();

        client.subscribe("caculate-topic")
                .lockDuration(1000) // 默认锁定时间为20秒，这里修改为1秒
                .handler((externalTask, externalTaskService) -> {
                    Map<String, Object> params = new HashMap<>();
                    // 获取流程变量
                    LOGGER.info("execute caculate-service ");
                    LOGGER.info("ExecutionId: "+ externalTask.getExecutionId());
                    LOGGER.info("ProcessInstanceId: "+ externalTask.getProcessInstanceId());
                    try{
                        Integer a = (Integer)externalTask.getVariable("a");
                        Integer b = (Integer)externalTask.getVariable("b");
                        int result = a + b;
                        LOGGER.info("a + b = "+ result + "");
                        externalTaskService.complete(externalTask, params);
                    }catch(Exception ex) {
                        params.put("a", 0);
                        params.put("b", 0);
                        externalTaskService.handleBpmnError(externalTask, "errorCode", "errorMsg", params);
                        throw new RuntimeException("Caculate Service Occur Error", ex);
                    }
                    //externalTaskService.handleFailure();
                    LOGGER.info("complete caculate-service ");
                })
                .open();

        client.subscribe("demo-logic-test")
                .lockDuration(1000) // 默认锁定时间为20秒，这里修改为1秒
                .handler((externalTask, externalTaskService) -> {
                    // 获取流程变量
                    LOGGER.info("execute msg of output ");
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
    }

}
