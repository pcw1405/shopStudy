package com.shopClone.service;

import com.shopClone.dto.CartDetailDto;
import com.shopClone.dto.CartItemDto;
import com.shopClone.dto.CartOrderDto;
import com.shopClone.dto.OrderDto;
import com.shopClone.entity.Cart;
import com.shopClone.entity.CartItem;
import com.shopClone.entity.Item;
import com.shopClone.entity.Member;
import com.shopClone.repository.CartItemRepository;
import com.shopClone.repository.CartRepository;
import com.shopClone.repository.ItemRepository;
import com.shopClone.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderService orderService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public Long addCart(CartItemDto cartItemDto, String email){
        Item item = itemRepository.findById(cartItemDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);

        Member member =memberRepository.findByEmail(email);

        Cart cart= cartRepository.findByMemberId(member.getId());


        if(cart==null){
            cart=Cart.createCart(member);
            cartRepository.save(cart);
        }

        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(),item.getId());
        //현재 상품이 장바구니에 이미들어있는지 조회합니다.
        if(savedCartItem != null){
            savedCartItem.addCount(cartItemDto.getCount());
            return savedCartItem.getId();
        }else{
            CartItem cartItem = CartItem.createCartItem(cart,item,cartItemDto.getCount());
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }


    }

    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email){

        List<CartDetailDto> cartDetailDtoList =new ArrayList<>();
        Member member =memberRepository.findByEmail(email);
        Cart cart =cartRepository.findByMemberId(member.getId());
        if(cart==null){
            return cartDetailDtoList;
        }
        cartDetailDtoList =cartItemRepository.findCartDetailDtoList(cart.getId());
        return cartDetailDtoList;
    }

    public boolean validateCartItem(Long cartItemId,String email){
        Member curMember = memberRepository.findByEmail(email);
        CartItem cartItem =cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember =cartItem.getCart().getMember();

        if(!StringUtils.equals(curMember.getEmail(),savedMember.getEmail())){
            return false;
        }
        return true;
    }
    public void updateCartItemCount(Long cartItemId,int count){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        cartItem.updateCount(count);
    }

    public void deleteCartItem(Long cartItemId){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);
    }

    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email){
        List<OrderDto> orderDtoList=new ArrayList<>();

        for(CartOrderDto cartOrderDto : cartOrderDtoList){
            CartItem cartItem = cartItemRepository
                    .findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderDto orderDto=new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }

        Long orderId = orderService.orders(orderDtoList,email);

        for(CartOrderDto cartOrderDto : cartOrderDtoList){
            CartItem cartItem =cartItemRepository
                    .findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);
            cartItemRepository.delete(cartItem);
            // 장바구니를 비운다
        }
        return orderId;

    }


}
