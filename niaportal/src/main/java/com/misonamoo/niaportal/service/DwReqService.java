package com.misonamoo.niaportal.service;

import com.misonamoo.niaportal.domain.DwReqVO;
import com.misonamoo.niaportal.domain.User;


public interface DwReqService {


   public DwReqVO getDwReqInfo(DwReqVO dwReq);

   public void insertReq(DwReqVO dwReq);
}
