package com.zxsimple.monitor.sample.client;


import com.caucho.hessian.client.HessianProxyFactory;
import com.zxsimple.monitor.sample.IHellowWorld;

import java.net.MalformedURLException;

public class HessianClient {

    public static void main(String[] args) {

        String url = "http://localhost:8080/sample/hello";


        HessianProxyFactory factory = new HessianProxyFactory();

        IHellowWorld getService;
        try {
            getService = (IHellowWorld)factory.create(IHellowWorld.class, url);
            System.out.println(getService.say("Sanders"));


        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
