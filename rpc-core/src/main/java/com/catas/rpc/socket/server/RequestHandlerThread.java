package com.catas.rpc.socket.server;


import com.catas.rpc.RequestHandler;
import com.catas.rpc.entity.RPCRequest;
import com.catas.rpc.entity.RPCResponse;
import com.catas.rpc.registry.ServiceRegistry;
import com.catas.rpc.serializer.CommonSerializer;
import com.catas.rpc.socket.util.ObjectReader;
import com.catas.rpc.socket.util.ObjectWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
public class RequestHandlerThread implements Runnable{

    private final Socket socket;

    private final RequestHandler requestHandler;

    private final ServiceRegistry serviceRegistry;

    private final CommonSerializer serializer;

    public RequestHandlerThread(Socket socket, RequestHandler requestHandler, ServiceRegistry serviceRegistry, CommonSerializer serializer) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serviceRegistry = serviceRegistry;
        this.serializer = serializer;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            // RPCRequest rpcRequest = (RPCRequest) inputStream.readObject();
            RPCRequest rpcRequest = (RPCRequest) ObjectReader.readObject(inputStream);
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object res = requestHandler.handler(rpcRequest, service);

            RPCResponse<Object> rpcResponse = RPCResponse.success(res, rpcRequest.getRequestId());
            ObjectWriter.writeObject(outputStream, rpcResponse, serializer);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Error occurred when executing remote calling.");
        }
    }

}
