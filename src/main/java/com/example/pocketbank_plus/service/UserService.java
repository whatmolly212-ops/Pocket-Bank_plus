package com.example.pocketbank_plus.service;

import com.example.pocketbank_plus.pojo.User;

public interface UserService {
    public User getByPhone(String phone);

    public boolean update(User user);

    public String register(User user);

    public User login(String phone,String password);

    public User lookUpInformation(Long id);

    public boolean modifyInformation(User user);
}
