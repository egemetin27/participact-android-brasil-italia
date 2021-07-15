package br.com.participact.participactbrasil.modules.support;

import java.util.ArrayList;
import java.util.List;

public class StatisticsCategory {

    String name;
    List<Statistic> statistics = new ArrayList<>();

    public StatisticsCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Statistic> getStatistics() {
        return statistics;
    }

    public void setStatistics(List<Statistic> statistics) {
        this.statistics = statistics;
    }
}