package br.com.participact.participactbrasil.modules.dialog;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;

import com.bergmannsoft.dialog.ColoredDialog;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.support.UserSettings;

public class FilterDialog extends ColoredDialog implements View.OnClickListener {

    public interface OnFilterListener {
        void onSelected(int days);
    }

    OnFilterListener listener;

    public FilterDialog(Context context, OnFilterListener listener) {
        super(context, null, R.layout.dialog_filter);
        this.listener = listener;
        view.findViewById(R.id.buttonMine).setOnClickListener(this);
        view.findViewById(R.id.buttonAWeek).setOnClickListener(this);
        view.findViewById(R.id.buttonAMonth).setOnClickListener(this);
        view.findViewById(R.id.buttonTwoMonths).setOnClickListener(this);
        view.findViewById(R.id.buttonThreeMonths).setOnClickListener(this);
        view.findViewById(R.id.buttonAll).setOnClickListener(this);

        Button mine = view.findViewById(R.id.buttonMine);
        mine.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#AAAAAA")));
        Button aWeek = view.findViewById(R.id.buttonAWeek);
        aWeek.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#AAAAAA")));
        Button aMonth = view.findViewById(R.id.buttonAMonth);
        aMonth.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#AAAAAA")));
        Button twoMonths = view.findViewById(R.id.buttonTwoMonths);
        twoMonths.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#AAAAAA")));
        Button threeMonths = view.findViewById(R.id.buttonThreeMonths);
        threeMonths.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#AAAAAA")));
        Button all = view.findViewById(R.id.buttonAll);
        all.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#AAAAAA")));

        int days = UserSettings.getInstance().getUrbanProblemsRangeDays();
        switch (days) {
            case 0:
                mine.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4EC500")));
                break;
            case 7:
                aWeek.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4EC500")));
                break;
            case 30:
                aMonth.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4EC500")));
                break;
            case 60:
                twoMonths.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4EC500")));
                break;
            case 90:
                threeMonths.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4EC500")));
                break;
            case 730:
                all.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4EC500")));
                break;
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonMine:
                listener.onSelected(0);
                break;
            case R.id.buttonAWeek:
                listener.onSelected(7);
                break;
            case R.id.buttonAMonth:
                listener.onSelected(30);
                break;
            case R.id.buttonTwoMonths:
                listener.onSelected(60);
                break;
            case R.id.buttonThreeMonths:
                listener.onSelected(90);
                break;
            case R.id.buttonAll:
                listener.onSelected(730);
                break;
        }
        dismiss();
    }
}
