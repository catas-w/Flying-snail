package com.catas.rpc.transport.netty.client;

import com.catas.rpc.entity.RPCResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class UnprocessedRequests {

    private static ConcurrentHashMap<String, CompletableFuture<RPCResponse>> unprocessedRespFutures = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RPCResponse> future) {
        unprocessedRespFutures.put(requestId, future);
    }

    public void remove(String requestId) {
        unprocessedRespFutures.remove(requestId);
    }

    public void complete(RPCResponse response) {
        CompletableFuture<RPCResponse> future = unprocessedRespFutures.remove(response.getRequestId());
        if (null != future) {
            future.complete(response);
        } else {
            throw new IllegalStateException();
        }
    }
}
