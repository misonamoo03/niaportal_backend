package com.misonamoo.niaportal.controller;


import com.misonamoo.niaportal.domain.DwReq;
import com.misonamoo.niaportal.service.CommonService;
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

@RestController
@RequestMapping("/Common")
public class CommonController extends BaseController{

    @Autowired
    CommonService commonService;


    //다운로드 요청 회원 정보 목록 조회
    @GetMapping(value = "/listCommonCode")
    public Map<String, Object> listDwReqUser(@RequestParam(value="prtCode", required = false, defaultValue="000") String prtCode,
                                             @RequestParam(value="codeType", required = false) String codeType,
                                             HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> ret = new HashMap<String,Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        ret.put("status", 200);
        ret.put("message", "다운로드 요청 정보 상세 조회 정상 처리");

        //commonCode 조회
        Map<String,String> paramMap = new HashMap<String,String>();
        paramMap.put("prtCode",prtCode);
        paramMap.put("codeType", codeType);
        List<Map<String,Object>> list  = commonService.listCommonCode(paramMap);
        if(list != null){
            data.put("list",list);
            ret.put("data", data);
        }else{
            ret.put("status", "105");
            ret.put("message", "요청정보 없음");
            return returnMap(ret);
        }

        return returnMap(ret);
    }


    //다운로드 요청 회원 정보 목록 조회
    @GetMapping(value = "/getCommonCode")
    public Map<String, Object> getCommonCode(@RequestParam(value="code", required = true) String code,
                                             HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> ret = new HashMap<String,Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        ret.put("status", 200);
        ret.put("message", "다운로드 요청 정보 상세 조회 정상 처리");

        //commonCode 조회
        Map<String,Object> info  = commonService.getCommonCode(code);
        if(info != null){
            data.put("info",info);
            ret.put("data", data);
        }else{
            ret.put("status", "105");
            ret.put("message", "요청정보 없음");
            return returnMap(ret);
        }

        return returnMap(ret);
    }


}
