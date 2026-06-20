package com.example.pocketbank_plus.service;

import com.example.pocketbank_plus.mapper.UserMapper;
import com.example.pocketbank_plus.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserMapper usermapper;//为了使用user的相关方法

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    //如果需要用到对象的多个属性，即传整个实体对象
    //如果只需要一个属性，传单个属性
    //注册
    public String register(User user){
        if(user.getPhone()==null){
            //user.getPhone().trim().isEmpty()这什么意思
            return "注册失败：手机号不能为空！";
        }

        String phoneRegex = "^1[3-9]\\d{9}$";
        if (!user.getPhone().matches(phoneRegex)) {
            return "注册失败：手机号格式错误！";
        }

        int count=usermapper.countByPhone(user.getPhone());
        if(count>0){
            return "注册失败:该手机已被注册!";
        }

        if (user.getIdCard() == null || user.getIdCard().trim().isEmpty()) {
            return "注册失败：身份证号不能为空！";
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return "注册失败：请设置登录密码！";
        }

        //对用户传来的密码进行加密
        String rawPassword=user.getPassword();
        String encodedPassword=passwordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);

        user.setCreateTime(LocalDateTime.now());
        int result=usermapper.insertUser(user);
        if(result==1){
            return "注册成功！";
        }
        else{
            return "注册失败：数据库插入异常！";
        }
    }



    public User login(String phone,String password){
        User user = usermapper.selectByPhone(phone);
        if (user == null) return null;

        boolean isMatch=passwordEncoder.matches(password,user.getPassword());
        if(isMatch){
            return user;
        }
       else{
           return null;
        }

    }
    //查询用户信息(对于这个简单版项目，个人理解是相当于登录，其实就是根据信息查询)
    //❗System.out.println(user)只是自己看日志，前端完全收不到
    public User lookUpInformation(Long id){
        return usermapper.selectById(id);
    }



    public boolean modifyInformation(User user){
//        usermapper.update(user);
//        return true;
        User exist = usermapper.selectById(user.getId());
        if (exist == null) {
            return false;
        }

        int rows = usermapper.update(user);
        return rows > 0;
    }

    public User getByPhone(String phone){
        return usermapper.selectByPhone(phone);
    }

    public boolean update(User user){
        int result=usermapper.update(user);
        if(result!=0)
            return true;
        else return false;
    }
}
