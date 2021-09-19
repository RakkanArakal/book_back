package com.learn.bookstore.serviceimpl;

import com.learn.bookstore.dao.OrderDao;
import com.learn.bookstore.entity.Book;
import com.learn.bookstore.entity.Order;
import com.learn.bookstore.entity.OrderDetail;
import com.learn.bookstore.service.BookService;
import com.learn.bookstore.service.OrderDetailService;
import com.learn.bookstore.service.OrderService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    OrderDetailService orderDetailService;

    @Autowired
    BookService bookService;

    @Autowired
    EntityManager entityManager;

    @Override
    public List<Map<String,Object>> getOrdersByUid(int uid) {
        return orderDao.getOrdersByUserId(uid);
    }

    @Override
    public Order saveOrder(Order order) {
        return orderDao.saveOrder(order);
    }

    @Override
    public String addOrder(JSONObject ordersJsonbject) {
        JSONObject totalOrderJsonobject = ordersJsonbject.getJSONObject("Order");
        Order order = (Order) JSONObject.toBean(totalOrderJsonobject, Order.class);
        try {
            Order thisorder = saveOrder(order);
            int orderid = thisorder.getId();

            if (orderid > 0) {
                JSONArray jsonArray = ordersJsonbject.getJSONArray("orderDetail");
                List<OrderDetail> OrderDetaillist = (List<OrderDetail>) JSONArray.toCollection(jsonArray, OrderDetail.class);
                for (OrderDetail orderDetail : OrderDetaillist) {
                    orderDetail.setOrderid(orderid);
                    Book book = bookService.getBookById(orderDetail.getBookid());
                    int stock = book.getStock() - 1;
//                    System.out.print(stock);
                    if (stock >= 0) {
                        orderDetailService.saveOrderDetail(orderDetail);
                        book.setStock(stock);
                        bookService.saveBook(book);
                    } else {
                        return "库存量不足";
                    }
                }
            } else {
                return "下单失败";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "下单失败";
        }
        return "success";
    }

    @Override
    public List<Map<String, Object>> searchOrders(int id, String t1, String t2) {
        String sql = "SELECT `order`.time,`order`.id,`order`.username,`order`.totalprice,order_detail.orderid,order_detail.bookid,order_detail.bookname,order_detail.price " +
                "FROM `order` INNER JOIN order_detail ON `order`.id = order_detail.orderid WHERE";
        if (id > 1)
            sql += "`order`.userid=" + id+" and ";

        sql += " unix_timestamp(" + "`order`.time) between unix_timestamp('" + t1 + "')";
        if(!t2.equals("")){
            t2 = "'"+t2+"'";
        }else {
            t2 = "now()";
        }
        sql += " and unix_timestamp(" + t2 + ")";
//        System.out.print(sql);
        List<Map<String, Object>> resultList = entityManager.createNativeQuery(sql)
                .unwrap(org.hibernate.Query.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .getResultList();
//        System.out.print(resultList);
        return resultList;
    }
}
