package com.catas.rpc.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description: rpc request entity
 */
@Data
@Builder
public class RPCRequest implements Serializable {

    private String interfaceName;

    private String methodName;

    private Object[] arguments;

    private Class<?>[] argTypes;
}
