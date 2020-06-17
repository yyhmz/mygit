package com.itheima.test;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author 张鹏
 * @date 2020/6/17 15:22
 */
public class TestTreeCache {

    private CuratorFramework client = null;

    @Before
    public void open() {
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(1000, 1);
        client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", 1000, 1000, retry);
        client.start();
    }


    /**
     * TreeCache有点像NodeCache和PathChildrenCache的结合
     * 既能够监听自身节点的变化，也能够监听子节点的变化
     */
    @Test
    public void test01() throws Exception {
        // 创建监听对象
        TreeCache treeCache = new TreeCache(client, "/a");
        // 开启监听
        treeCache.start();
        System.out.println(treeCache.getCurrentData("/a"));
        treeCache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework curator, TreeCacheEvent event) throws Exception {
                if (event.getType() == TreeCacheEvent.Type.INITIALIZED) {
                    System.out.println("初始化...");
                } else if (event.getType() == TreeCacheEvent.Type.CONNECTION_SUSPENDED) {
                    System.out.println("连接超时...");
                } else if (event.getType() == TreeCacheEvent.Type.CONNECTION_RECONNECTED) {
                    System.out.println("重新连接...");
                } else if (event.getType() == TreeCacheEvent.Type.CONNECTION_LOST) {
                    System.out.println("连接超时之后过一段时间执行...");
                } else if (event.getType() == TreeCacheEvent.Type.NODE_ADDED) { // 检测是否添加
                    System.out.println("添加的节点：" + event.getData().getPath());
                    System.out.println("添加的数据：" + new String(event.getData().getData()));
                } else if (event.getType() == TreeCacheEvent.Type.NODE_REMOVED) { // 检测是否删除
                    System.out.println("删除的节点：" + event.getData().getPath());
                    System.out.println("删除的数据：" + new String(event.getData().getData()));
                } else if (event.getType() == TreeCacheEvent.Type.NODE_UPDATED) { // 检测是否修改
                    System.out.println("修改的节点：" + event.getData().getPath());
                    System.out.println("修改的数据：" + new String(event.getData().getData()));
                }
            }
        });

        // 线程阻塞
        System.in.read();
    }

    @After
    public void close() {
        client.close();
    }
}
