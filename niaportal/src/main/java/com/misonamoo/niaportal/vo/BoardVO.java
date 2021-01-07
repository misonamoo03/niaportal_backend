package com.misonamoo.niaportal.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
public class BoardVO {

    private Long boardNo;
    private String boardTitle;
    private String boardContent;
    private Date boardRegistDate;
}
