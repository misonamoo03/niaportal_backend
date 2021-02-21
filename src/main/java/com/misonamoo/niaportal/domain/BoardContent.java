package com.misonamoo.niaportal.domain;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class BoardContent extends BasePaging {

    private String boardContentNo;
    private String orgBoardContentNo;
    private Long boardNo;
    private Long userNo;
    private String userName;
    private String title;
    private String content;
    private String secYn;
    private Long viewCnt;
    private int replyCnt;
    private String contentGroup;
    private String regDate;
    private String updDate;
    private List<BoardContent> replyList;
}
