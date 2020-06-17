package com.itheima.test;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author 张鹏
 * @date 2020/6/17 14:50
 */
public class TestPathChildrenCache {

    private CuratorFramework client = null;

    @Before
    public void open() {
        ExponentialBackoffRetry backoffRetry = new ExponentialBackoffRetry(3000, 2);
        client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", 3000, 1000, backoffRetry);
        client.start();
    }

    @Test
    public void test01() throws Exception {
        /*
            创建监听指定节点的子节点变化对象
            参数3：true表示客户端在接收到节点列表变更的同时，也能获取到节点的数据内容
                    如果为false，则无法获取到数据内容
         */
        PathChildrenCache cache = new PathChildrenCache(client, "/a", true);


        /*
            NORMAL：表示普通启动方式，在启动时缓存子节点数据
            POST_INITIALIZED_EVENT：在启动时缓存子节点数据，提示初始化
            BUILD_INITIAL_CACHE：在启动时什么都不会输出
         */
        cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);

        System.out.println(cache.getCurrentData());

        // 添加监听对象
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curator, PathChildrenCacheEvent event) throws Exception {
                if (event.getType() == PathChildrenCacheEvent.Type.INITIALIZED) {
                    System.out.println("初始化操作...");
                } else if (event.getType() == PathChildrenCacheEvent.Type.CONNECTION_SUSPENDED) {
                    System.out.println("连接失效...");
                } else if (event.getType() == PathChildrenCacheEvent.Type.CONNECTION_RECONNECTED) {
                    System.out.println("重写连接...");
                } else if (event.getType() == PathChildrenCacheEvent.Type.CONNECTION_LOST) {
                    System.out.println("连接失效之后稍等一会执行...");
                } else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_UPDATED) {  // 检查子节点是否更新
                    System.out.println("更新的节点：" + event.getData().getPath());
                    System.out.println("更新的数据：" + new String(event.getData().getData()));
                } else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {  // 检查子节点是否删除
                    System.out.println("删除的节点：" + event.getData().getPath());
                    System.out.println("删除的数据：" + new String(event.getData().getData()));
                } else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {  // 检查子节点是否添加
                    System.out.println("添加的节点：" + event.getData().getPath());
                    System.out.println("添加的数据：" + new String(event.getData().getData()));
                }
            }
        });

        // 使线程阻塞
        System.in.read();
    }

    @After
    public void close() {
        client.close();
    }
}
