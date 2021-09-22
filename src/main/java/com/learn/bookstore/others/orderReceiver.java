package com.learn.bookstore.others;

import com.learn.bookstore.service.OrderService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class orderReceiver {
    @Autowired
    OrderService orderService;

    @JmsListener(destination = "order")//, containerFactory = "myFactory")
    public void receiveMessage(JSONObject ordersJsonbject) {
//        System.out.println(ordersJsonbject);
        orderService.addOrder(ordersJsonbject);
    }
}
