package com.shopClone.entity;

import com.shopClone.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")
@Getter
@Setter
public class Order extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name="order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    private LocalDateTime orderDate; // 주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문상태

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
    orphanRemoval = true)
    private List<OrderItem> orderItems =new ArrayList<>();
    //영속성 전이 order를 저장하면 orderItem 도 함께 저장됨

    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public static Order createOrder(Member member,List<OrderItem> orderItemList){
        Order order =new Order();
        order.setMember(member); // 상품을 주문 회원의 정보

        for(OrderItem orderItem : orderItemList){
            order.addOrderItem(orderItem);
        }
        // 리스트 형태

        order.setOrderStatus(OrderStatus.ORDER); // 주문상태를 ORDER로 셋팅
        order.setOrderDate(LocalDateTime.now());
        return order;
    }



}
