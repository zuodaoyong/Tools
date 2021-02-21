package com.tools.netty.rpc.server.core;

import com.tools.netty.rpc.server.registry.ServiceRegistry;
import com.tools.netty.rpc.server.utils.CommonUtils;
import com.tools.netty.rpc.server.utils.ThreadPoolUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class NettyServer implements Server {


    private Thread thread;

    private String serverAddress;//注册中心地址

    private ServiceRegistry serviceRegistry;//注册中心

    private Map<String, Object> serviceMap = new HashMap<>();

    public NettyServer(String serverAddress, String registryAddress){
        this.serverAddress=serverAddress;
        this.serviceRegistry=new ServiceRegistry(registryAddress);
    }

    public void addService(String interfaceName, String version, Object serviceBean) {
        log.info("Adding service, interface: {}, version: {}, bean：{}", interfaceName, version, serviceBean);
        String serviceKey = CommonUtils.makeServiceKey(interfaceName, version);
        serviceMap.put(serviceKey,serviceBean);

    }

    @Override
    public void start() throws Exception {
        this.thread=new Thread(()->{
            ThreadPoolExecutor threadPoolExecutor = ThreadPoolUtil.makeServerThreadPool(
                    NettyServer.class.getSimpleName(), 16, 32);

            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                        .childHandler(new RpcServerInitializer(serviceMap, threadPoolExecutor))
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);

                String[] array = serverAddress.split(":");
                String host = array[0];
                int port = Integer.parseInt(array[1]);
                ChannelFuture future = bootstrap.bind(host, port).sync();

                if (serviceRegistry != null) {
                    serviceRegistry.registerService(host, port, serviceMap);
                }
                log.info("Server started on port {}", port);
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                if (e instanceof InterruptedException) {
                    log.info("Rpc server remoting server stop");
                } else {
                    log.error("Rpc server remoting server error", e);
                }
            } finally {
                try {
                    serviceRegistry.unregisterService();
                    workerGroup.shutdownGracefully();
                    bossGroup.shutdownGracefully();
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                }
            }
        });
    }

    @Override
    public void stop() throws Exception {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }
}
