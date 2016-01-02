package com.lipi.notifica.database;

public class PeriodGroup extends Model {
    public long period;
    public long p_group;

    public PeriodGroup() {}

    public PeriodGroup(long period, long group) {
        this.period = period;
        this.p_group = group;
    }
}