package com.catas.rpc.socket.client;


import com.catas.rpc.RPCClient;
import com.catas.rpc.enumeration.RPCError;
import com.catas.rpc.enumeration.ResponseCode;
import com.catas.rpc.entity.RPCRequest;
import com.catas.rpc.entity.RPCResponse;
import com.catas.rpc.exception.RPCException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
public class SocketClient implements RPCClient{

    private final String host;

    private final Integer port;

    public SocketClient(String hostAddr, Integer port) {
        this.host = hostAddr;
        this.port = port;
    }

    @Override
    public Object sendRequest(RPCRequest request) {
        try {
            Socket socket = new Socket(host, port);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream.writeObject(request);
            outputStream.flush();
            // RPCResponse response = (RPCResponse) inputStream.readObject();
            Object resp = inputStream.readObject();
            System.out.println(resp);
            RPCResponse response = (RPCResponse) resp;
            if (response == null) {
                log.info("服务调用失败, service: {}", request.getInterfaceName());
                throw new RPCException(RPCError.SERVICE_INVOCATION_FAILURE, "service: " + request.getInterfaceName());
            }
            if (response.getStatus() == null || !response.getStatus().equals(ResponseCode.SUCCESS.getCode())) {
                log.info("服务调用失败, service: {}, respnes: {}", request.getInterfaceName(), response);
                throw new RPCException(RPCError.SERVICE_INVOCATION_FAILURE, "service: " + request.getInterfaceName());
            }


            return response.getData();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            log.error("Error occurred during calling remote procedure.");
            return null;
        }
    }


}
