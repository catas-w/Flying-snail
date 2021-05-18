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
    RESPONSE_NOT_MATCH("响应与请求不匹配"),
    FAILED_TO_CONNECT_TO_SERVICE_REGISTRY("无法连接到注册中心"),
    REGISTER_SERVICE_FAILED("服务注册失败"),
    SERVICE_SCAN_PACKAGE_NOT_FOUND("启动类缺少ServiceScan注解"),
    UNKNOWN_ERROR("出现未知错误")
    ;

    private final String message;
}
