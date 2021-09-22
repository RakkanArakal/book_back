package com.learn.bookstore.controller;

import com.learn.bookstore.service.OrderService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/home")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    WebApplicationContext applicationContext;

//    @PostMapping("addOrder")
//    public String addTotalOrder(@RequestBody JSONObject ordersJsonbject) {
//        return orderService.addOrder(ordersJsonbject);
//    }

    @PostMapping("addOrder")
    public String addTotalOrder(@RequestBody JSONObject ordersJsonbject) {
        System.out.print(ordersJsonbject);

        JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class);
        jmsTemplate.convertAndSend("order", ordersJsonbject);

        return "success";
    }

    @GetMapping("/getOrdersByUid")
    public List<Map<String,Object>> getOrdersByUid(int id){
        return orderService.getOrdersByUid(id);
    }
    @PostMapping("/getOrders")
    public List<Map<String, Object>> searchOrders(
            @RequestParam int id,
            @RequestParam(required = true, defaultValue = "2020-01-01 00:00:00") String t1,
            @RequestParam(required = true, defaultValue = "") String t2
    ) {
        return orderService.searchOrders(id, t1, t2);
    }
}
