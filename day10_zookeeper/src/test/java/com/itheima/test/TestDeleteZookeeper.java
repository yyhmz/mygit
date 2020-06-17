package com.itheima.test;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryForever;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author 张鹏
 * @date 2020/6/17 13:56
 */
public class TestDeleteZookeeper {

    private CuratorFramework newClient = null;

    @Before
    public void before() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000, 3);
        newClient = CuratorFrameworkFactory.newClient("127.0.0.1:2181", 3000, 1000, retryPolicy);
        newClient.start();
    }

    @Test
    public void test01() throws Exception {
        // 删除一个子节点
        //newClient.delete().forPath("/a");
        // 删除节点并递归删除其子节点
        //newClient.delete().deletingChildrenIfNeeded().forPath("/c");
        // 强制保证删除一个节点（就算出现宕机或者异常都会进行删除）
        newClient.delete().guaranteed().deletingChildrenIfNeeded().forPath("/b");
    }

    @After
    public void after() {
        newClient.close();
    }

}
