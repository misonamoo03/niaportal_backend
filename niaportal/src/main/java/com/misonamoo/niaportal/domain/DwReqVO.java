package com.misonamoo.niaportal.domain;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class DwReqVO {
    private long userNo;
    private String email;
    private String userName;
    private String tel;
    private String agency;
    private String companyTypeCode;
    private String companyTypeName;
    private String confirmStateCode;
    private String confirmStateName;
    private String confirmMessage;
    private String reqCode;
    private String reqComment;
    private Date regDate;
    private Date confirmDate;

    public DwReqVO() {

    }


}
