package org.example.all;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 通用（代码创建交换机与队列且绑定关系）
 */
public class Producer {

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
            connection = connectionFactory.newConnection("生产者");
            // 3、创建通道
            channel = connection.createChannel();
            // 4、通过通道创建交换机、声明队列、绑定关系、路由key、发送消息、接收消息
            String exchangeName = "direct_message_exchange";
            String exchangeType = "direct";
            /**
             * @param1 交换机名称
             * @param1 交换机类型
             * @param1 是否持久化
             */
            channel.exchangeDeclare(exchangeName, exchangeType, true);
            // 声明队列
            /**
             * @param1 队列名称
             * @param2 是否要持久化，非持久化也会将数据存盘，但是非持久化的存盘会随着服务的重启而丢失
             * @param3 排他性 是否独占队列
             * @param4 随着最后一个消费者消费完毕后是否自动删除队列
             * @param5 携带附属参数
             */
            channel.queueDeclare("queue4", true, false, false, null);
            channel.queueDeclare("queue5", true, false, false, null);
            channel.queueDeclare("queue6", true, false, false, null);
            channel.queueBind("queue4", exchangeName, "order");
            channel.queueBind("queue5", exchangeName, "order");
            channel.queueBind("queue6", exchangeName, "course");
            // 5、准备消息内容
            String message = "direct mode";
            // 路由key
            String routingKey = "order";
            // 6、发送消息给队列
            /**
             * @param1 交换机
             * @param2 队列名称、路由key
             * @param3 消息状态控制
             * @param4 消息内容
             */
            channel.basicPublish(exchangeName, routingKey, null, message.getBytes());
            System.out.println("发送成功");
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
