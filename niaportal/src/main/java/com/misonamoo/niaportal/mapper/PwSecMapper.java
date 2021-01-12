package com.misonamoo.niaportal.mapper;


import com.misonamoo.niaportal.domain.PwSec;
import com.misonamoo.niaportal.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface PwSecMapper {
    /**
     * 코드 찾기
     *
     * @param userNO - 회원 정보가 담긴 User
     * @return
     * @throws Exception
     */
    public String findCode(int userNO);


    /**
     * 비밀번호 코드 삽입
     *
     * @param pwSec - 회원 정보가 담긴 User
     * @return
     * @throws Exception
     */
    public void setCode(PwSec pwSec);

    /**
     * 비밀번호 코드 변경
     *
     * @param pwSec - 회원 정보가 담긴 User
     * @return
     * @throws Exception
     */
    public void updateCode(PwSec pwSec);
}
