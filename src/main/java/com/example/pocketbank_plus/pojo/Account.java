package com.example.pocketbank_plus.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Account {

    private Long id;
    private Long userId;
    private String accountNo;
    private BigDecimal balance;
    private String payPassword;
    private LocalDateTime createTime;
}
