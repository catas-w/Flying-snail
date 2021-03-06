package com.catas.rpc.transport.socket.client;


import com.catas.rpc.loadbalancer.LoadBalancer;
import com.catas.rpc.loadbalancer.RandomLoadBalancer;
import com.catas.rpc.provider.ServiceProvider;
import com.catas.rpc.registry.nacos.NacosServiceDiscovery;
import com.catas.rpc.registry.nacos.NacosServiceRegistry;
import com.catas.rpc.registry.ServiceDiscovery;
import com.catas.rpc.registry.ServiceRegistry;
import com.catas.rpc.transport.RPCClient;
import com.catas.rpc.enumeration.RPCError;
import com.catas.rpc.enumeration.ResponseCode;
import com.catas.rpc.entity.RPCRequest;
import com.catas.rpc.entity.RPCResponse;
import com.catas.rpc.exception.RPCException;
import com.catas.rpc.serializer.CommonSerializer;
import com.catas.rpc.transport.netty.client.NettyClient;
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

    private final CommonSerializer serializer;

    private final ServiceDiscovery serviceDiscovery;

    // public SocketClient() {
    //     this(DEFAULT_SERIALIZER, new NacosServiceDiscovery());
    // }
    //
    // public SocketClient(ServiceDiscovery serviceDiscovery) {
    //     this(DEFAULT_SERIALIZER, serviceDiscovery);
    // }
    //
    // public SocketClient(Integer serializer) {
    //     this(serializer, new NacosServiceDiscovery());
    // }
    //
    // public SocketClient(Integer serializer, ServiceDiscovery serviceDiscovery) {
    //     this.serviceDiscovery = serviceDiscovery;
    //     this.serializer = CommonSerializer.getByCode(serializer);
    // }

    private SocketClient(Builder builder) {
        this.serializer = builder.serializer;
        this.serviceDiscovery = builder.serviceDiscovery;
    }

    public static class Builder {
        private CommonSerializer serializer = CommonSerializer.getByCode(DEFAULT_SERIALIZER);
        private ServiceDiscovery serviceDiscovery = new NacosServiceDiscovery();

        public Builder serializer(int serializerCode) {
            this.serializer = CommonSerializer.getByCode(DEFAULT_SERIALIZER);
            return this;
        }

        public Builder serviceDiscovery(ServiceDiscovery serviceDiscovery) {
            this.serviceDiscovery = serviceDiscovery;
            return this;
        }

        public SocketClient build() {
            return new SocketClient(this);
        }
    }

    @Override
    public Object sendRequest(RPCRequest request) {
        if (serializer == null) {
            log.error("????????????????????????");
            throw new RPCException(RPCError.SERIALIZER_NOT_FOUND);
        }
        // ????????????
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
                log.info("??????????????????, service: {}", request.getInterfaceName());
                throw new RPCException(RPCError.SERVICE_INVOCATION_FAILURE, "service: " + request.getInterfaceName());
            }
            if (response.getStatus() == null || !response.getStatus().equals(ResponseCode.SUCCESS.getCode())) {
                log.info("??????????????????, service: {}, respnes: {}", request.getInterfaceName(), response);
                throw new RPCException(RPCError.SERVICE_INVOCATION_FAILURE, "service: " + request.getInterfaceName());
            }
            // ??????
            RPCMessageChecker.check(request, response);
            return response;

        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error occurred during calling remote procedure.");
            return null;
        }
    }

}
