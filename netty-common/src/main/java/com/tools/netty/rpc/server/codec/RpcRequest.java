package com.tools.netty.rpc.server.codec;

import lombok.Data;

import java.io.Serializable;

@Data
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 7806235528851548556L;

    private String requestId;

    private String className;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] parameters;

    private String version;
}
