package com.zxsimple.monitor.monitor;

/**
 * Created by Zhaoxisheng on 2015/8/19.
 */
public class InvocationStat {

    private String serviceName;
    private String methodName;
    private long failsCounter;
    private long successCounter;
    private double totalExeDuration;
    private double avgExeDuration;

    public double getTotalExeDuration() {
        return totalExeDuration;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setFailsCounter(long failsCounter) {
        this.failsCounter = failsCounter;
    }

    public void setSuccessCounter(long successCounter) {
        this.successCounter = successCounter;
    }

    public void setTotalExeDuration(double totalExeDuration) {
        this.totalExeDuration = totalExeDuration;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public long getFailsCounter() {
        return failsCounter;
    }

    public long getSuccessCounter() {
        return successCounter;
    }

    public double getAvgExeDuration() {
        if (successCounter + failsCounter == 0)
            return 0;
        else
            return (totalExeDuration / (successCounter + failsCounter));
    }
}
