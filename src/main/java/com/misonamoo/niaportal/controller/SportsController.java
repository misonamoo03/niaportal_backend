package com.misonamoo.niaportal.controller;

import com.misonamoo.niaportal.service.BoardService;
import com.misonamoo.niaportal.service.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


@RestController
@RequestMapping("/Sports")
public class SportsController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(SportsController.class);

    @Autowired
    BoardService boardService;
    @Autowired
    CommonService commonService;

    /**
     *
     * @param sportsTypeCode
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/list")
    public Map<String, Object> getSportsList(@RequestParam("sportsTypeCode") String sportsTypeCode, HttpServletRequest request) throws Exception {
        Map<String, Object> ret = new HashMap<String,Object>();
        List<Map<String,Object>> resultSportsList = new ArrayList<>();
        Map<String, Object> data = new LinkedHashMap<>();
        ret.put("status", 200);

        //파람 : 스포츠 타입코드 골프 :CD020
        //1. .서브 공통코드 조회
        Map<String,String> paramMap = new HashMap<String,String>();
        paramMap.put("prtCode",sportsTypeCode);
        paramMap.put("codeType", "");
        List<Map<String,Object>> list  = commonService.listCommonCode(paramMap);
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%"+list.toString());
        //2 서브 공통 코드를 루프 돌면서 데이타 조회
        for(Map<String,Object> code : list){
            //데이타 조회
            Map<String,String> sportsMap = new HashMap<>();
            sportsMap.put("sportsTypeCode",sportsTypeCode);
            sportsMap.put("actCode", code.get("code").toString());
            List<Map<String,Object>> sportsList = boardService.getSportsList(sportsMap);
            if(sportsList != null && sportsList.size()>0){
                Map<String,Object> obj = new LinkedHashMap<String,Object>();
                obj.put("actName",code.get("codeName"));
                obj.put("actCode",code.get("code"));
                obj.put("sportslist",sportsList);
                resultSportsList.add(obj);
            }

        }
        data.put("list",resultSportsList);
        ret.put("data",data);
        System.out.println("////////////////////////"+ret.toString());
        return returnMap(ret);
    }
}
