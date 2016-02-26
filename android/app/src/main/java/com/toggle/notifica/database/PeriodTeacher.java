package com.toggle.notifica.database;

public class PeriodTeacher extends Model {
    public long period;
    public long teacher;

    public PeriodTeacher() {}

    public PeriodTeacher(long period, long teacher) {
        this.period = period;
        this.teacher = teacher;
    }
}
