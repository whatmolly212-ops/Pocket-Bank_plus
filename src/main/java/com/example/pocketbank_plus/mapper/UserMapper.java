package com.example.pocketbank_plus.mapper;

import com.example.pocketbank_plus.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    public User selectById(Long id);

    public User selectByPhone(String phone);

    public int insertUser(User user);

    public int update(User user);

    public int countByPhone(String phone);//作用是什么
}
