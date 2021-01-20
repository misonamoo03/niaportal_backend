package com.misonamoo.niaportal.mapper;


import com.misonamoo.niaportal.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserMapper {
    /**
     * 로그인
     *
     * @param VO - 회원 정보가 담긴 User
     * @return
     * @throws Exception
     */
    public User login(User VO);

    /**
     * 비밀번호 재설정
     *
     * @param VO - 회원 정보가 담긴 UserV
     * @throws Exception
     */
    public int setPw(User VO);

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
