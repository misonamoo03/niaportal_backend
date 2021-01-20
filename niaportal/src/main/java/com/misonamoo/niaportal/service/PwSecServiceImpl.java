package com.misonamoo.niaportal.service;

import com.misonamoo.niaportal.domain.PwSec;
import com.misonamoo.niaportal.mapper.PwSecMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PwSecServiceImpl implements PwSecService {

    @Autowired
    PwSecMapper pwSecMapper;


    @Override
    public String findCode(int userNo) {
        return pwSecMapper.findCode(userNo);
    }

    @Override
    public void setCode(PwSec pwSec) {
        pwSecMapper.setCode(pwSec);
    }

    @Override
    public void updateCode(PwSec pwSec) {
        pwSecMapper.updateCode(pwSec);
    }

    @Override
    public String getEndTime(int userNo) {
        return pwSecMapper.getEndTime(userNo);
    }
}
