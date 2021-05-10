package com.catas.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RPCError {

    SERVICE_INVOCATION_FAILURE("服务调用失败"),
    SERVICE_NOT_FOUND("找不到对应的服务"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("服务为实现接口"),
    UNKNOWN_PROTOCOL("无法识别协议"),
    UNKNOWN_SERIALIZER("无法识别序列化器"),
    UNKNOWN_PACKAGE_TYPE("无法识别数据包类型"),
    SERIALIZER_NOT_FOUND("找不到序列化器"),
    RESPONSE_NOT_FOUND("响应与请求不匹配")
    ;

    private final String message;
}
