package com.catas.rpc.entity;

import com.catas.rpc.enumeration.ResponseCode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class RPCResponse<T> implements Serializable {

    private Integer status;

    private String message;

    private T data;

    public static <K> RPCResponse<K> success(K data) {
        RPCResponse<K> response = new RPCResponse<>();
        response.setData(data);
        response.setStatus(ResponseCode.SUCCESS.getCode());
        return response;
    }

    public static <K> RPCResponse<K> failed(ResponseCode code) {
        RPCResponse<K> response = new RPCResponse<>();
        response.setStatus(code.getCode());
        response.setMessage(code.getMessage());
        return response;
    }
}
