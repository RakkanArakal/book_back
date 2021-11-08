package com.learn.bookstore.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Getter
@Setter
@Table(name = "user")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String account;
    private String password;
    private String email;
    private Integer ban;

    @Transient
    private Integer count;

    private ShoppingCart shoppingCart;

    @OneToOne(cascade = CascadeType.ALL) // 相互关联，共同增删改查
    @JoinColumn(name = "user", referencedColumnName = "id")
    public ShoppingCart getShoppingCart() { return shoppingCart; }

    public void setShoppingCart(ShoppingCart shoppingCart) { this.shoppingCart = shoppingCart; }

}
