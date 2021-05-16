package com.catas.rpc.transport.socket.client;


import com.catas.rpc.enumeration.SerializerCode;
import com.catas.rpc.loadbalancer.LoadBalancer;
import com.catas.rpc.loadbalancer.RandomLoadBalancer;
import com.catas.rpc.registry.NacosServiceDiscovery;
import com.catas.rpc.registry.NacosServiceRegistry;
import com.catas.rpc.registry.ServiceDiscovery;
import com.catas.rpc.registry.ServiceRegistry;
import com.catas.rpc.transport.RPCClient;
import com.catas.rpc.enumeration.RPCError;
import com.catas.rpc.enumeration.ResponseCode;
import com.catas.rpc.entity.RPCRequest;
import com.catas.rpc.entity.RPCResponse;
import com.catas.rpc.exception.RPCException;
import com.catas.rpc.serializer.CommonSerializer;
import com.catas.rpc.transport.socket.util.ObjectReader;
import com.catas.rpc.transport.socket.util.ObjectWriter;
import com.catas.rpc.util.RPCMessageChecker;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

@Slf4j
public class SocketClient implements RPCClient{

    private CommonSerializer serializer;

    private final ServiceRegistry serviceRegistry;

    private final ServiceDiscovery serviceDiscovery;

    private final LoadBalancer loadBalancer;

    public SocketClient() {
        this(DEFAULT_SERIALIZER, new RandomLoadBalancer());
    }

    public SocketClient(Integer serializer) {
        this(serializer, new RandomLoadBalancer());
    }

    public SocketClient(LoadBalancer loadBalancer) {
        this(DEFAULT_SERIALIZER, loadBalancer);
    }

    public SocketClient(Integer serializer, LoadBalancer loadBalancer) {
        this.serviceRegistry = new NacosServiceRegistry();
        this.serviceDiscovery = new NacosServiceDiscovery();
        this.serializer = CommonSerializer.getByCode(serializer);
        this.loadBalancer = loadBalancer;
    }

    @Override
    public Object sendRequest(RPCRequest request) {
        if (serializer == null) {
            log.error("序列化器不能为空");
            throw new RPCException(RPCError.SERIALIZER_NOT_FOUND);
        }
        // 服务发现
        InetSocketAddress socketAddress = serviceDiscovery.lookupService(request.getInterfaceName());

        try {
            Socket socket = new Socket();
            socket.connect(socketAddress);

            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            ObjectWriter.writeObject(outputStream, request, serializer);

            Object resp = ObjectReader.readObject(inputStream);
            RPCResponse response = (RPCResponse) resp;

            if (response == null) {
                log.info("服务调用失败, service: {}", request.getInterfaceName());
                throw new RPCException(RPCError.SERVICE_INVOCATION_FAILURE, "service: " + request.getInterfaceName());
            }
            if (response.getStatus() == null || !response.getStatus().equals(ResponseCode.SUCCESS.getCode())) {
                log.info("服务调用失败, service: {}, respnes: {}", request.getInterfaceName(), response);
                throw new RPCException(RPCError.SERVICE_INVOCATION_FAILURE, "service: " + request.getInterfaceName());
            }
            // 检查
            RPCMessageChecker.check(request, response);
            return response;

        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error occurred during calling remote procedure.");
            return null;
        }
    }

}
