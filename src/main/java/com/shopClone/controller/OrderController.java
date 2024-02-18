package com.shopClone.controller;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.shopClone.dto.OrderDto;
import com.shopClone.dto.OrderHistDto;
import com.shopClone.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping(value = "/order")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid OrderDto orderDto,
                                          BindingResult bindingResult, Principal principal){
        if(bindingResult.hasErrors()){
            StringBuilder sb =new StringBuilder();
            List<FieldError> fieldErrors =bindingResult.getFieldErrors();

            for(FieldError fieldError : fieldErrors){
                sb.append(fieldError.getDefaultMessage());
            }

            return new ResponseEntity<String>(sb.toString(),HttpStatus.BAD_REQUEST);
        }
        String email=principal.getName();
        // principal 객체로 넘겨줄 경우 해당 객체에 직접 접근할 수 있습니다.
        // principal 객체에서 현재 로그인한 회원의 이메일 정보를 조회합니다.
        Long orderId;

        try{
            orderId= orderService.order(orderDto,email);
        } catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(orderId,HttpStatus.OK);
    }

    @GetMapping(value = {"/orders","/orders/{page}"})
    public String orderHist(@PathVariable("page")Optional<Integer> page, Principal principal, Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0,4);

        Page<OrderHistDto> orderHistDtoList=orderService.getOrderList(principal.getName(),pageable);

        model.addAttribute("orders",orderHistDtoList);
        model.addAttribute("page",pageable.getPageNumber());
        model.addAttribute("maxPage",5);

        return "order/orderHist";
    }

    @PostMapping("/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId , Principal principal){
        if(!orderService.validateOrder(orderId,principal.getName())){
            return new ResponseEntity<String>("추문 취소 권한이 없습니다.",HttpStatus.FORBIDDEN);
        }
        orderService.cancelOrder(orderId);
        return new ResponseEntity<Long>(orderId,HttpStatus.OK);
    }

}
