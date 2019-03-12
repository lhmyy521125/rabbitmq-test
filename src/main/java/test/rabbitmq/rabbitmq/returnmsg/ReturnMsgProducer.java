package test.rabbitmq.rabbitmq.returnmsg;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @Author: 同恒科技-李怀明
 * @Date: 2019/3/2 19:25
 */
public class ReturnMsgProducer {

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
        String exchangeName = "test_return_exchange";
        //这里故意用一个错误的routingKey 以便测试交换机路由不到队列
        String routingKey = "test.send";
        //6 发送一条消息
        String msg = "Test Return Message";
        //设置mandatory true
        channel.basicPublish(exchangeName, routingKey, true, null, msg.getBytes());
        //7 添加确认监听 我们将返回的参数都打印出来
        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange,
                                     String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.err.println("replyCode: " + replyCode);
                System.err.println("replyText: " + replyText);
                System.err.println("exchange: " + exchange);
                System.err.println("routingKey: " + routingKey);
                System.err.println("properties: " + properties);
                System.err.println("body: " + new String(body));
            }
        });
    }

}
