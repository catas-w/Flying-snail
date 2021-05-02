package com.catas.rpc.server;


import com.catas.rpc.entity.RPCRequest;
import com.catas.rpc.entity.RPCResponse;
import com.catas.rpc.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;

@Slf4j
public class RequestHandlerThread implements Runnable{

    private final Socket socket;

    private final RequestHandler requestHandler;

    private final ServiceRegistry serviceRegistry;


    public RequestHandlerThread(Socket socket, RequestHandler requestHandler, ServiceRegistry serviceRegistry) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            RPCRequest rpcRequest = (RPCRequest) inputStream.readObject();
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object res = requestHandler.handler(rpcRequest, service);
            outputStream.writeObject(RPCResponse.success(res));
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Error occurred when executing remote calling.");
        }
    }

}
