package com.zxsimple.monitor.monitor;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Zhaoxisheng on 2015/8/18.
 */
public class ClientInvocationCounter {

    private Timer timer = null;

    private InfluxWriter influxWriter = null;
    private ConcurrentHashMap<String, Object> metrics = new ConcurrentHashMap<String, Object>();
    private ConcurrentHashMap<String, Integer> counterPackage = new ConcurrentHashMap<String, Integer>();

    public ClientInvocationCounter(int batchInterval) {

        timer = new Timer(this.getClass().getName());
        timer.schedule(new ReportCounterTimerTask(this), 0, batchInterval);
    }

    public void accumulate(String ipAddress) {

        Integer counter = counterPackage.get(ipAddress);
        counter = counter == null ? 0 : counter;
        counter++;
        counterPackage.put(ipAddress, counter);
    }

    public void resetCounter() {
        counterPackage.clear();
    }

    public ConcurrentHashMap<String, Integer> getCounterPackage() {
        return counterPackage;
    }


    public void setInfluxWriter(InfluxWriter influxWriter) {
        this.influxWriter = influxWriter;
    }

    private class ReportCounterTimerTask<T> extends TimerTask {

        ClientInvocationCounter sic = null;

        public ReportCounterTimerTask(ClientInvocationCounter sic) {
            this.sic = sic;
        }

        @Override
        public synchronized void run() {

            ConcurrentHashMap<String, Integer> counterPackage = sic.getCounterPackage();

            for(Map.Entry entry : counterPackage.entrySet()) {

                String ipAddress = (String) entry.getKey();
                Integer counter = (Integer) entry.getValue();
                metrics.put("invokes", counter);

                influxWriter.putMetrics(metrics, ipAddress);
            }

            metrics.clear();
            sic.resetCounter();
        }
    }
}
