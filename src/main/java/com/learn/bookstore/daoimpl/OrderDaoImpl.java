package com.learn.bookstore.daoimpl;

import com.learn.bookstore.dao.OrderDao;
import com.learn.bookstore.entity.Order;
import com.learn.bookstore.responsitory.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public class OrderDaoImpl implements OrderDao {
    @Autowired
    OrderRepository orderRepository;

    @Override
    public List<Map<String,Object>> getOrdersByUserId(int id) {
        return orderRepository.getOrdersByUid(id);
    }

    @Override
    @Transactional
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }


}
