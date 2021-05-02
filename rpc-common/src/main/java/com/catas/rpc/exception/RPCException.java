package com.catas.rpc.exception;

import com.catas.rpc.common.RPCError;

public class RPCException extends RuntimeException{

    public RPCException(RPCError error, String detail) {
        super(error.getMessage() + ":" + detail);
    }

    public RPCException(String message, Throwable cause) {
        super(message, cause);
    }

    public RPCException(RPCError error) {
        super(error.getMessage());
    }
}
