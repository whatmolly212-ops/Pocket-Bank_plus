package com.example.pocketbank_plus.service;

import com.example.pocketbank_plus.mapper.BillMapper;
import com.example.pocketbank_plus.pojo.Bill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class BillServiceImpl implements BillService{
    @Autowired
    BillMapper billMapper;
    public boolean addAccount(Bill bill) {
        if (bill.getCreateTime() == null) {
            bill.setCreateTime(LocalDateTime.now());
        }

        boolean result=billMapper.insert(bill);

        if(result)
            return true;
        else
            return false;
    }

    public List<Bill> getRecord(String yearMonth){
        if(yearMonth==null)
        {
            return billMapper.select();
        }
        return billMapper.selectByMonth(yearMonth);
    }

    public List<Map<String, Object>> visualization(Long userId, String yearMonth){

        return billMapper.selectExpenseStatistics(userId,yearMonth);

    }
}
