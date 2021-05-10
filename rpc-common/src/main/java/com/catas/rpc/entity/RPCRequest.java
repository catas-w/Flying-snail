package com.catas.rpc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description: rpc request entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RPCRequest implements Serializable {

    private String requestId;

    private String interfaceName;

    private String methodName;

    private Object[] arguments;

    private Class<?>[] argTypes;
}
