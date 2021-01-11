package com.misonamoo.niaportal.domain;

import lombok.Data;

/**
 * Parameter 용도로 분리
 * @author Yohan
 */
@Data
public class BoardParameter {
    private Long boardSeq;
    private String boardTitle;
    private String boardContent;
}
