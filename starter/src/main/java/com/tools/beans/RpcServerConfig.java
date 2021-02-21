package com.tools.beans;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component("rpcServerConfig")
@ConfigurationProperties(prefix="rpc")
public class RpcServerConfig {

    private String serverAddress;

    private String registryAddress;
}
