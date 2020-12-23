package com.misonamoo.niaportal.controller;

import com.misonamoo.niaportal.domain.Sample;
import com.misonamoo.niaportal.service.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class SampleController {

    @Autowired
    SampleService sampleService;

    @GetMapping("/sample")
    public List<Map<String, Sample>> getSampleList() {
        return sampleService.getSampleList();
    }

}
