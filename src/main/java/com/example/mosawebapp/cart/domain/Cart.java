package com.example.mosawebapp.cart.domain;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.product.domain.Product;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Cart {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    @CreationTimestamp
    private Date dateCreated;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="product_id")
    private Product product;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    private Account account;
    @Column
    private long quantity;

    public Cart(){}
    public Cart(Date dateCreated, Product product, Account account, long quantity) {
        this.dateCreated = dateCreated;
        this.product = product;
        this.account = account;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public long isQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getTotalPrice(){
        return product.getGrossPrice() * quantity;
    }
}
