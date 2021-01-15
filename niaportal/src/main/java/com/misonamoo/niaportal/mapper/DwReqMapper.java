package com.misonamoo.niaportal.mapper;


import com.misonamoo.niaportal.domain.DwReqVO;
import com.misonamoo.niaportal.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface DwReqMapper {

    public DwReqVO getDwReqInfo(DwReqVO dwReq);

    public void insertReq(DwReqVO dwReq);
}
