package com.tools.netty.rpc.server.registry;

import com.tools.netty.rpc.common.Constant;
import com.tools.netty.rpc.server.protocol.RpcProtocol;
import com.tools.netty.rpc.server.protocol.RpcServiceInfo;
import com.tools.netty.rpc.server.zookeeper.CuratorClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 服务注册
 */
@Slf4j
public class ServiceRegistry {

    private CuratorClient curatorClient;
    private List<String> pathList=new ArrayList<>();

    public ServiceRegistry(String registryAddress){
        this.curatorClient=new CuratorClient(registryAddress,5000);
    }

    /**
     * 注册服务
     * @param host
     * @param port
     * @param serviceMap
     */
    public void registerService(String host, int port, Map<String, Object> serviceMap) {
        List<RpcServiceInfo> serviceInfoList=new ArrayList<>();
        for(String key:serviceMap.keySet()){
            String[] serviceInfo = key.split(Constant.SERVICE_CONCAT_TOKEN);
            if(serviceInfo.length>0){
                RpcServiceInfo rpcServiceInfo=new RpcServiceInfo();
                rpcServiceInfo.setServiceName(serviceInfo[0]);
                rpcServiceInfo.setVersion("");
                if(serviceInfo.length>1){
                    rpcServiceInfo.setVersion(serviceInfo[1]);
                }
                log.info("Register new service: {}",key);
                serviceInfoList.add(rpcServiceInfo);
            } else {
                log.error("Can not get service name and version: {}",key);
            }
        }
        try{
            RpcProtocol rpcProtocol=new RpcProtocol();
            rpcProtocol.setHost(host);
            rpcProtocol.setPort(port);
            rpcProtocol.setServiceInfoList(serviceInfoList);

            String json = rpcProtocol.toString();
            byte[] bytes = json.getBytes();
            String path=Constant.ZK_DATA_PATH+"-"+rpcProtocol.hashCode();
            this.curatorClient.createPathData(path,bytes);
            log.info("Register {} new service, host: {}, port: {}", serviceInfoList.size(), host, port);
            pathList.add(path);
        }catch (Exception e){
            log.error("Register service fail, exception: {}", e.getMessage());
        }
        this.curatorClient.addConnectionStateListener(new ConnectionStateListener(){
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                if(newState==ConnectionState.RECONNECTED){
                    log.info("Connection state: {}, register service after reconnected", newState);
                    registerService(host, port, serviceMap);
                }
            }
        });
    }

    /**
     * 释放服务
     */
    public void unregisterService() {
        log.info("Unregister all service");
        for (String path : pathList) {
            try {
                this.curatorClient.deletePath(path);
            } catch (Exception ex) {
                log.error("Delete service path error: " + ex.getMessage());
            }
        }
        this.curatorClient.close();
    }

}
