package com.example.pocketbank_plus.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Bill {
    private Long id;
    private Long userId;
    private BigDecimal expense;
    private BillType type;
    private String remark;
    private LocalDateTime createTime;
}
