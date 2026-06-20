//package com.example.pocketbank_plus.controller;
//
//import com.alibaba.dashscope.aigc.generation.Generation;
//import com.alibaba.dashscope.aigc.generation.GenerationParam;
//import com.alibaba.dashscope.aigc.generation.GenerationResult;
//import com.alibaba.dashscope.common.Message;
//import com.alibaba.dashscope.common.Role;
//import com.example.pocketbank_plus.pojo.Account;
//import com.example.pocketbank_plus.pojo.Result;
//import com.example.pocketbank_plus.service.AccountService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/ai")
//@CrossOrigin(originPatterns = "*", allowCredentials = "true") // 允许前端跨域访问
//public class AiController {
//
//    @Autowired
//    private AccountService accountService;
//
//    // 🔴 你的 API-KEY
//    private final String MY_API_KEY = "sk-cc0718eaa884499693009dde96a97fda";
//
//    @PostMapping("/chat")
//    public Result chat(@RequestBody Map<String, Object> payload) {
//        // 1. 获取前端传参
//        Object msgObj = payload.get("message");
//        Object idObj = payload.get("userId");
//
//        if (msgObj == null || idObj == null) {
//            return Result.fail("消息或用户ID不能为空");
//        }
//
//        String userMsg = msgObj.toString();
//        Long userId = Long.valueOf(idObj.toString());
//
//        // 2. 意图识别：查询数据库余额
//        if (userMsg.contains("余额") || userMsg.contains("钱") || userMsg.contains("资产")) {
//            List<Account> accounts = accountService.findByUserId(userId);
//            if (accounts == null || accounts.isEmpty()) {
//                return Result.success("AI助理", "主人，您目前还没有开通电子账户哦，点击首页的‘开户’即可办理。");
//            }
//            double total = accounts.stream().mapToDouble(a -> a.getBalance().doubleValue()).sum();
//            return Result.success("AI助理", "报告主人！我为您找到了 " + accounts.size() + " 个账户，总资产合计为 ¥" + total + "。资金状态非常健康！");
//        }
//
//        // 3. 泛化聊天：调用通义千问大模型
//        try {
//            Generation gen = new Generation();
//
//            // 构造消息列表
//            Message systemMsg = Message.builder()
//                    .role(Role.SYSTEM.getValue())
//                    .content("你现在是PocketBank智能银行的AI管家。你专业、幽默、贴心。只能回答金融相关或简单的问候。")
//                    .build();
//            Message userMsgObj = Message.builder()
//                    .role(Role.USER.getValue())
//                    .content(userMsg)
//                    .build();
//
//            // 构造请求参数
//            GenerationParam param = GenerationParam.builder()
//                    .model("qwen-turbo") // 🔴 使用官方标准 ID
//                    .messages(Arrays.asList(systemMsg, userMsgObj))
//                    .resultFormat(GenerationParam.ResultFormat.MESSAGE) // 建议显式指定返回消息格式
//                    .apiKey(MY_API_KEY)
//                    .build();
//
//            // 发起同步调用
//            GenerationResult result = gen.call(param);
//
//            // 🟢 健壮性检查：防止空指针异常
//            if (result != null && result.getOutput() != null &&
//                    result.getOutput().getChoices() != null && !result.getOutput().getChoices().isEmpty()) {
//
//                // 成功拿到 AI 回复
//                String aiResponse = result.getOutput().getChoices().get(0).getMessage().getContent();
//                return Result.success("AI助理", aiResponse);
//
//            } else {
//                // 🔴 兼容性修改：如果 getCode() 报错，尝试直接打印 result 对象或使用 getStatus()
//                System.err.println("--- AI 调用失败详情 ---");
//
//                // 如果 result 不为空，直接打印整个 result，控制台会显示所有错误字段
//                if (result != null) {
//                    System.err.println("完整响应内容: " + result.toString());
//                    // 通常也可以通过以下方式获取（取决于你的版本）：
//                    // String error = result.getMessage();
//                }
//
//                return Result.fail("AI 助理由于权限或额度问题暂时无法回复，请检查控制台日志");
//            }
//        } catch (Exception e) {
//            // 捕捉 SDK 异常（如网络中断、URL错误等）
//            e.printStackTrace();
//            return Result.fail("AI 助理连接异常，请检查网络或配置: " + e.getMessage());
//        }
//    }
//}
package com.example.pocketbank_plus.controller;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.example.pocketbank_plus.pojo.Account;
import com.example.pocketbank_plus.pojo.Result;
import com.example.pocketbank_plus.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@CrossOrigin(originPatterns = "*", allowCredentials = "true") // 允许前端跨域访问
public class AiController {

    @Autowired
    private AccountService accountService;

    // 🔴 你的 API-KEY（已保持原样，生产环境建议放入 application.yml）
    private final String MY_API_KEY = "sk-cc0718eaa884499693009dde96a97fda";

    @PostMapping("/chat")
    public Result chat(@RequestBody Map<String, Object> payload) {
        // 1. 获取前端传参
        Object msgObj = payload.get("message");
        Object idObj = payload.get("userId");

        if (msgObj == null || idObj == null) {
            return Result.fail("消息或用户ID不能为空");
        }

        String userMsg = msgObj.toString();
        Long userId = Long.valueOf(idObj.toString());

        // 2. 💡 【硬核 RAG 改造】技术点：上下文资产数据动态检索与预热
        // 不管用户是问余额还是做分析，先去数据库捞出当前用户的真实名下账户
        List<Account> accounts = accountService.findByUserId(userId);

        // 构造注入给大模型的私有金融知识库上下文（Context）
        StringBuilder bankContext = new StringBuilder();
        if (accounts == null || accounts.isEmpty()) {
            bankContext.append("该用户目前在系统中尚未开通任何电子银行账户。资产总计：0元。建议引导其在首页点击『开户』按钮办理。");
        } else {
            double totalBalance = accounts.stream().mapToDouble(a -> a.getBalance().doubleValue()).sum();
            bankContext.append("【PocketBank 核心账务实时联动快照】\n");
            bankContext.append("- 持有卡片总量: ").append(accounts.size()).append(" 张\n");
            bankContext.append("- 大盘资产总合计: ￥").append(totalBalance).append(" 元\n");
            bankContext.append("- 明细卡片账目状况: \n");
            for (int i = 0; i < accounts.size(); i++) {
                Account acc = accounts.get(i);
                bankContext.append("  卡片[").append(i + 1).append("] 账号: ").append(acc.getAccountNo())
                        .append(" | 账户可用余额: ￥").append(acc.getBalance()).append("元\n");
            }
        }

        // 3. 🤖 将私有上下文灌入通义千问大模型（Qwen-Turbo Pipeline）
        try {
            Generation gen = new Generation();

            // 💡 系统级 Prompt 调教：把数据库查出来的 bankContext 作为最核心的约束背景喂给大模型
            String systemPrompt = "你现在是 PocketBank 智能网银系统的 AI 首席财务分析官。你专业、幽默、贴心，具备金融级严谨度。\n"
                    + "请始终基于以下【真实的网银实时资产数据】来为用户提供深度透视、理财规划、财务账单分析或余额答疑。如果用户提出理财建议，请给出合理的资产配置。\n"
                    + "特别注意：回答时严禁泄露完整银行卡号，卡号只能展示后 4 位，其余用 * 遮蔽脱敏！\n\n"
                    + "--- 当前用户的真实资产上下文 ---\n"
                    + bankContext.toString();

            Message systemMsg = Message.builder()
                    .role(Role.SYSTEM.getValue())
                    .content(systemPrompt)
                    .build();

            Message userMsgObj = Message.builder()
                    .role(Role.USER.getValue())
                    .content(userMsg)
                    .build();

            // 构造请求参数
            GenerationParam param = GenerationParam.builder()
                    .model("qwen-turbo")
                    .messages(Arrays.asList(systemMsg, userMsgObj))
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .apiKey(MY_API_KEY)
                    .build();

            // 发起同步调用
            GenerationResult result = gen.call(param);

            // 🟢 健壮性检查
            if (result != null && result.getOutput() != null &&
                    result.getOutput().getChoices() != null && !result.getOutput().getChoices().isEmpty()) {

                String aiResponse = result.getOutput().getChoices().get(0).getMessage().getContent();
                return Result.success("AI助理", aiResponse);

            } else {
                System.err.println("--- AI 调用失败详情 ---");
                if (result != null) {
                    System.err.println("完整响应内容: " + result.toString());
                }
                return Result.fail("AI 助理由于权限或额度问题暂时无法回复，请检查控制台日志");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("AI 助理连接异常，请检查网络或配置: " + e.getMessage());
        }
    }
}