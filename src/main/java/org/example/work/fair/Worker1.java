package org.example.work.fair;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * 公平分发
 */
public class Worker1 {
    public static void main(String[] args) {

        // 1、创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("159.75.86.194");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
        connectionFactory.setVirtualHost("/");
        Connection connection = null;
        Channel channel = null;
        // 2、创建连接
        try {
            connection = connectionFactory.newConnection("消费者");
            // 3、创建通道
            channel = connection.createChannel();
            // 4、通过通道创建交换机、声明队列、绑定关系、路由key、发送消息、接收消息
            final String queueName = "queue1";
            final Channel finalChannel = channel;
            // 每次订阅的客户端从队列获取的消息数
            finalChannel.basicQos(1);
            /**
             * @param1 队列名称
             * @param2 自动应答
             * @param3 接收成功事件
             * @param3 接收失败事件
             */
            // 公平分发必须将自动应答关闭
            finalChannel.basicConsume(queueName, false, new DeliverCallback() {
                public void handle(String s, Delivery delivery) throws IOException {
                    try {
                        System.out.println("worker1接收成功" + " " + new String(delivery.getBody(), "UTF-8"));
                        Thread.sleep(100);
                        finalChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new CancelCallback() {
                    public void handle(String s) throws IOException {
                        System.out.println("接收失败");
                    }
                });
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 7、关闭通道
            try {
                if (channel != null && channel.isOpen()) {
                    channel.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 8、关闭连接
            try {
                if (connection != null && connection.isOpen()) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
