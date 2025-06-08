package com.shopClone.dto;

import com.shopClone.entity.EmployeeLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeRegisterForm {

    private String name;
    private String email;
    private Long teamId;
    private EmployeeLevel level;
    private Long memberId; // 연결할 Member
}
