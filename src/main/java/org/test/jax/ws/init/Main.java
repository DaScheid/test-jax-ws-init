package org.test.jax.ws.init;

import com.baeldung.soap.ws.client.EmployeeServiceTopDown;
import com.baeldung.soap.ws.client.EmployeeServiceTopDown_Service;
import org.apache.commons.lang3.time.StopWatch;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Queue<EmployeeServiceTopDown> pooledProxies = new ConcurrentLinkedQueue<>();
        Queue<EmployeeServiceTopDown> pooledProxies2 = new ConcurrentLinkedQueue<>();
        Queue<EmployeeServiceTopDown> pooledProxies3 = new ConcurrentLinkedQueue<>();
        Queue<EmployeeServiceTopDown> pooledProxies4 = new ConcurrentLinkedQueue<>();
        Queue<EmployeeServiceTopDown> pooledProxies5 = new ConcurrentLinkedQueue<>();
        StopWatch watch = new StopWatch();
        List<Future<?>> tasks = new ArrayList<>();

        URL url = Main.class.getClassLoader().getResource("employeeservicetopdown.wsdl");
        EmployeeServiceTopDown_Service employeeService_Service = new EmployeeServiceTopDown_Service(url);

        ExecutorService executorService = Executors.newFixedThreadPool(500);
        watch.start();
        for (int i = 0; i < 1000; i++) {
            tasks.add(executorService.submit(() -> pooledProxies.add(employeeService_Service.getEmployeeServiceTopDownSOAP())));
            tasks.add(executorService.submit(() -> pooledProxies2.add(employeeService_Service.getEmployeeServiceTopDownSOAP())));
            tasks.add(executorService.submit(() -> pooledProxies3.add(employeeService_Service.getEmployeeServiceTopDownSOAP())));
            tasks.add(executorService.submit(() -> pooledProxies4.add(employeeService_Service.getEmployeeServiceTopDownSOAP())));
            tasks.add(executorService.submit(() -> pooledProxies5.add(employeeService_Service.getEmployeeServiceTopDownSOAP())));
        }
        for (Future<?> task : tasks) {
            task.get();
        }
        watch.stop();
        executorService.shutdown();
        if (!executorService.awaitTermination(30, TimeUnit.SECONDS))
            executorService.shutdownNow();

        System.out.println("pooledProxies.size() = " + pooledProxies.size());
        System.out.println("pooledProxies2.size() = " + pooledProxies2.size());
        System.out.println("pooledProxies3.size() = " + pooledProxies3.size());
        System.out.println("pooledProxies4.size() = " + pooledProxies4.size());
        System.out.println("pooledProxies5.size() = " + pooledProxies5.size());
        System.out.println("Init took " + watch.getTime(TimeUnit.SECONDS) + " seconds");
    }
}