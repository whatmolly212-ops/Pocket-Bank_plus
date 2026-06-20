package com.example.pocketbank_plus.mapper;

import com.example.pocketbank_plus.pojo.Bill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BillMapper {
    public List<Bill> select();
    public List<Bill>selectByMonth(@Param("yearMonth") String yearMonth);
    public boolean insert(Bill bill);
    // 根据用户ID和月份查询花费分类统计
    public List<Map<String, Object>> selectExpenseStatistics(@Param("userId") Long userId,@Param("yearMonth") String yearMonth);
}
