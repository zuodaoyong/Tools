package com.tools.netty.rpc.server.zookeeper;

import com.tools.netty.rpc.common.Constant;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;

import java.util.List;

public class CuratorClient {

    private CuratorFramework client;

    public CuratorClient(String connectString, String namespace, int sessionTimeout, int connectionTimeout){
        this.client=CuratorFrameworkFactory.builder().namespace(namespace).connectString(connectString)
                .sessionTimeoutMs(sessionTimeout).connectionTimeoutMs(connectionTimeout)
                .retryPolicy(new ExponentialBackoffRetry(1000,10))
                .build();
        this.client.start();
    }

    public CuratorClient(String connectString, int timeout){
        this(connectString, Constant.ZK_NAMESPACE,Constant.ZK_SESSION_TIMEOUT,timeout);
    }

    public CuratorClient(String connectString){
        this(connectString,Constant.ZK_NAMESPACE,Constant.ZK_SESSION_TIMEOUT,Constant.ZK_CONNECTION_TIMEOUT);
    }

    public CuratorFramework getClient() {
        return client;
    }

    public void addConnectionStateListener(ConnectionStateListener connectionStateListener){
        this.client.getConnectionStateListenable().addListener(connectionStateListener);
    }

    public void createPathData(String path, byte[] data) throws Exception{
        this.client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath(path,data);
    }

    public void updatePathData(String path, byte[] data) throws Exception{
        this.client.setData().forPath(path,data);
    }

    public void deletePath(String path) throws Exception{
        this.client.delete().forPath(path);
    }

    public void watchNode(String path, Watcher watcher) throws Exception {
        this.client.getData().usingWatcher(watcher).forPath(path);
    }

    public byte[] getData(String path) throws Exception {
        return this.client.getData().forPath(path);
    }

    public List<String> getChildren(String path) throws Exception {
        return this.client.getChildren().forPath(path);
    }

    public void watchTreeNode(String path, TreeCacheListener listener) {
        TreeCache treeCache = new TreeCache(client, path);
        treeCache.getListenable().addListener(listener);
    }

    public void watchPathChildrenNode(String path, PathChildrenCacheListener listener) throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, path, true);
        //BUILD_INITIAL_CACHE 代表使用同步的方式进行缓存初始化。
        pathChildrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        pathChildrenCache.getListenable().addListener(listener);
    }

    public void close(){
        this.client.close();
    }
}
