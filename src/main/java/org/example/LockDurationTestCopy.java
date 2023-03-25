package org.example;

import org.camunda.bpm.client.ExternalTaskClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class LockDurationTestCopy {

    private final static Logger LOGGER = Logger.getLogger(LockDurationTestCopy.class.getName());

    public static void main(String[] args) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        ExternalTaskClient client = ExternalTaskClient.create()
                .baseUrl("http://localhost:8080/engine-rest")
                .asyncResponseTimeout(10000)
                .build();

        client.subscribe("lock-duration")
                //.lockDuration(30*1000)
                .handler((externalTask, externalTaskService) -> {
                    Date date = new Date(System.currentTimeMillis());
                    System.out.println(formatter.format(date)+"# execute lock-duration instance2");
                    try {
                        Thread.sleep(60*1000);
                        Date date2 = new Date(System.currentTimeMillis());
                        System.out.println(formatter.format(date2)+"# complete lock-duration instance2");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    externalTaskService.complete(externalTask);
                })
                .open();

        client.subscribe("lock-duration-end")
                .lockDuration(1000) // 默认锁定时间为20秒，这里修改为1秒
                .handler((externalTask, externalTaskService) -> {

                    // 获取流程变量
                    LOGGER.info("execute msg of lock-duration-end ");
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
