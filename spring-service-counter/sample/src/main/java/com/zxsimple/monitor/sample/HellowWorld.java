package com.zxsimple.monitor.sample;

/**
 * Created by admin on 2015/7/29.
 */
public class HellowWorld implements IHellowWorld {

    @Override
    public String say(String name) {
        return "Hellow " + name;
    }
}
