package com.example.pocketbank_plus.pojo.dto;

import lombok.Data;

@Data
public class UnmaskDTO {
    private Long userId;
    private Long id;
    private String payWord;
}
