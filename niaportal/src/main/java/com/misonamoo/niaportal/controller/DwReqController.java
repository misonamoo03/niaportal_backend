package com.misonamoo.niaportal.controller;


import com.misonamoo.niaportal.domain.DwReq;
import com.misonamoo.niaportal.service.DwReqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.misonamoo.niaportal.common.CommonUtil.*;
import static com.misonamoo.niaportal.common.CommonUtil.isNull;

@RestController
@RequestMapping("/DwReq")
public class DwReqController extends BaseController{

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
    public Map<String, Object> dwState(@ModelAttribute DwReq dwReq, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> ret = new HashMap<String,Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        ret.put("code", 200);
        ret.put("message", "다운로드 상태 정상 처리");

        if(isLoginNow(request)) {
            dwReq.setUserNo(Long.parseLong(getCookieValue(request,"userNo")));
            DwReq dwReqInfo = dwReqService.getDwReqInfo(dwReq);

            if(dwReqInfo == null){
                data.put("isReqPossible",true);
            }else{
                if(dwReqCodeReReq.equals(dwReqInfo.getConfirmStateCode())){
                    data.put("isReqPossible",true);
                }else{
                    data.put("isReqPossible",false);
                    data.put("confirmStateCode",dwReqInfo.getConfirmStateCode());
                    data.put("confirmStateName",dwReqInfo.getConfirmStateName());
                }
            }
            ret.put("data", data);
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
    public Map<String, Object> insertReq(@RequestBody DwReq dwReq, HttpServletRequest request) throws Exception {
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

            DwReq dwReqInfo = dwReqService.getDwReqInfo(dwReq);
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
                ret.put("code", 133);
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
    public Map<String, Object> dwDetailInfo(@ModelAttribute DwReq dwReq, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> ret = new HashMap<String,Object>();
        ret.put("code", 200);
        ret.put("message", "다운로드 요청 정보 상세 조회 정상 처리");

        if (isNull(Long.toString(dwReq.getUserNo())) ) {

            ret.put("code", 100);
            ret.put("message", "필수 변수값 없음");
            return ret;
        }

        if(isLoginNow(request)) {
            DwReq dwReqInfo =null;
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
                return returnMap(ret);
            }else{
                ret.put("data",dwReqInfo);
            }
        }
        else {
            ret.put("code", "104");
            ret.put("message", "접근권한 없음");
            return ret;
        }
        return returnMap(ret);
    }

    //승인요청
    @PostMapping(value = "/setConfirm")
    public Map<String, Object> setConfirm(@ModelAttribute DwReq dwReq, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> ret = new HashMap<String,Object>();
        ret.put("code", 200);
        ret.put("message", "다운로드 요청 정보 상세 조회 정상 처리");

        if (isNull(Long.toString(dwReq.getUserNo())) ||
                isNull(dwReq.getConfirmStateCode()) ||
                isNull(dwReq.getConfirmMessage())) {

            ret.put("code", 100);
            ret.put("message", "필수 변수값 없음");
            return returnMap(ret);
        }

        if(isLoginNow(request)) {
            DwReq dwReqInfo =null;
            if(isSuperUser(request)){// 슈퍼유저인경우- 모든 회원정보를 조회 할 수 있다.
                DwReq getDwReqInfo = dwReqService.getDwReqInfo(dwReq);
                if(getDwReqInfo != null){
                    getDwReqInfo.setConfirmStateCode(dwReq.getConfirmStateCode());
                    getDwReqInfo.setConfirmMessage(dwReq.getConfirmMessage());
                    dwReqService.insertReq(getDwReqInfo);
                }else{
                    ret.put("code", "105");
                    ret.put("message", "요청정보 없음");
                    return returnMap(ret);
                }

            }else {//일반유저의 경우 - 자기자신 정보만 조회 가능하다.

                ret.put("code", "104");
                ret.put("message", "접근권한 없음");
                return returnMap(ret);
            }

        }
        else {
            ret.put("code", "104");
            ret.put("message", "접근권한 없음");
            return returnMap(ret);
        }
        return returnMap(ret);
    }


    //다운로드 요청 회원 정보 목록 조회
    @GetMapping(value = "/listDwReqUser")
    public Map<String, Object> listDwReqUser(@ModelAttribute DwReq dwReq, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> ret = new HashMap<String,Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        ret.put("code", 200);
        ret.put("message", "다운로드 요청 정보 상세 조회 정상 처리");


        if(isLoginNow(request)) {
            DwReq dwReqInfo =null;
            if(isSuperUser(request)){// 슈퍼유저인경우- 모든 회원정보를 조회 할 수 있다.
                Map<String, Object> listDwReqInfo = dwReqService.listDwReqInfoPage(dwReq);
                data.put("currentPage",dwReq.getCurrentpage());
                data.put("pagePerRow", dwReq.getPagePerRow());
                data.put("totalCnt",listDwReqInfo.get("totalCnt"));
                data.put("list",listDwReqInfo.get("list"));
                ret.put("data", data);

            }else {//일반유저의 경우 - 자기자신 정보만 조회 가능하다.

                ret.put("code", "104");
                ret.put("message", "접근권한 없음");
                return returnMap(ret);
            }

        }
        else {
            ret.put("code", "104");
            ret.put("message", "접근권한 없음");
            return returnMap(ret);
        }
        return returnMap(ret);
    }


}
