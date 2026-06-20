package com.example.pocketbank_plus.pojo;

public enum BillType {
    FOOD("餐饮美食🍔"),
    SHOPPING("日用百货"),
    TRANSPORT("交通出行🚗"),
    ENTERTAINMENT("休闲娱乐🎉"),
    HOUSING("房租水电"),
    MEDICAL("医疗保健👩‍⚕️"),
    INVEST("理财投资"),
    OTHER("其他支出");

    private final String description;

    BillType(String description) {
        this.description = description;
    }
}
