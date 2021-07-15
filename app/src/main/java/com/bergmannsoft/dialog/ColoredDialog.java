package com.bergmannsoft.dialog;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.App;

public class ColoredDialog extends CustomDialog {

    protected LayoutInflater inflater;
    protected FragmentActivity activity;

    public ColoredDialog(Context context, String title, int layoutId) {
        super(context, null, App.getInstance().getLayoutInflater().inflate(layoutId, null));
        activity = (FragmentActivity) context;
        inflater = App.getInstance().getLayoutInflater();
        TextView textTitle = view.findViewById(R.id.title);
        if (textTitle != null && title != null) {
            textTitle.setText(title);
        }
    }

    public void hideDefaultTitle() {
        view.findViewById(R.id.title).setVisibility(View.GONE);
    }

    public FragmentActivity getActivity() {
        return activity;
    }

    @SuppressWarnings("deprecation")
    protected void setupColor(int drawableBgId, int colorCustomTitle) {
        view.setBackground(context.getResources().getDrawable(drawableBgId));
        view.findViewById(R.id.title).getBackground().setColorFilter(context.getResources().getColor(colorCustomTitle), PorterDuff.Mode.SRC_ATOP);
    }

    protected View setContent(int layoutContentId) {
        View view = App.getInstance().getLayoutInflater().inflate(layoutContentId, null);
        ((FrameLayout) this.view.findViewById(R.id.content)).addView(view);
        return view;
    }

    protected ViewGroup getContent() {
        return view.findViewById(R.id.content);
    }

}