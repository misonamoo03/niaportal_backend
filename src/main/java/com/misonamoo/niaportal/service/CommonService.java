package com.misonamoo.niaportal.service;

import com.misonamoo.niaportal.domain.PwSec;

import java.util.List;
import java.util.Map;

public interface CommonService {

    List<Map<String, Object>> listCommonCode(Map<String, String> paramMap);

    Map<String, Object> getCommonCode(String code);
}
