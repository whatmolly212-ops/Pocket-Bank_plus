package com.example.pocketbank_plus.pojo.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferRequest {
    private BigDecimal money;
    private String sourceAccountNo;
    private String targetAccountNo;
    private String password;
    private String remark;

    // --- 新增风控增强字段（非高风险交易可为空） ---
    private String riskLevel; // 前端传回的风险等级：NONE / LOW / HIGH
    private String idCard;    // 用户在风控弹窗中输入的身份证号
    private Long userId;      // 当前操作的用户ID，用于定位实名信息
}