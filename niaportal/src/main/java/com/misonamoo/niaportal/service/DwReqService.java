package com.misonamoo.niaportal.service;

import com.misonamoo.niaportal.domain.DwBase;
import com.misonamoo.niaportal.domain.DwReq;

import java.util.List;
import java.util.Map;


public interface DwReqService {


    public void dwInsert(DwBase db);

    public DwReq getDwReqInfo(DwReq dwReq);

    public void insertReq(DwReq dwReq);

    public Map<String, Object> listDwReqInfoPage(DwReq dwReq);

    public int dupFileNo(DwBase db);
}
