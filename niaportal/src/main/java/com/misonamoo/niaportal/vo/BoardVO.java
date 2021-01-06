package com.misonamoo.niaportal.vo;

import lombok.Data;

import java.util.Date;

@Data
public class BoardVO {

    private Long boardNo;
    private String boardTitle;
    private String boardContent;
    private Date boardRegistDate;
}
