package com.example.pocketbank_plus.mapper;

import com.example.pocketbank_plus.pojo.Account;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface AccountMapper {
    public List<Account> selectByuserId(Long userId);//通过用户id查帐户，用于登录后的查询

    public Account selectByaccountNo(String accountNo);//通过账户id查帐户，通常给用于转账

    public Account selectByid(Long id);

    public int updateBalance_plus(String accountNo,BigDecimal money);
    public int updateBalance_reduce(String accountNo,BigDecimal money);

    public int insert(Account account);//成功返回1，失败返回0
//    public void recharge();//这个是不是要仅仅换成查询和update
//    public void withdraw();//具体功能的实现应该在service?
}
