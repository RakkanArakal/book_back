package com.learn.bookstore.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Getter
@Setter
@Table(name = "shopping_cart")
public class ShoppingCart implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private User user;

    @OneToOne(mappedBy = "ShoppingCart", cascade = CascadeType.ALL)
    @JoinColumn(name = "user", referencedColumnName = "id")
    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }
}
