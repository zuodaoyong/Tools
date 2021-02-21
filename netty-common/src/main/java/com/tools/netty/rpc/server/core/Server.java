package com.tools.netty.rpc.server.core;

public interface Server {


    /**
     * 启动
     * @throws Exception
     */
    void start() throws Exception;

    /**
     * 停止
     * @throws Exception
     */
    void stop() throws Exception;
}
