package com.misonamoo.niaportal.service;

import com.misonamoo.niaportal.vo.User;

import java.util.List;

public interface UserService {
    public User login(User vo);
    public String findId(User vo )throws Exception;
    public int setPw(User vo);
}
