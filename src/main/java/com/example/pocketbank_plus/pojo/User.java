package com.example.pocketbank_plus.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String name;
    private String phone;
    private String idCard;// 下划线转驼峰：id_card → idCard
    private String password;
    private LocalDateTime createTime;

}
