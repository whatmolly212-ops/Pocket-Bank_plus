package com.example.pocketbank_plus.pojo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountCreateDTO {
    private Long userId;       // 哪个用户开户
    private String payPassword; // 设定的交易密码
    private BigDecimal initialBalance; // 初始存入金额（可选，默认为0）
}
