package com.tools.rpc;

import com.tools.beans.RpcServerConfig;
import com.tools.netty.rpc.server.core.NettyServer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import com.tools.annotation.NettyRpcService;

import java.util.Map;

@Component
public class RpcServer extends NettyServer implements ApplicationContextAware, InitializingBean, DisposableBean {

    @Autowired(required = true)
    public RpcServer(@Qualifier(value = "rpcServerConfig") RpcServerConfig rpcServerConfig) {
        super(rpcServerConfig.getServerAddress(), rpcServerConfig.getRegistryAddress());
    }

    @Override
    public void destroy() throws Exception {
        super.stop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(NettyRpcService.class);
        if(serviceBeanMap!=null){
            for (Object serviceBean : serviceBeanMap.values()) {
                NettyRpcService nettyRpcService=serviceBean.getClass().getAnnotation(NettyRpcService.class);
                String interfaceName = nettyRpcService.value().getName();
                String version = nettyRpcService.version();
                super.addService(interfaceName, version, serviceBean);
            }
        }
    }

}
