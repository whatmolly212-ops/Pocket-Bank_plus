package com.example.pocketbank_plus.mapper;

import com.example.pocketbank_plus.pojo.TransactionRecord;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface TransactionRecordMapper {
    public List<TransactionRecord> selectByAccountNo(String accountNo);//思考查询什么时候要用List，什么时候只需要一个对象接收

    public int insert(TransactionRecord record);

    public List<BigDecimal> selectRecentAmounts(String accountNo);
}
