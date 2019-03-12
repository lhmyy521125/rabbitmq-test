package test.rabbitmq.rabbitmq.dlx;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 同恒科技-李怀明
 * @Date: 2019/3/2 19:25
 */
public class DlxProducer {

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
        String exchangeName = "test_dlx_exchange";
        //设置routingKey
        String routingKey = "dlx.send";
        //6 发送一条消息
        String msg = "Test DLX Message";
        for (int i = 1; i < 5; i++) {
            Map<String, Object> headers = new HashMap<String, Object>();
            //设置TTL过期时间 10秒
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    .deliveryMode(2)
                    .contentEncoding("UTF-8")
                    .expiration("10000")
                    .headers(headers)
                    .build();
            channel.basicPublish(exchangeName, routingKey, true, properties, msg.getBytes());
        }
    }

}
