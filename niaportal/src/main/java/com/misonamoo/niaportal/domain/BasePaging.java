package com.misonamoo.niaportal.domain;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class BasePaging {

    private int currentPage;
    private int pagePerRow;
    private int startRow;
    public BasePaging() {
        currentPage = 1;
        pagePerRow = 10;

    }

    public int getStartRow() {
        return startRow = (currentPage-1)*pagePerRow;
    }

}
