package com.misonamoo.niaportal.service;

import com.misonamoo.niaportal.mapper.UserMapper;
import com.misonamoo.niaportal.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public UserVO login(UserVO userVO) {
        return userMapper.login(userVO);
    }

    @Override
    public String findId(UserVO vo) throws Exception {
        return userMapper.findId(vo);
    }

    @Override
    public int setPw(UserVO vo) {
        return userMapper.setPw(vo);
    }
}
