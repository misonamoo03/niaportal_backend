package com.misonamoo.niaportal.mapper;


import com.misonamoo.niaportal.domain.PwSec;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface CommonMapper {

    List<Map<String, Object>> listCommonCode(Map<String, String> paramMap);

    Map<String, Object> getCommonCode(String code);
}
