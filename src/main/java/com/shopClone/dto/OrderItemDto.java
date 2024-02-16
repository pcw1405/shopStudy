package com.shopClone.dto;

import com.shopClone.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDto {

    private String itemNm; // 상품명
    private int count; // 주문 수량
    private int orderPrice; // 주문 금액
    private String imgUrl; // 상품 이미지 경로
    // OrderItemDto 클래스의 생성자로 orderItem 객체와

    public OrderItemDto(OrderItem orderItem, String imgUrl){
        this.itemNm=orderItem.getItem().getItemNm();
        // 상품의 이름
        this.count=orderItem.getOrderPrice();
        this.orderPrice=orderItem.getOrderPrice();
        this.imgUrl=imgUrl;

    }



}
