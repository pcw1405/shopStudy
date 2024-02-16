package com.shopClone.controller;

import com.shopClone.dto.OrderDto;
import com.shopClone.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

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
}
