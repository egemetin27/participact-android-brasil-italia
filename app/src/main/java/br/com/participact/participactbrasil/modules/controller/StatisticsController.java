package br.com.participact.participactbrasil.modules.controller;

import android.content.Context;

import com.bergmannsoft.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import br.com.participact.participactbrasil.modules.network.requests.StatisticsResponse;
import br.com.participact.participactbrasil.modules.support.Statistic;
import br.com.participact.participactbrasil.modules.support.StatisticsCategory;

public class StatisticsController {

    public static List<StatisticsCategory> build(Context context, StatisticsResponse response) {
        List<StatisticsCategory> statistics = new ArrayList<>();
        if (response != null && response.getItems() != null && response.getItems().size() > 0) {

            // Separamos por data

            HashMap<String, List<StatisticsResponse.StatisticItem>> byDate = new HashMap<>();
            for (StatisticsResponse.StatisticItem item : response.getItems()) {
                String key = item.dateYearMonth();
                List<StatisticsResponse.StatisticItem> items = byDate.get(key);
                if (items == null) {
                    items = new ArrayList<>();
                }
                items.add(item);
                byDate.put(key, items);
            }
            List<String> byDateKeys = new ArrayList<>(byDate.keySet());
            Collections.sort(byDateKeys, new Comparator<String>() {
                @Override
                public int compare(String a, String b) {
                    return a.compareTo(b);
                }
            });

            // deixamos apenas os últimos 6 meses
            while (byDateKeys.size() > 6) {
                byDateKeys.remove(0);
            }

            // separamos por categorias
            HashMap<String, List<StatisticsResponse.StatisticItem>> categoryHashMap = new HashMap<>();

            for (String key : byDateKeys) {
                List<StatisticsResponse.StatisticItem> items = byDate.get(key);
                for (StatisticsResponse.StatisticItem item : items) {
                    String categoryKey = item.getCategoryName();
                    if (categoryKey != null) {
                        List<StatisticsResponse.StatisticItem> category = categoryHashMap.get(categoryKey);
                        if (category == null) {
                            category = new ArrayList<>();
                        }
                        category.add(item);
                        categoryHashMap.put(categoryKey, category);
                    }
                }
            }

            // ordenar por nomes
            List<String> categoryNames = new ArrayList<>(categoryHashMap.keySet());
            Collections.sort(categoryNames, new Comparator<String>() {
                @Override
                public int compare(String a, String b) {
                    return a.compareTo(b);
                }
            });


            HashMap<String, StatisticsCategory> categories = new HashMap<>();
            for (String categoryNameKey : categoryNames) {
                StatisticsCategory category = categories.get(categoryNameKey);
                if (category == null) {
                    category = new StatisticsCategory(categoryNameKey);
                }
                // ordenar novamente por meses
                HashMap<String, List<StatisticsResponse.StatisticItem>> itemsMonth = new HashMap<>();
                for (StatisticsResponse.StatisticItem item : categoryHashMap.get(categoryNameKey)) {
                    String yearMonth = item.dateYearMonth();
                    List<StatisticsResponse.StatisticItem> items = itemsMonth.get(yearMonth);
                    if (items == null) {
                        items = new ArrayList<>();
                    }
                    items.add(item);
                    itemsMonth.put(yearMonth, items);
                }
                List<String> yearMonthKeys = new ArrayList<>(itemsMonth.keySet());
                Collections.sort(yearMonthKeys, new Comparator<String>() {
                    @Override
                    public int compare(String a, String b) {
                        return a.compareTo(b);
                    }
                });
                for (String yearMonth : yearMonthKeys) {
                    // 2018-08-18
                    String monthStr = yearMonth.substring(5);
                    int month = Integer.parseInt(monthStr);
                    String monthName = Utils.getMonthShortByNumber(context, month);
                    category.getStatistics().add(new Statistic(monthName, itemsMonth.get(yearMonth).size()));
                }
                categories.put(categoryNameKey, category);
            }

            // ordenar por nomes
            categoryNames = new ArrayList<>(categories.keySet());
            Collections.sort(categoryNames, new Comparator<String>() {
                @Override
                public int compare(String a, String b) {
                    return a.compareTo(b);
                }
            });

            for (String categoryName: categoryNames) {
                statistics.add(categories.get(categoryName));
            }

        }
        return statistics;
    }

}
