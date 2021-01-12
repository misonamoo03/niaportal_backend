package com.misonamoo.niaportal.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class User {
    private int userNo;
    private String email;
    private String password;
    private String userName;
    private String tel;
    private String agency;
    private String companyTypeCode;
    private String dwConfirmYN;
    private Date regDate;
    private Date updDate;
}
