package com.example.pocketbank_plus.service;

import com.example.pocketbank_plus.pojo.Account;
import com.example.pocketbank_plus.pojo.TransactionRecord;
import com.example.pocketbank_plus.pojo.dto.TransferRequest;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    public String createAccount(Long userId, String payPassword, BigDecimal initialBalance);//返回账户号

    public List<Account> findByUserId(Long userId);

    public Account findById(Long id);

    public Account findByAccountNo(String accountNo);

    public BigDecimal recharge(TransferRequest trans);

    public BigDecimal withdraw(TransferRequest trans);

    public BigDecimal transfer(TransferRequest trans);

    public List<TransactionRecord> getRecord(String accountNo);

    public String detectRiskLevel(String accountNo, BigDecimal currentAmount);
}
