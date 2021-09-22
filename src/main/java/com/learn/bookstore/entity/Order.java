package com.learn.bookstore.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
@Entity
@Getter
@Setter
@Table(name = "`order`")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Float totalprice;
    private Integer userid;
    private String username;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8s")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date time;

    @OneToMany(targetEntity = OrderDetail.class,mappedBy = "order",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<OrderDetail> orderDetails;
}
