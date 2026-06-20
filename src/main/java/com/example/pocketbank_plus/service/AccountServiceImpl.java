//controller和service层是按照业务模块来规定划分几个类
package com.example.pocketbank_plus.service;

import com.example.pocketbank_plus.mapper.AccountMapper;
import com.example.pocketbank_plus.mapper.TransactionRecordMapper;
import com.example.pocketbank_plus.mapper.UserMapper;
import com.example.pocketbank_plus.pojo.Account;
import com.example.pocketbank_plus.pojo.TransactionRecord;
import com.example.pocketbank_plus.pojo.User;
import com.example.pocketbank_plus.pojo.dto.TransferRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private TransactionRecordMapper transactionRecordMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder encoder;

    public Account getAccount(String accountNo) {
        return accountMapper.selectByaccountNo(accountNo);
    }

    @Override
    @Transactional // 保证开户过程的原子性
    public String createAccount(Long userId, String payPassword, BigDecimal initialBalance) {
        Account account = new Account();

        // 1. 绑定用户
        account.setUserId(userId);

        // 2. 自动生成账号 (可以使用随机数或时间戳，银行账号通常比较长)
        String newAccountNo = "6222" + System.currentTimeMillis();
        account.setAccountNo(newAccountNo);

        // 3. 加密交易密码 (非常重要！)
        account.setPayPassword(encoder.encode(payPassword));

        // 4. 设置余额和时间
        account.setBalance(initialBalance);
        account.setCreateTime(LocalDateTime.now());

        // 5. 调用 Mapper 写入数据库
        accountMapper.insert(account);

        return newAccountNo;
    }


    /**
     * 1. 充值业务
     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public BigDecimal recharge(BigDecimal money, String accountNo) {
//        // 校验金额
//        validateAmount(money);
//
//        // 底层加钱
//        executePlus(accountNo, money);
//
//        // 记录流水：充值没有目标账户，传 null
//        saveTransactionRecord(accountNo, null, "RECHARGE", money, "账户充值");
//
//        return accountMapper.selectByaccountNo(accountNo).getBalance();
//    }


    // 在 AccountServiceImpl.java 中添加实现
    @Override
    public List<Account> findByUserId(Long userId) {
        return accountMapper.selectByuserId(userId);
    }

    @Override
    public Account findByAccountNo(String accountNo) {
        return accountMapper.selectByaccountNo(accountNo);
    }

    @Override
    public Account findById(Long id){
        return accountMapper.selectByid(id);
    }
    /**
     * 2. 取钱业务
     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public BigDecimal withdraw(BigDecimal money, String accountNo,String payPassword) {
//        System.out.println(">>> [业务层-取现] 收到前端请求账号: [" + accountNo + "]，金额: " + money);
//        String encodedPassword=getAccount(accountNo).getPayPassword();
//        boolean isMatch=encoder.matches(payPassword,encodedPassword);
//        if (!isMatch) {
//            // 如果不匹配，必须抛出异常或返回特定标识，否则程序会继续往下走
//            throw new RuntimeException("交易密码错误，请重新输入！");
//        }
//        // 校验金额
//        validateAmount(money);
//
//        // 底层减钱 (内部包含余额充足校验)
//        executeReduce(accountNo, money);
//
//        // 记录流水：取钱没有目标账户，传 null
//        saveTransactionRecord(accountNo, null, "WITHDRAW", money, "账户取现");
//
//        return accountMapper.selectByaccountNo(accountNo).getBalance();
//    }

    /**
     * 3. 转账业务
     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public BigDecimal transfer(BigDecimal money, String sourceAccountNo, String targetAccountNo, String remark,String payPassword) {
//        System.out.println(">>> [业务层-转账] 收到前端请求源账号: [" + sourceAccountNo + "]，目标账号: [" + targetAccountNo + "]，金额: " + money);
//        String encodedPassword=getAccount(sourceAccountNo).getPayPassword();
//        boolean isMatch=encoder.matches(payPassword,encodedPassword);
//        if (!isMatch) {
//            // 如果不匹配，必须抛出异常或返回特定标识，否则程序会继续往下走
//            throw new RuntimeException("交易密码错误，请重新输入！");
//        }
//        // 校验金额
//        validateAmount(money);
//        if (sourceAccountNo.equals(targetAccountNo)) {
//            throw new RuntimeException("不能给自己转账");
//        }
//
//        // 执行转账核心逻辑：一减一加
//        executeReduce(sourceAccountNo, money);
//        executePlus(targetAccountNo, money);
//
//        // 记录流水：转账有明确的目标账户
//        saveTransactionRecord(sourceAccountNo, targetAccountNo, "TRANSFER", money, remark);
//
//        return accountMapper.selectByaccountNo(sourceAccountNo).getBalance();
//    }
    /**
     * 统一风控与身份核验网关（私有工具函数）
     */

    /**
     * 1. 充值业务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BigDecimal recharge(TransferRequest trans) {
        // 核心亮点：交易函数前方的有条件身份验证
        verifyRiskAndIdentity(trans);

        // 校验金额
        validateAmount(trans.getMoney());

        // 底层加钱
        executePlus(trans.getTargetAccountNo(), trans.getMoney());

        // 记录流水
        saveTransactionRecord(trans.getTargetAccountNo(), null, "RECHARGE", trans.getMoney(), "账户充值");

        return accountMapper.selectByaccountNo(trans.getTargetAccountNo()).getBalance();
    }

    /**
     * 2. 取钱业务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BigDecimal withdraw(TransferRequest trans) {
        System.out.println(">>> [业务层-取现] 收到前端请求账号: [" + trans.getSourceAccountNo() + "]，金额: " + trans.getMoney());

        // 核心亮点：交易函数前方的有条件身份验证
        verifyRiskAndIdentity(trans);

        String encodedPassword = getAccount(trans.getSourceAccountNo()).getPayPassword();
        boolean isMatch = encoder.matches(trans.getPassword(), encodedPassword);
        if (!isMatch) {
            throw new RuntimeException("交易密码错误，请重新输入！");
        }

        // 校验金额
        validateAmount(trans.getMoney());

        // 底层减钱 (内部包含余额充足校验)
        executeReduce(trans.getSourceAccountNo(), trans.getMoney());

        // 记录流水
        saveTransactionRecord(trans.getSourceAccountNo(), null, "WITHDRAW", trans.getMoney(), "账户取现");

        return accountMapper.selectByaccountNo(trans.getSourceAccountNo()).getBalance();
    }

    /**
     * 3. 转账业务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BigDecimal transfer(TransferRequest trans) {
        System.out.println(">>> [业务层-转账] 收到前端请求源账号: [" + trans.getSourceAccountNo() + "]，目标账号: [" + trans.getTargetAccountNo() + "]，金额: " + trans.getMoney());

        // 核心亮点：交易函数前方的有条件身份验证
        verifyRiskAndIdentity(trans);

        String encodedPassword = getAccount(trans.getSourceAccountNo()).getPayPassword();
        boolean isMatch = encoder.matches(trans.getPassword(), encodedPassword);
        if (!isMatch) {
            throw new RuntimeException("交易密码错误，请重新输入！");
        }

        // 校验金额
        validateAmount(trans.getMoney());
        if (trans.getSourceAccountNo().equals(trans.getTargetAccountNo())) {
            throw new RuntimeException("不能给自己转账");
        }

        // 执行转账核心逻辑：一减一加
        executeReduce(trans.getSourceAccountNo(), trans.getMoney());
        executePlus(trans.getTargetAccountNo(), trans.getMoney());

        // 记录流水
        saveTransactionRecord(trans.getSourceAccountNo(), trans.getTargetAccountNo(), "TRANSFER", trans.getMoney(), trans.getRemark());

        return accountMapper.selectByaccountNo(trans.getSourceAccountNo()).getBalance();
    }

    // ================= 以下为私有工具方法 (Private Helpers) =================

    /**
     * 底层更新：加钱
     */
    private void executePlus(String accountNo, BigDecimal money) {
        int rows = accountMapper.updateBalance_plus(accountNo, money);
        if (rows == 0) {
            throw new RuntimeException("操作失败，账号不存在：" + accountNo);
        }
    }

    /**
     * 底层更新：减钱 (包含余额检查)
     */
    private void executeReduce(String accountNo, BigDecimal money) {
        // 必须先查一下余额，用于逻辑判断
        Account account = accountMapper.selectByaccountNo(accountNo);
        if (account == null) {
            throw new RuntimeException("账号不存在：" + accountNo);
        }
        if (account.getBalance().compareTo(money) < 0) {
            throw new RuntimeException("账户余额不足，当前余额：" + account.getBalance());
        }

        // 原子减法
        int rows = accountMapper.updateBalance_reduce(accountNo, money);
        if (rows == 0) {
            throw new RuntimeException("并发操作导致失败，请重试");
        }
    }

    /**
     * 统一流水记录方法
     */
    private void saveTransactionRecord(String accNo, String targetAccNo, String type, BigDecimal amount, String remark) {
        TransactionRecord record = new TransactionRecord();
        record.setAccountNo(accNo);
        record.setTargetAccountNo(targetAccNo); // 如果是 null, MyBatis 会自动处理为数据库的 NULL
        record.setType(type);
        record.setAmount(amount);
        record.setCreateTime(LocalDateTime.now());
        record.setRemark(remark);
        transactionRecordMapper.insert(record);
    }

    /**
     * 金额合法性校验
     */
    private void validateAmount(BigDecimal money) {
        if (money == null || money.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("交易金额必须大于0");
        }
    }

    public List<TransactionRecord> getRecord(String accountNo){
        return transactionRecordMapper.selectByAccountNo(accountNo);
    }

    public String detectRiskLevel(String accountNo, BigDecimal currentAmount) {
        if (currentAmount.compareTo(new BigDecimal("100000")) > 0) {
            return "HIGH";
        }
        List<BigDecimal> history = transactionRecordMapper.selectRecentAmounts(accountNo);
        System.out.println(">>> 风控调试 - 历史流水条数: " + (history == null ? "null" : history.size()));
        System.out.println(">>> 风控调试 - 历史流水详情: " + history);
        // 样本太少（比如新用户），不具备统计学意义，默认无风险
        if (history == null || history.size() < 10) {
            return "NONE";
        }

        // 1. 计算平均值 (Mean)
        double sum = 0;
        for (BigDecimal amt : history) sum += amt.doubleValue();
        double mean = sum / history.size();

        // 2. 计算标准差 (Standard Deviation)
        double squareSum = 0;
        for (BigDecimal amt : history) {
            squareSum += Math.pow(amt.doubleValue() - mean, 2);
        }
        double stdDev = Math.sqrt(squareSum / history.size());

        // 3. 计算 Z-Score (当前金额偏离平均值的程度)
        // 如果标准差为 0（说明过去每笔钱都一模一样），则根据金额是否相等判断
        if (stdDev == 0) return currentAmount.doubleValue() > mean ? "HIGH" : "NONE";

        double zScore = (currentAmount.doubleValue() - mean) / stdDev;

        // 4. 定义风控阈值
        if (zScore > 3.0) return "HIGH"; // 极度异常（超过3倍标准差）
        if (zScore > 2.0) return "LOW";  // 轻微异常
        return "NONE";
    }

    private void verifyRiskAndIdentity(TransferRequest trans) {
        // 如果前端检测出是高风险，或者后端在最终防线判定也是 HIGH
        if ("HIGH".equals(trans.getRiskLevel())) {
            System.out.println(">>> [风控触发] 检测到高风险交易，启动二级身份核验...");

            // 1. 安全防御性校验：防止前端恶意篡改数据导致 userId 或 idCard 丢失
            if (trans.getUserId() == null || trans.getIdCard() == null || trans.getIdCard().trim().isEmpty()) {
                throw new RuntimeException("风险拦截：高风险交易必须提供完整的实名核验信息！");
            }

            // 2. 寻找真正的身份证号进行判断
            Account account = accountMapper.selectByaccountNo(trans.getSourceAccountNo());
            if (account == null) {
                throw new RuntimeException("安全核验失败：源银行账户不存在！");
            }

            User user = userMapper.selectById(account.getUserId());
            if (user == null) {
                throw new RuntimeException("安全核验失败：未找到对应的开户人信息！");
            }

            String realIdCard = user.getIdCard();
            if (realIdCard == null || realIdCard.trim().isEmpty()) {
                throw new RuntimeException("安全核验失败：系统内该开户人未绑定实名身份证！");
            }

            // 3. 严格比对
            if (!realIdCard.equals(trans.getIdCard().trim())) {
                throw new RuntimeException("安全核验失败：输入的身份证号与系统实名信息不符，交易已被终止！");
            }

            System.out.println(">>> [风控通过] 用户身份核验一致，予以放行。");
        }
    }
}
