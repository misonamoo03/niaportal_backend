package com.misonamoo.niaportal.mapper;

import com.misonamoo.niaportal.domain.Sample;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface SampleMapper {
    List<Map<String, Sample>> getSampleList();
}
