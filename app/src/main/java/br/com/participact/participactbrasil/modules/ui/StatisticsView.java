package br.com.participact.participactbrasil.modules.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.participact.participactbrasil.modules.support.Statistic;

public class StatisticsView extends View {

    public StatisticsView(Context context) {
        super(context);
    }

    public StatisticsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
//        setup(Arrays.asList(
//                new Statistic("MAR", 20),
//                new Statistic("ABR", 3),
//                new Statistic("MAI", 10),
//                new Statistic("JUN", 70),
//                new Statistic("JUL", 30),
//                new Statistic("AGO", 7)
//        ));
    }

    public StatisticsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /*
    100% - #C01418
90% - #C01418
80% - #C05214
70% - #C05214
60% - #E57638
50% - #FF9F00
40% - #BEDA57
30% - #219653
20% - #219653
10% - #219653
     */

    private String getColor(float percent) {
        if (percent >= 90) {
            return "#C01418";
        } else if (percent >= 70) {
            return "#C05214";
        } else if (percent >= 60) {
            return "#E57638";
        } else if (percent >= 50) {
            return "#FF9F00";
        } else if (percent >= 40) {
            return "#BEDA57";
        } else {
            return "#219653";
        }
    }

    List<Statistic> statistics;
    float max = 0;

    public void setup(List<Statistic> statistics) {
        this.statistics = new ArrayList<>();
        for (Statistic statistic : statistics) {
            if (statistic.getAmount() > max) {
                max = statistic.getAmount();
            }
            this.statistics.add(statistic);
        }
        while (this.statistics.size() > 6) {
            this.statistics.remove(0);
        }
        while (this.statistics.size() < 6) {
            if (this.statistics.size() == 0) {
                this.statistics.add(new Statistic("", 0));
            } else {
                this.statistics.add(0, new Statistic("", 0));
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        float width = canvas.getWidth();
        float height = canvas.getHeight();
        float topSpace = 30;
        float bottomSpace = 40;

        Paint paintTextMonths = new Paint();
        paintTextMonths.setColor(Color.DKGRAY);
        paintTextMonths.setTextAlign(Paint.Align.CENTER);
        paintTextMonths.setTypeface(Typeface.DEFAULT);
        paintTextMonths.setTextSize(30);

        Paint paintTextAmount = new Paint();
        paintTextAmount.setColor(Color.DKGRAY);
        paintTextAmount.setTextAlign(Paint.Align.CENTER);
        paintTextAmount.setTypeface(Typeface.DEFAULT);
        paintTextAmount.setTextSize(20);

        Paint paint = new Paint();

//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(2);
//        paint.setColor(Color.GRAY);
//        canvas.drawRect(0, 0, width, height, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);


        float space = 10;
        float barWidth = ((width - (space * 5)) / 6);
        float cornerRadius = 8;
        float textBottomSpace = 12;

        float x = 0; // x initial
        float w = x + barWidth; // x final
        float h = height - bottomSpace; // y final
        float textOffset = w / 2;
        float monthNameY = height - textBottomSpace;

        float barMaxHeight = h - topSpace;

        for (Statistic statistic : statistics) {
            float percent = statistic.getAmount() * 100 / max;
            float barHeight = barMaxHeight * percent / 100;
            float y = topSpace + (barMaxHeight - barHeight);

            paint.setColor(Color.parseColor(getColor(percent)));

            canvas.drawRoundRect(x, y, w, h, cornerRadius, cornerRadius, paint);
            canvas.drawText(statistic.getMonth(), x + textOffset, monthNameY, paintTextMonths);
            if (statistic.getAmount() > 0) {
                canvas.drawText(String.valueOf(statistic.getAmount()), x + textOffset, y - 6, paintTextAmount);
            }

            x += barWidth + space;
            w = x + barWidth;
        }

    }
}
