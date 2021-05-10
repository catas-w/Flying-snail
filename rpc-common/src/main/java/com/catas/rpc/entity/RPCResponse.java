package com.catas.rpc.entity;

import com.catas.rpc.enumeration.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RPCResponse<T> implements Serializable {

    private String requestId;

    private Integer status;

    private String message;

    private T data;

    public static <K> RPCResponse<K> success(K data, String requestId) {
        RPCResponse<K> response = new RPCResponse<>();
        response.setData(data);
        response.setStatus(ResponseCode.SUCCESS.getCode());
        response.setRequestId(requestId);
        return response;
    }

    public static <K> RPCResponse<K> failed(ResponseCode code, String requestId) {
        RPCResponse<K> response = new RPCResponse<>();
        response.setStatus(code.getCode());
        response.setMessage(code.getMessage());
        response.setRequestId(requestId);
        return response;
    }
}
