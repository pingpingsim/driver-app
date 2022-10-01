package com.pingu.driverapp.data.model;

import com.google.gson.annotations.SerializedName;

public class OrderSummary {
    @SerializedName("today")
    private OrderTotal orderTotalToday;
    @SerializedName("history")
    private OrderTotal orderTotalHistory;

    public OrderTotal getOrderTotalToday() {
        return orderTotalToday;
    }

    public void setOrderTotalToday(OrderTotal orderTotalToday) {
        this.orderTotalToday = orderTotalToday;
    }

    public OrderTotal getOrderTotalHistory() {
        return orderTotalHistory;
    }

    public void setOrderTotalHistory(OrderTotal orderTotalHistory) {
        this.orderTotalHistory = orderTotalHistory;
    }
}
