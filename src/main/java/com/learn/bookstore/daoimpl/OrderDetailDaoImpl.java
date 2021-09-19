package com.learn.bookstore.daoimpl;

import com.learn.bookstore.dao.OrderDetailDao;
import com.learn.bookstore.entity.OrderDetail;
import com.learn.bookstore.responsitory.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OrderDetailDaoImpl implements OrderDetailDao {

    @Autowired
    OrderDetailRepository orderDetailRepository;
    @Override
    public OrderDetail saveOrderDetail(OrderDetail orderDetail) {
        return orderDetailRepository.save(orderDetail);
    }
}
