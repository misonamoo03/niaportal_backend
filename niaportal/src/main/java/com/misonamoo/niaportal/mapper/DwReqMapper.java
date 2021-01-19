package com.misonamoo.niaportal.mapper;


import com.misonamoo.niaportal.domain.DwBase;
import com.misonamoo.niaportal.domain.DwReq;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface DwReqMapper {

    public DwReq getDwReqInfo(DwReq dwReq);

    public void insertReq(DwReq dwReq);

    public List<Map<String, Object>> listDwReqInfo(DwReq dwReq);

    public int getDwReqTotalCnt(DwReq dwReq);

    public void dwInsert(DwBase db);

    public int dupFileNo(DwBase db);
}
