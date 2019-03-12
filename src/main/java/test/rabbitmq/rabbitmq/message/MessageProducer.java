package test.rabbitmq.rabbitmq.message;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

public class MessageProducer {

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
        //4 声明
        String routingKey = "test.direct";
        //5 发送
        Map<String, Object> headers = new HashMap<>();
        headers.put("param1", "111");
        headers.put("param1", "222");

        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                .deliveryMode(2) //设置持久化
                .contentEncoding("UTF-8")
                .expiration("10000") //设置10秒过期
                .headers(headers) //传递自定义headers
                .build();
        String msg = "Test Message";
        //exchange 为空 默认走RabbitMQ默认交换机
        channel.basicPublish("", routingKey, properties, msg.getBytes());
        channel.close();
        connection.close();
    }

}
