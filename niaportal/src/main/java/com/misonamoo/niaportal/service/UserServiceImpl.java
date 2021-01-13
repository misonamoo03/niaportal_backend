package com.misonamoo.niaportal.service;

import com.misonamoo.niaportal.common.SHA256Util;
import com.misonamoo.niaportal.mapper.UserMapper;
import com.misonamoo.niaportal.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Value("${key}")
    private String salt;    // 비밀번호 암호화 키

    @Override
    public User login(User user) {
        return userMapper.login(user);
    }

    @Override
    public int setPw(User vo) {
        return userMapper.setPw(vo);
    }

    @Override
    public int dupEmail(User user) {
        return userMapper.dupEmail(user);
    }

    @Override
    public void register(User user) {
        String password = user.getPassword();
        password = SHA256Util.getEncrypt(password, salt);

        user.setPassword(password);
        userMapper.register(user);
    }

    @Override
    public void delete(User user) {
        userMapper.delete(user);
    }

    @Override
    public void withdraw(User user) {
        userMapper.withdraw(user);
    }

    @Override
    public void edit(User user) {
        userMapper.edit(user);
    }

    @Override
    public int checkEmailPass(User user) {
        return userMapper.checkEmailPass(user);
    }
    @Override
    public int findUserNo(User user) {
        return userMapper.findUserNo(user);
    }

}
