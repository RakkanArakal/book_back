package com.learn.bookstore.responsitory;

import com.learn.bookstore.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface OrderRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {
    @Query(value = "SELECT `order`.createtime,`order`.id,`order`.totalprice,order_item.oid,order_item.bookid,order_item.bookname,order_item.price,order_item.quantity FROM `order` INNER JOIN order_item ON `order`.id = order_item.orderid WHERE `order`.userid =?", nativeQuery = true)
    List<Map<String, Object>> getOrdersByUid(int id);


}
