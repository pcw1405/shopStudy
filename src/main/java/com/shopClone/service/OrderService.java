package com.shopClone.service;

import com.shopClone.dto.OrderDto;
import com.shopClone.entity.Item;
import com.shopClone.entity.Member;
import com.shopClone.entity.Order;
import com.shopClone.entity.OrderItem;
import com.shopClone.repository.ItemImgRepository;
import com.shopClone.repository.ItemRepository;
import com.shopClone.repository.MemberRepository;
import com.shopClone.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;

    public Long order(OrderDto orderDto,String email){

        Item item =itemRepository.findById(orderDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);
        Member member =memberRepository.findByEmail(email);
        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        // 주문할 상품 엔티티와 주문 수량을 이용하여 주문 상품 엔티티를 생성합니다.
        orderItemList.add(orderItem);
        Order order= Order.createOrder(member, orderItemList);
        // 회원정보와 주문할 상품 리스트 정보를 이용하여 주문 엔티티를 생성합니다.
        orderRepository.save(order);
        // 생성한 주문 엔티티를 저장합니다.
        return order.getId();
    }

}
