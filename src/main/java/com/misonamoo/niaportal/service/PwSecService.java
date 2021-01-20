package com.misonamoo.niaportal.service;

import com.misonamoo.niaportal.domain.PwSec;

public interface PwSecService {
    public String findCode(int userNo);

    public void setCode(PwSec pwSec);

    public void updateCode(PwSec pwSec);

    public String getEndTime(int userNo);
}
