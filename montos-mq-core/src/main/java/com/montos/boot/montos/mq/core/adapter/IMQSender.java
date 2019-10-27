package com.montos.boot.montos.mq.core.adapter;

/**
 * 定义发送端接口
 */
public interface IMQSender {

    /**
     * 向队列发送消息
     *
     * @param msg 消息
     */
    public void send(MQMessage msg);

}
