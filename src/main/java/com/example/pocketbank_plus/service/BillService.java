/*
具体功能实现：
记账功能
根据年份月份查询账单记录
生成饼状图，或许AI 给出适当建议？
*/
package com.example.pocketbank_plus.service;

import com.example.pocketbank_plus.mapper.BillMapper;
import com.example.pocketbank_plus.pojo.Bill;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public interface BillService {
    public boolean addAccount(Bill bill);
    public List<Bill> getRecord(String yearMonth);
    public List<Map<String, Object>> visualization(Long userId, String yearMonth);
}
