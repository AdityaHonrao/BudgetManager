package com.sovereign.budgetmanager.Database;

public class LimitModel {
    private int cat;
    private int type;
    private int limit;


    public LimitModel(int limit, int cat, int type) {
        this.cat = cat;
        this.type = type;
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "LimitModel{" +
                "cat=" + cat +
                ", type=" + type +
                ", limit=" + limit +
                '}';
    }

    public int getCat() {
        return cat;
    }

    public void setCat(int cat) {
        this.cat = cat;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

}
