package com.learn.bookstore.service;

import com.learn.bookstore.entity.Order;
import net.sf.json.JSONObject;

import java.util.List;
import java.util.Map;

public interface OrderService {

    List<Map<String,Object>> getOrdersByUid(int uid);

    Order saveOrder(Order order);

    String addOrder(JSONObject ordersJsonbject);

    List<Map<String, Object>> searchOrders(int id, String t1, String t2);
}
