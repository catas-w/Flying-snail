# Flying-snail RPC framework

## Introduction
Flying-snail is an RPC(Remote procedure call) framework based on Java. 

## Features
- Flying-snail could work through traditional socket(BIO) or Netty(NIO, default).
- Following serialization method are supported: Json, Kryo, Hessian(default), Protostuff.
- Support service register center: Nacos(default) and Zookeeper.
- Load balance for service provide.
- No need to worry about TCP adhesive package.

## Usage

### 1 Define the service interface
```java
public interface AddService {
    // define your own service
    public Integer add(Integer a, Integer b);
}
```

### 2 Implement service in server-end
```java
// add this goddamn notation
@RPCService
public class AddServiceImpl implements AddService {
    @Override
    public Integer add(Integer a, Integer b) {
        return a + b;
    }
}
```

### 3 Run RPC service in server-end
```properties
# select your register center and make sure they're running on the port your configured here
snail.zookeeper.address=127.0.0.1:12444
snail.nacos.address=127.0.0.1:12345
```
```java
// add this notation
@RPCServiceScan
public class NettyTestServer {
    public static void main(String[] args) {
        // Use Netty as transport framework
        NettyServer nettyServer = new NettyServer.Builder()
                .port(9001)
                // Ensure zookeeper or nacos is running
                .serviceRegistry(new ZkServiceRegistry())
                .build();
        nettyServer.scanService();
        // start the service
        nettyServer.start();
    }
}
```

### 4 Invoke remote procedure call
```java
public class NettyTestClient {
    public static void main(String[] args) {
        // start client
        NettyClient nettyClient = new NettyClient.Builder()
                .serviceDiscovery(new ZkServiceDiscovery())
                .serializer(SerializerCode.HESSIAN.getCode())
                .build();

        RPCClientProxy clientProxy = new RPCClientProxy(nettyClient);
        AddService addProxy = clientProxy.getProxy(AddService.class);
        // now we can invoke this stupid function remotely
        Integer res = addProxy.add(23, 12);
    }
}
```

