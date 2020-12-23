package com.misonamoo.niaportal.service;

import com.misonamoo.niaportal.domain.Sample;
import com.misonamoo.niaportal.mapper.SampleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SampleServiceImpl implements SampleService{

    @Autowired
    SampleMapper sampleMapper;

    @Override
    public List<Map<String, Sample>> getSampleList() {
        return sampleMapper.getSampleList();
    }
}
