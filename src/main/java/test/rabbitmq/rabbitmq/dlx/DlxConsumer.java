package test.rabbitmq.rabbitmq.dlx;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DlxConsumer {

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

        //4 创建私信队列
        channel.exchangeDeclare("dlx.exchange", "topic", true, false, null);
        channel.queueDeclare("dlx.queue", true, false, false, null);
        channel.queueBind("dlx.queue", "dlx.exchange", "#");

        //5 正常声明我们工作的交换机和队列 与DlxProducer exchangeName相同
        String exchangeName = "test_dlx_exchange";
        String queueName = "test_dlx_queue";
        //因为*号代表匹配一个单词
        String routingKey = "dlx.*";
        //表示声明了一个交换机
        channel.exchangeDeclare(exchangeName, "topic", true, false, false, null);
        //表示声明了一个队列 创建agruments属性，要设置到声明队列上
        Map<String, Object> agruments = new HashMap();
        //dlx.exchange 是我们要创建的死信队列
        agruments.put("x-dead-letter-exchange", "dlx.exchange");
        channel.queueDeclare(queueName, true, false, false, agruments);
        //建立一个绑定关系:
        channel.queueBind(queueName, exchangeName, routingKey);
        //6 创建消费者
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String msg = new String(body, "UTF-8");
                //采用 Nack 拒绝 并重回队列
                System.out.println("未消费:" + msg);
                channel.basicNack(envelope.getDeliveryTag(), false, false);
                //采用Reject拒绝
                //channel.basicReject(envelope.getDeliveryTag(), false);
            }
        };
        //参数：队列名称、是否自动ACK、Consumer
        channel.basicConsume(queueName, false, consumer);
    }

}
