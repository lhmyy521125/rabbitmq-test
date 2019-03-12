package test.rabbitmq.rabbitmq.ack;

import com.rabbitmq.client.*;

import java.io.IOException;

public class AckMsgConsumer {

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
        String exchangeName = "test_ack_exchange";
        //指定类型为topic
        String exchangeType = "topic";
        String queueName = "test_ack_queue";
        //因为*号代表匹配一个单词
        String routingKey = "ack.*";
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

                //让第一条数据 采用 Nack 拒绝 并重回队列
                if ((Integer) properties.getHeaders().get("num") == 1) {
                    System.out.println("未消费:" + msg);
                    channel.basicNack(envelope.getDeliveryTag(), false, true);
                    //channel.basicReject(envelope.getDeliveryTag(), false);
                } else {
                    System.out.println("已消费:" + msg);
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };
        //参数：队列名称、是否自动ACK、Consumer
        channel.basicConsume(queueName, false, consumer);
    }

}
