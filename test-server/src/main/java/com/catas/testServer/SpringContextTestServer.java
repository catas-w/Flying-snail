package com.catas.testServer;


import com.catas.rpc.annotation.RPCScan;
import com.catas.rpc.transport.RPCServer;
import com.catas.rpc.transport.netty.server.NettyServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

@RPCScan()
public class SpringContextTestServer {

    @Bean("rpcServer")
    public RPCServer setRPCServer() {
        return new NettyServer.Builder()
                .port(9001)
                // .serviceRegistry(new ZkServiceRegistry())
                .build();
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringContextTestServer.class);
        RPCServer rpcServer = (RPCServer) context.getBean("rpcServer");

        rpcServer.start();
    }
}
