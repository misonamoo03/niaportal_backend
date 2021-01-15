package com.misonamoo.niaportal.controller;


import com.misonamoo.niaportal.domain.Board;
import com.misonamoo.niaportal.domain.DwReqVO;
import com.misonamoo.niaportal.domain.User;
import com.misonamoo.niaportal.service.DwReqService;
import com.misonamoo.niaportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static com.misonamoo.niaportal.common.CommonUtil.*;
import static com.misonamoo.niaportal.common.CommonUtil.isNull;

@RestController
@RequestMapping("/DwReq")
public class DwReqController {

    @Autowired
    DwReqService dwReqService;
    @Value("${dwReqCode.dwReq}")
    private String dwReqCodeDwReq;    // 다운로드 승인 요청상태
    @Value("${dwReqCode.dwReqConfirm}")
    private String dwReqCodeDwReqConfirm;    // 다운로드 승인완료
    @Value("${dwReqCode.dwReqFail}")
    private String dwReqCodeDwReqFail;    // 다운로드 승인 반려
    @Value("${dwReqCode.reReq}")
    private String dwReqCodeReReq;    // 다운로드 재요청 가능 상태

    //다운로드 요청 가능 상태 조회
    @GetMapping(value = "/dwState")
    public Map<String, Object> dwState(@ModelAttribute DwReqVO dwReq, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> ret = new HashMap<String,Object>();
        ret.put("code", 200);
        ret.put("message", "다운로드 상태 정상 처리");

        if(isLoginNow(request)) {
            dwReq.setUserNo(Long.parseLong(getCookieValue(request,"userNo")));
            DwReqVO dwReqInfo = dwReqService.getDwReqInfo(dwReq);
            Map<String, Object> resultMap = new HashMap<String,Object>();

            if(dwReqInfo == null){
                resultMap.put("isReqPossible",true);
            }else{
                if(dwReqCodeReReq.equals(dwReqInfo.getConfirmStateCode())){
                    resultMap.put("isReqPossible",true);
                }else{
                    resultMap.put("isReqPossible",false);
                    resultMap.put("confirmStateCode",dwReqInfo.getConfirmStateCode());
                    resultMap.put("confirmStateName",dwReqInfo.getConfirmStateName());
                }
            }


            ret.put("result", resultMap);
        }
        else {
            ret.put("code", "104");
            ret.put("message", "접근권한 없음");
            return ret;
        }
        return ret;
    }

    //승인요청
    @PostMapping(value = "/insertReq")
    public Map<String, Object> insertReq(@RequestBody DwReqVO dwReq, HttpServletRequest request) throws Exception {
        Map<String, Object> ret = new HashMap<String,Object>();
        ret.put("code", 200);
        ret.put("message", "다운로드 요청 정상 처리");

        if (isNull(Long.toString(dwReq.getUserNo())) ||    // 필수 변수값 없음
                isNull(dwReq.getReqCode()) ||
                isNull(dwReq.getReqComment())) {

            ret.put("code", 100);
            ret.put("message", "필수 변수값 없음");
            return ret;
        }
        if(isLoginNow(request) && dwReq.getUserNo() == Long.parseLong(getCookieValue(request,"userNo"))) {

            DwReqVO dwReqInfo = dwReqService.getDwReqInfo(dwReq);
            if(dwReqInfo == null ){
                // 다운로드 승인 요청
                dwReq.setConfirmStateCode(dwReqCodeDwReq);
                dwReqService.insertReq(dwReq);
            }else if(dwReqCodeReReq.equals(dwReqInfo.getConfirmStateCode())){
                // 다운로드 승인 요청
                dwReqInfo.setConfirmStateCode(dwReqCodeDwReq);
                dwReqService.insertReq(dwReqInfo);
            }else if(dwReqCodeDwReq.equals(dwReqInfo.getConfirmStateCode())){
                ret.put("code", 131);
                ret.put("message", "승인요청중");
                return ret;
            }else if(dwReqCodeDwReqConfirm.equals(dwReqInfo.getConfirmStateCode())){
                ret.put("code", 132);
                ret.put("message", "이미 승인완료 상태");
                return ret;
            }else if(dwReqCodeDwReqFail.equals(dwReqInfo.getConfirmStateCode())){
                ret.put("code", 132);
                ret.put("message", "이미 승인반려 상태 ");
                return ret;
            }


        }else {
            ret.put("code", 104);
            ret.put("message", "접근권한 없음");
            return ret;
        }

        return ret;
    }

    //다운로드 요청 정보 상세 조회
    @GetMapping(value = "/dwDetailInfo")
    public Map<String, Object> dwDetailInfo(@ModelAttribute DwReqVO dwReq, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> ret = new HashMap<String,Object>();
        ret.put("code", 200);
        ret.put("message", "다운로드 요청 정보 상세 조회 정상 처리");

        if (isNull(Long.toString(dwReq.getUserNo())) ) {

            ret.put("code", 100);
            ret.put("message", "필수 변수값 없음");
            return ret;
        }

        if(isLoginNow(request)) {
            DwReqVO dwReqInfo =null;
            if(isSuperUser(request)){// 슈퍼유저인경우- 모든 회원정보를 조회 할 수 있다.
                dwReqInfo = dwReqService.getDwReqInfo(dwReq);
            }else {//일반유저의 경우 - 자기자신 정보만 조회 가능하다.
                if (dwReq.getUserNo() == Long.parseLong(getCookieValue(request, "userNo"))) {
                    dwReqInfo = dwReqService.getDwReqInfo(dwReq);
                } else {
                    ret.put("code", "104");
                    ret.put("message", "접근권한 없음");
                    return ret;
                }
            }
            if(dwReqInfo == null){
                ret.put("code", "105");
                ret.put("message", "요청정보 없음");
                return ret;
            }else{
                ret.put("result",dwReqInfo);
            }
        }
        else {
            ret.put("code", "104");
            ret.put("message", "접근권한 없음");
            return ret;
        }
        return ret;
    }

    //다운로드 요청 정보 상세 조회
    @PostMapping(value = "/setConfirm")
    public Map<String, Object> setConfirm(@ModelAttribute DwReqVO dwReq, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> ret = new HashMap<String,Object>();
        ret.put("code", 200);
        ret.put("message", "다운로드 요청 정보 상세 조회 정상 처리");

        if (isNull(Long.toString(dwReq.getUserNo())) ||
                isNull(dwReq.getConfirmStateCode()) ||
                isNull(dwReq.getConfirmMessage())) {

            ret.put("code", 100);
            ret.put("message", "필수 변수값 없음");
            return ret;
        }

        if(isLoginNow(request)) {
            DwReqVO dwReqInfo =null;
            if(isSuperUser(request)){// 슈퍼유저인경우- 모든 회원정보를 조회 할 수 있다.
                DwReqVO getDwReqInfo = dwReqService.getDwReqInfo(dwReq);
                if(getDwReqInfo != null){
                    getDwReqInfo.setConfirmStateCode(dwReq.getConfirmStateCode());
                    getDwReqInfo.setConfirmMessage(dwReq.getConfirmMessage());
                    dwReqService.insertReq(getDwReqInfo);
                }else{
                    ret.put("code", "105");
                    ret.put("message", "요청정보 없음");
                    return ret;
                }

            }else {//일반유저의 경우 - 자기자신 정보만 조회 가능하다.

                ret.put("code", "104");
                ret.put("message", "접근권한 없음");
                return ret;
            }

        }
        else {
            ret.put("code", "104");
            ret.put("message", "접근권한 없음");
            return ret;
        }
        return ret;
    }


}
