package br.com.participact.participactbrasil.modules.network.requests;

import com.bergmannsoft.rest.Response;
import com.bergmannsoft.util.Utils;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class StatisticsResponse extends Response {

    public class StatisticItem {
        Double latitude;
        Double longitude;
        @SerializedName("report_id")
        Long reportId;
        @SerializedName("date_time")
        String dateWhen;
        @SerializedName("category_name")
        String categoryName;

        public Double getLatitude() {
            return latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public Long getReportId() {
            return reportId;
        }

        public String getDateWhen() {
            return dateWhen;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public String dateYearMonth() {
            // 2018-08-18 00:00:00
            if (dateWhen == null) {
                dateWhen = Utils.dateToString("yyyy-MM-dd HH:mm:ss", new Date());
            }
            return dateWhen.substring(0, 7);
        }

    }

    @SerializedName("item")
    List<StatisticItem> items;

    public List<StatisticItem> getItems() {
        return items;
    }
}
