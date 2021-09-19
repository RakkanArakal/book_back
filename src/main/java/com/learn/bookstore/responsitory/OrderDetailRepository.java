package com.learn.bookstore.responsitory;

import com.learn.bookstore.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderDetailRepository extends JpaRepository<OrderDetail,Integer>, JpaSpecificationExecutor<OrderDetail> {
}
