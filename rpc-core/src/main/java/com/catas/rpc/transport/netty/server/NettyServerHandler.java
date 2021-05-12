package com.catas.rpc.transport.netty.server;

import com.catas.rpc.handler.RequestHandler;
import com.catas.rpc.entity.RPCRequest;
import com.catas.rpc.entity.RPCResponse;
import com.catas.rpc.provider.ServiceProviderImpl;
import com.catas.rpc.provider.ServiceProvider;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RPCRequest> {

    private static final RequestHandler requestHandler;
    private static final ServiceProvider SERVICE_PROVIDER;

    static {
        requestHandler = new RequestHandler();
        SERVICE_PROVIDER = new ServiceProviderImpl();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RPCRequest rpcRequest) throws Exception {

        try {
            log.info("服务器收到请求");
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = SERVICE_PROVIDER.getServiceProvider(interfaceName);
            Object result = requestHandler.handler(rpcRequest, service);
            ChannelFuture future = channelHandlerContext.writeAndFlush(RPCResponse.success(result, rpcRequest.getRequestId()));
            future.addListener(ChannelFutureListener.CLOSE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(rpcRequest);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("调用时错误发生");
        cause.printStackTrace();
        ctx.close();
    }
}
