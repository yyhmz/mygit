package com.itheima.test;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author 张鹏
 * @date 2020/6/17 13:46
 */
public class TestGetZookeeper {

    private CuratorFramework newClient = null;

    /**
     * 开启连接
     */
    @Before
    public void beforeClient() {
        /*
            RetryPolicy：失败的重试策略的公共接口
            ExponentialBackoffRetry：公共接口中的一个实现类
            参数1：初始化sleep时间，用于计算后的每次重试的sleep时间
            参数2：最大重试次数
            参数3：最大sleep时间，如果上述的当前sleep计算出来比这个大，那么sleep用这个时间（可以省略）
         */
        // 创建失败策略对象
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3, 10);


        /*
            参数1：连接的ip地址和端口号
            参数2：会话超时时间，单位毫秒
            参数3：连接超时时间，单位毫秒
            参数4：失败重试策略
         */
        // 创建客户端
        newClient = CuratorFrameworkFactory.newClient("127.0.0.1:2181", 3000, 1000, retryPolicy);

        // 开启客户端（会阻塞到会话连接成功为止）
        newClient.start();
    }

    /**
     * 查询zookeeper中的节点数据
     */
    @Test
    public void setNode() throws Exception{
        byte[] bytes = newClient.getData().forPath("/c/f");
        System.out.println(new String(bytes));
    }


    /**
     * 关闭连接
     */
    @After
    public void closeClient() {
        newClient.close();
    }

}
