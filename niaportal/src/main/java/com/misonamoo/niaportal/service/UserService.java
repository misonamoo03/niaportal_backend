package com.misonamoo.niaportal.service;

import com.misonamoo.niaportal.domain.User;


public interface UserService {
    public User login(User vo);

    public int setPw(User vo);

    public int dupEmail(User user);

    public void register(User user);
    public void delete(User user);
    public void withdraw(User user);
    public void edit(User user);
    public int checkEmailPass(User user);

    public int findUserNo(User user);

    public int deletedUser(User user);
    public User inquiry(User user);
}
