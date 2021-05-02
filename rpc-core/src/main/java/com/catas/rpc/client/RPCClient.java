package com.catas.rpc.client;


import com.catas.rpc.common.RPCError;
import com.catas.rpc.common.ResponseCode;
import com.catas.rpc.entity.RPCRequest;
import com.catas.rpc.entity.RPCResponse;
import com.catas.rpc.exception.RPCException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
public class RPCClient {

    public Object sendRequest(RPCRequest request, String host, Integer port) {
        try {
            Socket socket = new Socket(host, port);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream.writeObject(request);
            outputStream.flush();
            RPCResponse response = (RPCResponse) inputStream.readObject();
            if (response == null) {
                log.info("服务调用失败, service: {}", request.getInterfaceName());
                throw new RPCException(RPCError.SERVICE_INVOCATION_FAILURE, "service: " + request.getInterfaceName());
            }
            if (response.getStatus() == null || !response.getStatus().equals(ResponseCode.SUCCESS.getCode())) {
                log.info("服务调用失败, service: {}, respnes: {}", request.getInterfaceName(), response);
                throw new RPCException(RPCError.SERVICE_INVOCATION_FAILURE, "service: " + request.getInterfaceName());
            }


            return response;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            log.error("Error occurred during calling remote procedure.");
            return null;
        }
    }
}
