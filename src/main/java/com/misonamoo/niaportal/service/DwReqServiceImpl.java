package com.misonamoo.niaportal.service;

import com.misonamoo.niaportal.domain.DwBase;
import com.misonamoo.niaportal.domain.DwReq;
import com.misonamoo.niaportal.mapper.DwReqMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DwReqServiceImpl implements DwReqService {

    @Autowired
    DwReqMapper dwReqMapper;


    @Override
    public void dwInsert(DwBase db) {
        dwReqMapper.dwInsert(db);
    }

    @Override
    public DwReq getDwReqInfo(DwReq dwReq) {
        return dwReqMapper.getDwReqInfo(dwReq);
    }

    @Override
    public void insertReq(DwReq dwReq) {
        dwReqMapper.insertReq(dwReq);
    }

    @Override
    public Map<String, Object> listDwReqInfoPage(DwReq dwReq) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> list = dwReqMapper.listDwReqInfo(dwReq);
        int totalCnt = dwReqMapper.getDwReqTotalCnt(dwReq);
        result.put("totalCnt", totalCnt);
        result.put("list", list);

        return result;

    }

    @Override
    public int dupFileNo(DwBase db) {
        return dwReqMapper.dupFileNo(db);
    }

    @Override
    public Map<String, Object> dwList(DwBase dwBase) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> list = dwReqMapper.dwList(dwBase);
        int totalCnt = dwReqMapper.getDwBaseTotalCnt(dwBase);
        result.put("totalCnt", totalCnt);
        result.put("list", list);

        return result;
    }
}
