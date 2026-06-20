package com.example.pocketbank_plus.pojo.dto;

import lombok.Data;

@Data
public class ResetPasswordDTO {
    private String phone;
    private String idCard;
    private String newPassword;
    private String code;
}
