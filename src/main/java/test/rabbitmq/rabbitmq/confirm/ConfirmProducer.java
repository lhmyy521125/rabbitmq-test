package test.rabbitmq.rabbitmq.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;

/**
 * @Author: 同恒科技-李怀明
 * @Date: 2019/3/2 19:25
 */
public class ConfirmProducer {

    public static void main(String[] args) throws Exception {
        //1 创建ConnectionFactory
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.1.28");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("toher");
        connectionFactory.setPassword("toher888");
        //2 创建Connection
        Connection connection = connectionFactory.newConnection();
        //3 创建Channel
        Channel channel = connection.createChannel();
        //4 指定我们的消息投递模式: 消息的确认模式
        channel.confirmSelect();
        //5 声明交换机 以及 路由KEY
        String exchangeName = "test_confirm_exchange";
        String routingKey = "confirm.send";
        //6 发送一条消息
        String msg = "Test Confirm Message";
        channel.basicPublish(exchangeName, routingKey, null, msg.getBytes());
        //7 添加确认监听
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.err.println("收到ACK应答");
            }

            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.err.println("收到NACK应答");
            }
        });
    }

}
