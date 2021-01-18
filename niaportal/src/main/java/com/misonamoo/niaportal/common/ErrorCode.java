package com.misonamoo.niaportal.common;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ErrorCode {

    ERROR100(100, "필수 변수값 없음"),
    ERROR200(200, "정상처리"),
    ERROR400(400, "서버 요청 실패 오류"),
    ERROR500(500, "서버 오류"),
    ERROR101(101, "중복된 ID"),
    ERROR102(102, "비밀번호 없음"),
    ERROR103(103, "탈퇴한 회원 아이디"),
    ERROR104(104, "접근권한 없음"),
    ERROR105(105, "요청정보 없음"),
    ERROR106(106, "인증 기간 만료"),
    ERROR107(107, "인증 코드 불일치"),
    ERROR108(108, "비밀번호 재설정 유효기간 만료"),
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
