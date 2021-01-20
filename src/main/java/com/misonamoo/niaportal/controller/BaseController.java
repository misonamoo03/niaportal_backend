package com.misonamoo.niaportal.controller;


import com.misonamoo.niaportal.common.ErrorCode;
import com.misonamoo.niaportal.domain.DwReq;
import com.misonamoo.niaportal.service.DwReqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static com.misonamoo.niaportal.common.CommonUtil.*;

public class BaseController {


    public Map<String, Object> returnMap(Map<String, Object> ret){

        if(ret == null){
            ret.put("status", 500);
        }

        //errorCode Message 생성
        String errorMessage = "지정하지 않은 오류";
        try{
            if(ret.get("status") != null) {
                for (ErrorCode error : ErrorCode.values()) {
                    if (error.getCode() == (int) ret.get("status")) {
                        errorMessage = error.getMessage();
                    }
                }
            }
        } catch (Exception e) {
            ret.put("status", 500);
            for (ErrorCode error : ErrorCode.values()) {
                if (error.getCode() == (int) ret.get("status")) {
                    errorMessage = error.getMessage();
                }
            }
        }

        ret.put("message",errorMessage);
        return ret;


    }

}