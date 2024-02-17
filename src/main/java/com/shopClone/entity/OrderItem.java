package com.shopClone.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class OrderItem extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name="order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    private Order order;

    private int orderPrice; //주문가격
    private int count; // 수량

    public static OrderItem createOrderItem(Item item,int count){
        OrderItem orderItem =new OrderItem();
        orderItem.setItem(item); // 주문할 상품 셋팅
        orderItem.setCount(count); // 주문수량 셋팅
        orderItem.setCount(count); // 주문수량셋팅
        orderItem.setOrderPrice(item.getPrice()); // 상품가격을 주문 가격으로 세팅
        item.removeStock(count); // item의 메서드인 removeStock을 사용하여 재고를 제거

        return orderItem;
    }

    public void cancel(){ this.getItem().addStock(count); }

}
