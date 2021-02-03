package com.misonamoo.niaportal.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Board extends BasePaging {
    private Long boardNo;
    private Long userNo;
    private String userName;
    private String title;
    private String content;
    private Long boardContentNo;
    private String orgBoardContentNo;
    private String name;
    private String boardTypeCode;
    private String sportsBoardCode;
    private LocalDateTime regDate;
    private Long regUserNo;
    private LocalDateTime updDate;
    private Long updUserNo;
    private String secYn;
    private Long contentGroup;
    
    // 파라미터로 온 변수
    private String email;
    private String userGbCode;

    public Board() {
        boardNo = 0L;

    }
}
