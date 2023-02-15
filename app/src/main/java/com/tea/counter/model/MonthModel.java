package com.tea.counter.model;

import java.util.ArrayList;

public class MonthModel {
    private String monthName;
    private Boolean isClick = false;

    public MonthModel(String monthName) {
        this.monthName = monthName;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public Boolean getClick() {
        return isClick;
    }

    public void setClick(Boolean click) {
        isClick = click;
    }
}
