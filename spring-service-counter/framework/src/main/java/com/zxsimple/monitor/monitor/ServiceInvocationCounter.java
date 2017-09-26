package com.zxsimple.monitor.monitor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Zhaoxisheng on 2015/8/18.
 */
public class ServiceInvocationCounter {

    private Timer timer = null;

    private InfluxWriter influxWriter = null;
    private ConcurrentHashMap<String, Object> metrics = new ConcurrentHashMap<String, Object>();
    private HashMap<String, InvocationStat> invocationPackage = new HashMap<String, InvocationStat>();
    private String hostname;

    public ServiceInvocationCounter(int batchInterval) {

        timer = new Timer(this.getClass().getName());
        timer.schedule(new ReportCounterTimerTask(this), 0, batchInterval);
        try {
            hostname = InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException e) {
            hostname = "unkown";
        }
    }

    public void accumulate(String serviceName, String methodName, boolean success, long duration) {

        InvocationStat stat = invocationPackage.get(serviceName + methodName);

        if(stat != null) {

            if (success) {
                stat.setSuccessCounter(stat.getSuccessCounter() + 1);
            } else {
                stat.setFailsCounter(stat.getFailsCounter() + 1);
            }
            stat.setTotalExeDuration(stat.getTotalExeDuration() + duration);

        } else {
            stat = new InvocationStat();
            stat.setFailsCounter(success ? 0 : 1);
            stat.setSuccessCounter(success ? 1 : 0);
            stat.setServiceName(serviceName);
            stat.setMethodName(methodName);
            stat.setTotalExeDuration(duration);

            invocationPackage.put(serviceName + methodName, stat);
        }
    }

    public void resetCounter() {
        invocationPackage.clear();
    }

    public HashMap<String, InvocationStat> getInvocationPackage() {
        return invocationPackage;
    }


    public void setInfluxWriter(InfluxWriter influxWriter) {
        this.influxWriter = influxWriter;
    }

    private class ReportCounterTimerTask<T> extends TimerTask {

        ServiceInvocationCounter sic = null;

        public ReportCounterTimerTask(ServiceInvocationCounter sic) {
            this.sic = sic;
        }

        @Override
        public synchronized void run() {

            HashMap<String, InvocationStat> invocationPackage = sic.getInvocationPackage();


            for(Map.Entry entry : invocationPackage.entrySet()) {
                InvocationStat stat = (InvocationStat) entry.getValue();
                metrics.put("success", stat.getSuccessCounter());
                metrics.put("fails", stat.getFailsCounter());
                metrics.put("avg_duration", stat.getAvgExeDuration());

                influxWriter.putMetrics(metrics, stat.getServiceName() + "." + stat.getMethodName() + "@" + hostname);
            }


            metrics.clear();
            sic.resetCounter();
        }
    }
}
