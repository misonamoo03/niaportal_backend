package com.misonamoo.niaportal.domain;

import lombok.Data;

import java.util.Date;

/**
 * Response 용도로 분리
 * @author Yohan
 */
@Data
public class Board {

    private Long boardSeq;
    private String boardTitle;
    private String boardContent;
    private Date boardRegistDate;
}
