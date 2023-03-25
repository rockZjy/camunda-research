package org.example;

import org.camunda.bpm.client.ExternalTaskClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class TimerStuckTest {
    /**
     * Related BPMN Name: timer-stuck-test.bpmn
     */

    private final static Logger LOGGER = Logger.getLogger(TimerStuckTest.class.getName());

    public static void main(String[] args) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        ExternalTaskClient client = ExternalTaskClient.create()
                //.asyncResponseTimeout(10000)
                .baseUrl("http://localhost:8080/engine-rest")
                .build();

        client.subscribe("timer-start")
                .handler((externalTask, externalTaskService) -> {
                    Date date = new Date(System.currentTimeMillis());
                    System.out.println(formatter.format(date)+"# execute timer-start service");
                    try {
                        Thread.sleep(5*1000);
                        Date date2 = new Date(System.currentTimeMillis());
                        System.out.println(formatter.format(date2)+"# complete timer-start service");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    externalTaskService.complete(externalTask);
                })
                .open();

        client.subscribe("timer-end")
                .handler((externalTask, externalTaskService) -> {
                    Date date = new Date(System.currentTimeMillis());
                    System.out.println(formatter.format(date)+"# execute timer-end service");
                    try {
                        Thread.sleep(5*1000);
                        Date date2 = new Date(System.currentTimeMillis());
                        System.out.println(formatter.format(date2)+"# complete timer-end service");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    externalTaskService.complete(externalTask);
                })
                .open();
    }

}
