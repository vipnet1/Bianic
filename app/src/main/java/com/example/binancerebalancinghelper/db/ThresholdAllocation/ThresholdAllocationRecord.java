package com.example.binancerebalancinghelper.db.ThresholdAllocation;

public class ThresholdAllocationRecord {
    private int id;
    private String createdAt;
    private String symbol;
    private float desiredAllocation;

    public ThresholdAllocationRecord(int id, String createdAt, String symbol, float desiredAllocation) {
        this.id = id;
        this.createdAt = createdAt;
        this.symbol = symbol;
        this.desiredAllocation = desiredAllocation;
    }

    public ThresholdAllocationRecord(String symbol, float desiredAllocation) {
        this.symbol = symbol;
        this.desiredAllocation = desiredAllocation;
    }

    public String getSymbol() {
        return symbol;
    }

    public float getDesiredAllocation() {
        return desiredAllocation;
    }
}
