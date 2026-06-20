package com.example.pocketbank_plus.pojo;

import lombok.Data;

@Data
public class Result{
    private int code;
    private String msg;
    private Object data;

    // ====================== 成功返回 ======================
    public static Result success(String msg) {
        Result result = new Result();
        result.setCode(200);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }

    // 成功 + 带数据
    public static Result success(String msg, Object data) {
        Result result = new Result();
        result.setCode(200);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    // ====================== 失败返回 ======================
    public static Result fail(String msg) {
        Result result = new Result();
        result.setCode(500);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }
}
