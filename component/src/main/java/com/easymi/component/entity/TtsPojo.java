package com.easymi.component.entity;

public class TtsPojo {
    private long id;
    private int type;
    private long time;

    public long getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public long getTime() {
        return time;
    }

    public TtsPojo(long id, int type, long time) {
        this.id = id;
        this.type = type;
        this.time = time;
    }


}
