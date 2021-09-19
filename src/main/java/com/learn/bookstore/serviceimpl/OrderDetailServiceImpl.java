package com.learn.bookstore.serviceimpl;

import com.learn.bookstore.dao.OrderDetailDao;
import com.learn.bookstore.entity.OrderDetail;
import com.learn.bookstore.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {
    @Autowired
    OrderDetailDao orderDetailDao;
    @Override
    public OrderDetail saveOrderDetail(OrderDetail orderDetail) {
        return orderDetailDao.saveOrderDetail(orderDetail);
    }
}
