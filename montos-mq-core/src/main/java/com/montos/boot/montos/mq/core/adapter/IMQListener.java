package com.montos.boot.montos.mq.core.adapter;

/**
 * 定义监听器接口
 */
public interface IMQListener {

    /**
     * 监听接收的信息
     *
     * @param mQReceiver
     */
    public void listener(IMQReceiver mQReceiver);

    /**
     * 停止服务，关闭监听
     */
    public default void shutdown() {
    }

    ;

}
