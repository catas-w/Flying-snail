package com.catas.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    SUCCESS(200, "调用成功"),
    FAILED(500, "调用失败"),
    METHOD_NOT_FOUND(500, "方法未找到"),
    CLASS_NOT_FOUND(500, "class not found");

    private final Integer code;

    private final String message;
}
