package com.misonamoo.niaportal.service;

import com.misonamoo.niaportal.common.SHA256Util;
import com.misonamoo.niaportal.mapper.UserMapper;
import com.misonamoo.niaportal.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.misonamoo.niaportal.common.CommonUtil.isNull;
import static com.misonamoo.niaportal.common.CommonUtil.setEncryptPass;

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
        //패스워드를 암호화해서 다시 패스워드에 저장
        user.setPassword(setEncryptPass(user.getPassword(), salt));

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
        if (!isNull(user.getNewPassword())) { //요청받은 새로운 패스워드가 있다면
            user.setPassword(setEncryptPass(user.getNewPassword(), salt)); //새로운 패스워드를 암호화하여 패스워드에 저장
        }
        userMapper.edit(user);
    }

    @Override
    public int checkEmailPass(User user) {
        user.setPassword(setEncryptPass(user.getPassword(), salt));

        return userMapper.checkEmailPass(user);
    }

    @Override
    public int findUserNo(User user) {
        return userMapper.findUserNo(user);
    }

    @Override
    public int deletedUser(User user) {
        return userMapper.deletedUser(user);
    }

    @Override
    public User inquiry(User user) {
        return userMapper.inquiry(user);
    }

}
