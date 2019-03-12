package test.rabbitmq.rabbitmq.exchange.fanout;

import com.rabbitmq.client.*;


import java.io.IOException;

public class FanoutExchangeConsumer {

    public static void main(String[] args) throws Exception {
        //1 创建一个ConnectionFactory, 并进行配置
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.1.28");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("toher");
        connectionFactory.setPassword("toher888");
        //2 通过连接工厂创建连接
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        //4 定义
        String exchangeName = "test_fanout_exchange";
        //5 指定类型为fanout
        String exchangeType = "fanout";
        String queueName = "test_fanout_queue";
        String routingKey = "";    //不设置路由键
        channel.exchangeDeclare(exchangeName, exchangeType, true, false, false, null);
        channel.queueDeclare(queueName, false, false, false, null);
        channel.queueBind(queueName, exchangeName, routingKey);
        //5 创建消费者
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String msg = new String(body, "UTF-8");
                System.out.println("消费端:" + msg);
            }
        };
        //参数：队列名称、是否自动ACK、Consumer
        channel.basicConsume(queueName, true, consumer);

    }
}
