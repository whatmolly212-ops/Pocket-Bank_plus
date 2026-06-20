package com.example.pocketbank_plus.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionRecord {
    private Long id;
    private String AccountNo;
    private String targetAccountNo;
    private String type; // RECHARGE/ WITHDRAW/ TRANSFER
    private BigDecimal amount;
    private LocalDateTime createTime;
    private String remark;//这是干啥的
}
