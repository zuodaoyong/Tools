package com.tools.netty.rpc.server.codec;

import lombok.Data;

import java.io.Serializable;

@Data
public class RpcResponse implements Serializable {
    private static final long serialVersionUID = -7274260711756650836L;

    private String requestId;

    private String error;

    private Object result;
}
