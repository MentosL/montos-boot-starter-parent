package com.montos.boot.montos.mq.core.adapter;


import com.montos.boot.montos.mq.core.api.enums.QueueType;

/**
 * 定义接收端接口
 */
public interface IMQReceiver {

    /**
     * 获取队列类型
     *
     * @return
     */
    public QueueType getQueueType();

    /**
     * 获取队列名称
     *
     * @return
     */
    public String getmQName();

    /**
     * 获取队列标签
     *
     * @return
     */
    public String getTag();

    /**
     * 获取队列名称
     *
     * @return
     */
    public String getmQDataSource();

    /**
     * 获取消息类型
     *
     * @return
     */
    public Class<?>[] getMsgClass();

    /**
     * 收到一个队列的消息
     *
     * @param msg
     * @throws Throwable
     */
    public void receive(Object msg) throws Throwable;

}
