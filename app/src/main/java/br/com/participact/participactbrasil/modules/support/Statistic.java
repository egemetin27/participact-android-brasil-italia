package br.com.participact.participactbrasil.modules.support;

public class Statistic {

    String month;

    int amount;

    public Statistic(String month, int amount) {
        this.month = month;
        this.amount = amount;
    }

    public String getMonth() {
        return month;
    }

    public int getAmount() {
        return amount;
    }
}
