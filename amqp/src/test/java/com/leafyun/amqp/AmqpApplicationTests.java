package com.leafyun.amqp;

import com.leafyun.amqp.bean.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AmqpApplicationTests {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    AmqpAdmin amqpAdmin;// 用于创建 exchange 以及 queue 及绑定规则

    @Test
    public void contextLoads() {
    }

    /**
     * 单播点对点
     */
    @Test
    public void test1() {
        // Message 需要自己构造一个；定义消息体内容和消息头,，因此我们一般使用 convertAndSend 方法自动序列化
        // rabbitTemplate.send(exchange,routing,msg);

        // object 默认当成消息体 只需要传如发送的对象，自动序列化发送给 mq
        // rabbitTemplate.convertAndSend(exchange,routing,object);
        HashMap<String, Object> map = new HashMap<>();
        map.put("msg", "z这是一条消息");
        map.put("data", Arrays.asList("123", true, 456));
        // 对象默认被使用 java 的序列化方式发送出去
        rabbitTemplate.convertAndSend("exchange.direct", "atguigu.news"
                , new Book("西游记", "吴承恩"));

//        rabbitTemplate.convertAndSend("exchange.direct", "atguigu.news"
//                , "hello word 123");
    }

    /**
     * 接受数据，如何将数据自动的转为json发出去
     */
    @Test
    public void receiveTest() {
        Object o = rabbitTemplate.receiveAndConvert("atguigu.news");
        System.out.println(o.getClass());
        System.out.println(o);
    }

    /**
     * 采用广播的方式发送 fanout
     */
    @Test
    public void sendTest() {
        rabbitTemplate.convertAndSend("exchange.fanout", "atguigu.#"
                , new Book("红楼梦", "曹雪芹"));
    }

    /**
     * 多个接受者接受消息
     */
    @Test
    public void receiveByAnyoneTest() {
        Object o = rabbitTemplate.receiveAndConvert("atguigu");
        Object o1 = rabbitTemplate.receiveAndConvert("atguigu.news");
        Object o2 = rabbitTemplate.receiveAndConvert("atguigu.emps");
        Object o3 = rabbitTemplate.receiveAndConvert("gulixueyuan.news");
        System.out.println(o);
        System.out.println(o1);
        System.out.println(o2);
        System.out.println(o3);
    }


    /**
     * 创建 exchange 以及 queue 及绑定规则
     */
    @Test
    public void test2() {
//        // 创建 fanout 交换机
//        amqpAdmin.declareExchange(new FanoutExchange("amqpAdmin.exchange"));
//        // 创建消息队列
//        amqpAdmin.declareQueue(new Queue("amqpAdmin.queue",true));
        // 创建绑定规则
        Binding binding = new Binding("amqpAdmin.queue", Binding.DestinationType.QUEUE,
                "amqpAdmin.exchange", "amqp.haha", null);
        // 进行绑定
        amqpAdmin.declareBinding(binding);
        // 删除 exchange 以及 queue 及绑定规则
//        amqpAdmin.deleteExchange("amqpAdmin.exchange");
//        amqpAdmin.deleteQueue("amqpAdmin.queue");
//        amqpAdmin.removeBinding(binding);
    }

}
