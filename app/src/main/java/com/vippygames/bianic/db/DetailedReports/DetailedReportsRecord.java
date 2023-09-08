package com.vippygames.bianic.db.DetailedReports;

public class DetailedReportsRecord {
    private String uuid;
    private String reportsTableUuid;
    private String coin;
    private double targetAllocation;
    private double quantity;
    private double price;
    private double currentUsdValue;
    private double currentAllocation;
    private double targetQuantity;
    private String createdAt;

    public DetailedReportsRecord(String uuid, String reportsTableUuid, String coin,
                                 double targetAllocation, double quantity, double price,
                                 double currentUsdValue, double currentAllocation,
                                 double targetQuantity, String createdAt) {
        this.uuid = uuid;
        this.reportsTableUuid = reportsTableUuid;
        this.coin = coin;
        this.targetAllocation = targetAllocation;
        this.quantity = quantity;
        this.price = price;
        this.currentUsdValue = currentUsdValue;
        this.currentAllocation = currentAllocation;
        this.targetQuantity = targetQuantity;
        this.createdAt = createdAt;
    }

    public DetailedReportsRecord(String uuid, String reportsTableUuid, String coin,
                                 double targetAllocation, double quantity, double price,
                                 double currentUsdValue, double currentAllocation,
                                 double targetQuantity) {
        this.uuid = uuid;
        this.reportsTableUuid = reportsTableUuid;
        this.coin = coin;
        this.targetAllocation = targetAllocation;
        this.quantity = quantity;
        this.price = price;
        this.currentUsdValue = currentUsdValue;
        this.currentAllocation = currentAllocation;
        this.targetQuantity = targetQuantity;
    }

    public String getUuid() {
        return uuid;
    }

    public String getReportsTableUuid() {
        return reportsTableUuid;
    }

    public String getCoin() {
        return coin;
    }

    public double getTargetAllocation() {
        return targetAllocation;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getCurrentUsdValue() {
        return currentUsdValue;
    }

    public double getCurrentAllocation() {
        return currentAllocation;
    }

    public double getTargetQuantity() {
        return targetQuantity;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
