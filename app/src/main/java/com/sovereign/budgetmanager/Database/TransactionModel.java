package com.sovereign.budgetmanager.Database;

public class TransactionModel {

    private String title;
    private int amount;
    private boolean isCredit;
    private int cat;
    private String date;
    private String time;
    private int transactionMode;

    public TransactionModel() {
    }

    public TransactionModel(String title, int amount, boolean isCredit, int cat, int transactionMode, String date, String time) {
        this.title = title;
        this.amount = amount;
        this.isCredit = isCredit;
        this.cat = cat;
        this.transactionMode = transactionMode;
        this.date = date;
        this.time = time;
    }

    @Override
    public String toString() {
        return "TransactionModel{" +
                "title='" + title + '\'' +
                ", amount=" + amount +
                ", isCredit=" + isCredit +
                ", cat=" + cat +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", transactionMode=" + transactionMode +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isCredit() {
        return isCredit;
    }

    public void setCredit(boolean credit) {
        isCredit = credit;
    }

    public int getCat() {
        return cat;
    }

    public void setCat(int cat) {
        this.cat = cat;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTransactionMode() {
        return transactionMode;
    }

    public void setTransactionMode(int transactionMode) {
        this.transactionMode = transactionMode;
    }
}
