package com.toggle.notifica.database;

public class ClassAdmin extends Model {
    public long p_class;
    public long user;

    public ClassAdmin() {}

    public ClassAdmin(long p_class, long user) {
        this.p_class = p_class;
        this.user = user;
    }
}
