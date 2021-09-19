package com.learn.bookstore.dao;

import com.learn.bookstore.entity.Order;

import java.util.List;
import java.util.Map;

public interface OrderDao {
     List<Map<String,Object>> getOrdersByUserId(int id);
     Order saveOrder(Order order);
}
