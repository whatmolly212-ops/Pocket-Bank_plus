package com.example.pocketbank_plus.controller;

import com.example.pocketbank_plus.pojo.Bill;
import com.example.pocketbank_plus.pojo.Result;
import com.example.pocketbank_plus.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bill")
public class BillController {
    @Autowired
    BillService billService;

    @PostMapping("/add")
    public Result addRecord(@RequestBody Bill bill){
        System.out.println(">>> [控制层] 收到新增账单请求: " + bill.toString());

        boolean result=billService.addAccount(bill);
        if(result)
            return Result.success("添加账单成功！");
        else
            return Result.success("添加账单失败！");
    }
    @GetMapping("/getData")
    public Result getData(String yearMonth){
        List<Bill> bill=billService.getRecord(yearMonth);
        return Result.success("查询成功",bill);

    }
    @GetMapping("/statistics")
    public Result getStatistics(@RequestParam Long userId, @RequestParam String yearMonth) {
        System.out.println(">>> [控制层] 收到统计请求: 用户ID=" + userId + ", 月份=" + yearMonth);
        List<Map<String, Object>> stats = billService.visualization(userId, yearMonth);
        return Result.success("可视化成功！",stats);
    }
}
