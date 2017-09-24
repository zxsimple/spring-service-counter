# spring-service-counter

## usage:

1. Define performance conter properties

```
    <bean id="performanceInterceptor" class="com.juanpi.bis.monitor.BisPerfMonitorInterceptor">
        <property name="serviceInvocationCounter" ref="serviceInvocationCounter" />
        <property name="enableMonitor" value="true" />
    </bean>
    <bean id="serviceInvocationCounter" class="com.juanpi.bis.monitor.ServiceInvocationCounter">
        <constructor-arg ref="batchInterval" value="10000" />
        <property name="influxWriter" ref="influxWriter" />

    </bean>
    <bean id="clientInvocationCounter" class="com.juanpi.bis.monitor.ClientInvocationCounter" >
        <constructor-arg ref="batchInterval" value="10000" />
        <property name="influxWriter" ref="influxWriter" />
    </bean>

    <bean id="influxWriter" class="com.juanpi.bis.monitor.InfluxWriter" scope="prototype">
        <constructor-arg ref="influxApiUrl" value="http://192.168.16.96:8086" />
        <constructor-arg ref="influxDBName" value="bird" />
        <constructor-arg ref="influxUser" value="root" />
        <constructor-arg ref="influxPassword" value="root" />
    </bean>
```

2. Add interceptor to the target methods
```
   <bean name="/hello"
         class="org.springframework.remoting.caucho.HessianServiceExporter">
          <property name="service" ref="helloWorld" />
          <property name="serviceInterface" value="com.zxsimple.monitor.sample.IHellowWorld" />
          <property name="interceptors" ref="performanceInterceptor" />
   </bean>
```
