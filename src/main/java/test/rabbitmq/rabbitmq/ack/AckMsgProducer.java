package test.rabbitmq.rabbitmq.ack;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 同恒科技-李怀明
 * @Date: 2019/3/2 19:25
 */
public class AckMsgProducer {

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
        String exchangeName = "test_ack_exchange";
        //这里故意用一个错误的routingKey 以便测试交换机路由不到队列
        String routingKey = "ack.send";
        //6 发送一条消息
        String msg = "Test ACK Message";
        for (int i = 1; i < 5; i++) {
            Map<String, Object> headers = new HashMap<String, Object>();
            headers.put("num", i);
            //为了测试消息拒绝 我们传递一个自定义参数 消费端进行测试
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    .deliveryMode(2)
                    .contentEncoding("UTF-8")
                    .headers(headers)
                    .build();
            msg = msg + " 第" + i + "条";
            channel.basicPublish(exchangeName, routingKey, true, properties, msg.getBytes());
        }
    }

}
