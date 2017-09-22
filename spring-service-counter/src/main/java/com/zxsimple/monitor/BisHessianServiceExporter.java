package com.zxsimple.monitor;

import com.zxsimple.monitor.monitor.ClientInvocationCounter;
import org.springframework.remoting.caucho.HessianServiceExporter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Zhaoxisheng on 2015/8/27.
 */
public class BisHessianServiceExporter extends HessianServiceExporter {

    private ClientInvocationCounter clientInvocationCounter;
    private boolean enableMonitor;

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        super.handleRequest(request, response);

        //Counting invokes from remote IP
        if(enableMonitor) {
            clientInvocationCounter.accumulate(request.getRemoteAddr());
        }
    }

    public void setClientInvocationCounter(ClientInvocationCounter clientInvocationCounter) {
        this.clientInvocationCounter = clientInvocationCounter;
    }

    public void setEnableMonitor(boolean enableMonitor) {
        this.enableMonitor = enableMonitor;
    }
}
