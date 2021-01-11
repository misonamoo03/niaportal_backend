package com.misonamoo.niaportal.mapper;

import com.misonamoo.niaportal.vo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface UserMapper {
    /**
     * 로그인
     *
     * @param VO - 회원 정보가 담긴 UserVO
     * @return
     * @throws Exception
     */
    public User login(User VO);

    /**
     * 아이디 찾기
     *
     * @param VO - 회원 정보가 담긴 UserVO
     * @return
     * @throws Exception
     */
    public String findId(User VO) throws Exception;

    /**
     * 비밀번호 재설정
     *
     * @param VO - 회원 정보가 담긴 UserVO
     * @return
     * @throws Exception
     */
    public int setPw(User VO);
}
