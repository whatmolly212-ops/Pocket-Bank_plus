package com.example.pocketbank_plus.controller;


import com.example.pocketbank_plus.pojo.Account;
import com.example.pocketbank_plus.pojo.AccountVO;
import com.example.pocketbank_plus.pojo.Result;
import com.example.pocketbank_plus.pojo.TransactionRecord;
import com.example.pocketbank_plus.pojo.dto.AccountCreateDTO;
import com.example.pocketbank_plus.pojo.dto.TransferRequest;
import com.example.pocketbank_plus.pojo.dto.UnmaskDTO;
import com.example.pocketbank_plus.service.AccountService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController  // 标记这是控制器，且返回JSON数据（前后端分离常用）
@RequestMapping("/account")//用来申请请求路径，把前端发来的 HTTP 请求（比如 http://localhost:8080/demo/user）和后端 Controller 中的类 / 方法绑定起来
public class AccountController {
    @Autowired
    AccountService accountService;

    @Autowired
    private BCryptPasswordEncoder encoder;

//    public static String maskCardNo(String cardNo) {
//        if (cardNo == null || cardNo.length() < 10) return cardNo;
//        return cardNo.substring(0, 4) + " **** **** " + cardNo.substring(cardNo.length() - 4);
//    }

    @PostMapping("/createAccount")
    public Result createAccount(@RequestBody AccountCreateDTO dto) {

        // 1. 基本校验
        if (dto.getUserId() == null) {
            return Result.fail("开户失败：用户ID不能为空");
        }
        if (dto.getPayPassword() == null || dto.getPayPassword().length() < 6) {
            return Result.fail("开户失败：交易密码至少需要6位");
        }

        // 2. 设置默认余额
        BigDecimal balance = dto.getInitialBalance() != null ? dto.getInitialBalance() : BigDecimal.ZERO;

        try {
            // 3. 调用 Service 执行开户逻辑
            // 返回新生成的账号 (AccountNo)
            String accountNo = accountService.createAccount(dto.getUserId(), dto.getPayPassword(), balance);

            return Result.success("开户成功！您的账号为：" + accountNo);
        } catch (Exception e) {
            return Result.fail("开户失败：" + e.getMessage());
        }
    }

    // AccountController.java
//,@RequestBody UnmaskDTO mask
//    @GetMapping("/getInformation")
//    public Result getInformation(@RequestParam Long id) {
//        try {
//            // 调用你已经在 ServiceImpl 里写好的方法
//            List<Account> accounts = accountService.findByUserId(id);
//            for(Account account:accounts)
//            {
//                String mask_account=maskCardNo(account.getAccountNo());
//            }
//            return Result.success("查询成功", accounts);
//        } catch (Exception e) {
//            return Result.fail("查询账户失败：" + e.getMessage());
//        }
//    }
    @GetMapping("/getInformation")
    public Result getInformation(@RequestParam Long id) {
        try {
            List<Account> accounts = accountService.findByUserId(id);

            // 使用流或循环，将 Account 转换为 AccountVO
            List<AccountVO> voList = accounts.stream().map(acc -> {
                AccountVO vo = new AccountVO();
                BeanUtils.copyProperties(acc, vo); // 复制属性

                // 只对 VO 进行脱敏处理
                String originalNo = vo.getAccountNo();
                if (originalNo != null && originalNo.length() > 10) {
                    vo.setAccountNo(originalNo.substring(0, 4) + " **** " + originalNo.substring(originalNo.length() - 4));
                }
                return vo;
            }).collect(Collectors.toList());

            return Result.success("查询成功", voList);
        } catch (Exception e) {
            return Result.fail("查询失败");
        }
    }

    @PostMapping("/getUnmaskedNo")
    public Result getUnmaskedNo(@RequestBody UnmaskDTO unmask){
        System.out.println("Received ID: " + unmask.getId() + ", Password: " + unmask.getPayWord());
        Account account=accountService.findById(unmask.getId());
        if (account == null) {
            return Result.fail("查询失败：账号不存在");
        }
        String sch_password=account.getPayPassword();
        boolean isMatch=encoder.matches(unmask.getPayWord(),sch_password);
        if(isMatch){
            return Result.success("验证成功",account.getAccountNo());
        }
        else{
            return Result.fail("支付密码错误");
        }
    }

    @PostMapping("/transfer")
    public Result transferMoney(@RequestBody TransferRequest trans){
//        BigDecimal result=accountService.transfer(trans.getMoney(),trans.getSourceAccountNo(),trans.getTargetAccountNo(),trans.getRemark());
//        if(result!=null){
//            return Result.success("转账成功");
//        }
//        else{
//            return Result.fail("转账失败");
//        }
        try {
            // 尝试去转账
            BigDecimal balance = accountService.transfer(trans);
            return Result.success("转账成功，当前余额：" + balance);
        } catch (RuntimeException e) {
            // 如果 Service 报错了，把报错的信息（例如 "余额不足"）接住，传给前端
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/recharge")
    public Result rechargeMoney(@RequestBody TransferRequest trans){
        try {
            // 尝试去转账
            BigDecimal balance = accountService.recharge(trans);
            return Result.success("存款成功，当前余额：" + balance);
        } catch (RuntimeException e) {
            // 如果 Service 报错了，把报错的信息（例如 "余额不足"）接住，传给前端
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/withdraw")
    public Result withdrawMoney(@RequestBody TransferRequest trans){
        try {
            // 尝试去转账
            BigDecimal balance = accountService.withdraw(trans);
            return Result.success("提现成功，当前余额：" + balance);
        } catch (RuntimeException e) {
            // 如果 Service 报错了，把报错的信息（例如 "余额不足"）接住，传给前端
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/getTransactionRecord")
    public Result getTransactionRecord(@RequestParam String accountNo){
        try {
            // 调用你已经在 ServiceImpl 里写好的方法
            List<TransactionRecord> record = accountService.getRecord(accountNo);
            return Result.success("查询账单明细成功", record);
        } catch (Exception e) {
            return Result.fail("查询账单明细失败：" + e.getMessage());
        }
    }

    @PostMapping("/detectRiskLevel")
    public Result checkRisk(@RequestBody TransferRequest request) {
        // 调用 Service 层我们写好的算法
        String level = accountService.detectRiskLevel(request.getSourceAccountNo(), request.getMoney());

        // 返回给前端
        Map<String, Object> data = new HashMap<>();
        data.put("riskLevel", level);
        return Result.success("成功",data);
    }

}
