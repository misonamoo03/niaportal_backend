package com.misonamoo.niaportal.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class UserVO {
    private String id;
    private String pw;
    private String pw_re;
    private String name;
    private String phone;
    private String email;
    private Date userRegistDate;
    private Date UserUpdateDate;
}
