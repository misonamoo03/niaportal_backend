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
    private String userGbCode;
    private String password;
    private String newPassword;
    private String userName;
    private String tel;
    private String agency;
    private String companyTypeCode;
    private String dwConfirmYn;
    private String delYn;
    private Date regDate;
    private Date updDate;
    private String updStyle;

    public User() {
        userGbCode = "CD002001";
        dwConfirmYn = "N";
        delYn = "N";
    }


}
