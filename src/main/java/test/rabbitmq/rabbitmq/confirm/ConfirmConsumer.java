package test.rabbitmq.rabbitmq.confirm;

import com.rabbitmq.client.*;

import java.io.IOException;

public class ConfirmConsumer {

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
        String exchangeName = "test_confirm_exchange";
        //指定类型为topic
        String exchangeType = "topic";
        String queueName = "test_confirm_queue";
        //因为*号代表匹配一个单词，生产者中routingKey3将匹配不到
        String routingKey = "confirm.*";
        //表示声明了一个交换机
        channel.exchangeDeclare(exchangeName, exchangeType, true, false, false, null);
        //表示声明了一个队列
        channel.queueDeclare(queueName, true, false, false, null);
        //建立一个绑定关系:
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
