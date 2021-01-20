package com.misonamoo.niaportal.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class PwSec {
    private int userNo;
    private String secCode;
    private Date startDt;
    private Date endDt;
}
