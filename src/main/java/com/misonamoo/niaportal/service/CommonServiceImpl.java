package com.misonamoo.niaportal.service;

import com.misonamoo.niaportal.domain.DwReq;
import com.misonamoo.niaportal.mapper.CommonMapper;
import com.misonamoo.niaportal.mapper.DwReqMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    CommonMapper commonMapper;

    @Override
    public List<Map<String, Object>> listCommonCode(Map<String, String> paramMap) {
        return commonMapper.listCommonCode(paramMap);
    }

    @Override
    public Map<String, Object> getCommonCode(String code) {
        return commonMapper.getCommonCode(code);
    }
}
