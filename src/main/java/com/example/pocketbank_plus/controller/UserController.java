package com.example.pocketbank_plus.controller;

import com.example.pocketbank_plus.pojo.Result;
import com.example.pocketbank_plus.pojo.User;
import com.example.pocketbank_plus.pojo.dto.LoginDTO;
import com.example.pocketbank_plus.pojo.dto.ResetPasswordDTO;
import com.example.pocketbank_plus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userservice;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/register")//定义接口的访问路径和请求方式，PostMapping是用于新增的
    //@RequestBody将前端发过来的JSON字符串自动转化为JSON对象
    public Result register(@RequestBody User user){
        String msg=userservice.register(user);

        //注意静态方法的调用是直接用类名调用
        if(msg.equals("注册成功！")){
            return Result.success(msg);
        }
        else{
            return Result.fail(msg);
        }
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO log){
        User user=userservice.login(log.getPhone(),log.getPassword());
        if(user != null){
            // 2. ✅ 关键点：把整个 user 对象塞进去，这样前端才能读到 user.id
            return Result.success("登录成功",user);
        }
        else{
            // 3. 如果没查到，说明手机号或密码错了
            return Result.fail("手机号或密码错误");
        }
    }

    @GetMapping("/getInformation")
    public Result getInformation(@RequestParam Long id){
        User user=userservice.lookUpInformation(id);
        if(user!=null){
            return Result.success("查询成功",user);
        }
        else{
            return Result.fail("查询失败");
        }
    }

    @PutMapping("/update")//用于修改现有的
    public Result update(@RequestBody User user){
        boolean result = userservice.modifyInformation(user);
        if(result==true){
            return Result.success("修改成功");
        }
        else{
            return Result.fail("修改失败");
        }
    }

    @PostMapping("/resetPassword")
    public Result resetPassword(@RequestBody ResetPasswordDTO dto) {
        // 1. 验证码校验 (第一道防线)
        // 实际开发：String serverCode = redisTemplate.opsForValue().get("SMS:" + dto.getPhone());
        // 这里我们先模拟校验逻辑，假设验证码是正确的
        if (dto.getCode() == null || dto.getCode().isEmpty()) {
            return Result.fail("请输入验证码");
        }

        // 2. 根据手机号查询用户
        User user = userservice.getByPhone(dto.getPhone());

        // 3. 核心校验：用户是否存在，身份证是否匹配
        if (user == null) {
            return Result.fail("该手机号未注册");
        }

        if (!user.getIdCard().equals(dto.getIdCard())) {
            return Result.fail("身份证号与预留信息不符");
        }

        // 4. (模拟) 校验验证码是否匹配
        // 如果你前端生成的验证码通过接口传不过来，这里可以先写死一个值测试，或者跳过
        // if (!dto.getCode().equals(serverCode)) { return Result.error("验证码错误"); }

        // 5. 校验通过，更新密码
        String rawPassword=dto.getNewPassword();
        String encodedPassword=passwordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);
        System.out.println("准备更新用户密码❗: " + user.getPassword());
        userservice.update(user);

        return Result.success("密码重置成功");
    }
}
