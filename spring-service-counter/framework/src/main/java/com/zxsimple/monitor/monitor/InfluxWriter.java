package com.zxsimple.monitor.monitor;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Zhaoxisheng on 2015/8/19.
 */
public class InfluxWriter<T> {

    private InfluxDB influxDB = null;
    private BatchPoints batchPoints = null;

    private String influxApiUrl;
    private String influxUser;
    private String influxPassword;
    private String influxDBName;

    public InfluxWriter(String influxApiUrl, String influxUser, String influxPassword, String influxDBName) {

        this.batchPoints = BatchPoints.database(influxDBName)
                .tag("async", "true")
                .retentionPolicy("default")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();

        this.influxApiUrl = influxApiUrl;
        this.influxUser = influxUser;
        this.influxPassword = influxPassword;
        this.influxDBName = influxDBName;

        connect();

    }

    public void putMetrics(ConcurrentHashMap<String, T> metrics, String measurement) {

        Point.Builder pointBuilder = Point.measurement(measurement);
        pointBuilder.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);

        for(Map.Entry<String, T> metric : metrics.entrySet()) {

            pointBuilder.field(metric.getKey(), metric.getValue());
        }
        Point point = pointBuilder.build();

        batchPoints.point(point);

        try {
            influxDB.write(batchPoints);
        } catch (Exception e) {

            System.out.println("###### " + e.getMessage());
            if(e.getMessage().equals("timeout")) {
                connect();
            }
        }
    }

    private void connect() {
        this.influxDB = InfluxDBFactory.connect(influxApiUrl, influxUser, influxPassword);
    }

    public static void main(String [] args) {

        InfluxWriter writer = new InfluxWriter("http://192.168.30.16:8086", "bird", "root", "root");

        for(int i = 0; i < 100; i++) {

            ConcurrentHashMap<String, Double> metrics = new ConcurrentHashMap<String, Double>();
            metrics.put("fails", new Random().nextDouble());
            metrics.put("success", new Random().nextDouble());
            metrics.put("avg duration", new Random().nextDouble());
            writer.putMetrics(metrics, "services");

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
