package com.misonamoo.niaportal.service;

import com.misonamoo.niaportal.vo.BoardVO;
import com.misonamoo.niaportal.vo.UserVO;

import java.util.List;

public interface UserService {
    public UserVO login(UserVO vo);
    public String findId(UserVO vo )throws Exception;
    public int setPw(UserVO vo);
}
