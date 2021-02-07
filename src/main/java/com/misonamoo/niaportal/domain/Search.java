package com.misonamoo.niaportal.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Search extends BasePaging {
    private String type;
    private String query;

    public Search() {
        type = "";
    }

}
