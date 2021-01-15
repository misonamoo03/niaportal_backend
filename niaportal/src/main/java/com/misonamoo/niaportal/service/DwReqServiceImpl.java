package com.misonamoo.niaportal.service;

import com.misonamoo.niaportal.domain.DwReqVO;
import com.misonamoo.niaportal.domain.User;
import com.misonamoo.niaportal.mapper.DwReqMapper;
import com.misonamoo.niaportal.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.misonamoo.niaportal.common.CommonUtil.isNull;
import static com.misonamoo.niaportal.common.CommonUtil.setEncryptPass;

@Service
public class DwReqServiceImpl implements DwReqService{

    @Autowired
    DwReqMapper dwReqMapper;


    @Override
    public DwReqVO getDwReqInfo(DwReqVO dwReq) { return dwReqMapper.getDwReqInfo(dwReq); }

    @Override
    public void insertReq(DwReqVO dwReq) {
        dwReqMapper.insertReq(dwReq);
    }
}
