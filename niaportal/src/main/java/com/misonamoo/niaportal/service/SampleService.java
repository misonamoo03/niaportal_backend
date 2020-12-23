package com.misonamoo.niaportal.service;

import com.misonamoo.niaportal.domain.Sample;

import java.util.List;
import java.util.Map;

public interface SampleService {
    List<Map<String, Sample>> getSampleList();
}
