package com.zxsimple.monitor.monitor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;

/**
 * Created by zhaoxisheng on 2015/8/17.
 */
public class PerfMonitorInterceptor implements MethodInterceptor {

    private ServiceInvocationCounter serviceInvocationCounter;
    private boolean enableMonitor;

    public Object invoke(MethodInvocation invocation) throws Throwable {

        Object ret = null;

        String name = createInvocationTraceName(invocation);

        final String className = getClassName(invocation);
        final String methodName = getMethodName(invocation);
        StopWatch stopWatch = new StopWatch(className + methodName);
        stopWatch.start();

        try {
            ret = invocation.proceed();
            stopWatch.stop();
            if(enableMonitor)
                serviceInvocationCounter.accumulate(className, methodName, true, stopWatch.getLastTaskTimeMillis());
        } catch (Throwable throwable) {

            throwable.printStackTrace();
            if(enableMonitor)
                serviceInvocationCounter.accumulate(className, methodName, false, stopWatch.getLastTaskTimeMillis());
            throw throwable;
        }

        return ret;
    }

    protected String createInvocationTraceName(MethodInvocation invocation) {
        StringBuilder sb = new StringBuilder(invocation.getMethod().getDeclaringClass().getName());
        Method method = invocation.getMethod();
        Class clazz = method.getDeclaringClass();
        if(clazz.isInstance(invocation.getThis())) {
            clazz = invocation.getThis().getClass();
        }

        sb.append(clazz.getName());
        sb.append('.').append(method.getName());
        sb.append(' ');
        return sb.toString();
    }

    private String getClassName(MethodInvocation invocation) {
        return invocation.getMethod().getDeclaringClass().getSimpleName();
    }

    private String getMethodName(MethodInvocation invocation) {
        return invocation.getMethod().getName();
    }

    public void setServiceInvocationCounter(ServiceInvocationCounter serviceInvocationCounter) {
        this.serviceInvocationCounter = serviceInvocationCounter;
    }

    public void setEnableMonitor(boolean enableMonitor) {
        this.enableMonitor = enableMonitor;
    }
}
