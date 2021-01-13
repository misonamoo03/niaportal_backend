package com.misonamoo.niaportal.service;

import com.misonamoo.niaportal.mapper.UserMapper;
import com.misonamoo.niaportal.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public User login(User userVO) {
        return userMapper.login(userVO);
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
    public void regist(User user) {
        userMapper.regist(user);
    }

    @Override
    public void delete(User user) {
        userMapper.delete(user);
    }

    @Override
    public int findUserNo(User user) {
        return userMapper.findUserNo(user);
    }

}
