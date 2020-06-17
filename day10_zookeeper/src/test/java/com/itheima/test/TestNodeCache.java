package com.itheima.test;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author 张鹏
 * @date 2020/6/17 14:24
 */

public class TestNodeCache {

    private CuratorFramework client = null;

    @Before
    public void start() {
        // 创建失败策略对象
        RetryPolicy backoffRetry = new ExponentialBackoffRetry(3000, 3, 1000);
        // 创建客户端
        client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", 3000, 1000, backoffRetry);
        // 开启客户端（会阻塞到会话连接成功为止）
        client.start();
    }

    /**
     * 监听和缓存根节点变化，只监听单一个节点变化（添加，删除，修改）
     */
    @Test
    public void test01() throws Exception {
        // 创建节点监听对象
        NodeCache nodeCache = new NodeCache(client, "/aaa");

        // 开启监听缓存,默认为false，true为开启
        nodeCache.start(true);

        // 添加监听对象，它是异步操作
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            //如果节点数据有变化，会回调该方法
            @Override
            public void nodeChanged() throws Exception {
                // 变化的路径
                String path = nodeCache.getCurrentData().getPath();
                // 变化的数据
                byte[] bytes = nodeCache.getCurrentData().getData();
                String data = new String(bytes);
                // 打印出
                System.out.println("变化路径：" + path + "\n变化数据：" + data);
            }
        });

        // 使线程阻塞
        System.in.read();
    }

    @After
    public void close() {
        // 关闭连接
        client.close();
    }

}
