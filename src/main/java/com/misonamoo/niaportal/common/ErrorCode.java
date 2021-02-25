package com.misonamoo.niaportal.common;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ErrorCode {

    ERROR100(100, "필수 변수값 없음"),
    ERROR200(200, "정상처리"),
    ERROR400(400, "서버 요청 실패 오류"),
    ERROR500(500, "서버 오류"),
    ERROR101(101, "중복된 이메일입니다."),
    ERROR102(102, "아이디 또는 비밀번호가 일치하지 않습니다."),
    ERROR103(103, "탈퇴한 회원 ID"),
    ERROR104(104, "접근권한 없음"),
    ERROR105(105, "요청정보 없음"),
    ERROR106(106, "인증번호 유효시간이 만료되었습니다."),
    ERROR107(107, "인증번호가 일치하지 않습니다."),
    ERROR108(108, "비밀번호 재설정 유효시간이 만료되었습니다."),
    ERROR109(109, "아이디 없음"),
    ERROR131(131, "승인요청중"),
    ERROR132(132, "승인완료"),
    ERROR133(133, "승인반려");

    private int code;
    private String  message;
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
