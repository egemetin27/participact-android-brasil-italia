package br.com.participact.participactbrasil.modules.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bergmannsoft.util.FileCache;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.activities.adapters.CategoriesAdapter;
import br.com.participact.participactbrasil.modules.support.UPCategory;

public class CategoryListItemView extends LinearLayout {

    private BaseAdapter adapter;
    private UPCategory category;

    public CategoryListItemView(Context context) {
        super(context);
    }

    public CategoryListItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CategoryListItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public UPCategory getCategory() {
        return category;
    }

//    public void init(Context context, UPCategory category, BaseAdapter adapter, CategoriesAdapter.OnCategorySelectedListener listener) {
//
////        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
////            @Override
////            public void onGlobalLayout() {
////                getViewTreeObserver().removeOnGlobalLayoutListener(this);
////                for (int i = 0; i < getChildCount(); i++) {
////                    View view = getChildAt(i);
////                    if (view instanceof ImageView) {
////
////                    }
////                }
////            }
////        });
//        init(context, category, adapter, listener, false);
//    }

    public void init(Context context, UPCategory category, final BaseAdapter adapter, final CategoriesAdapter.OnCategorySelectedListener listener, UPCategory selected) {
        this.category = category;
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof ImageView) {
                final ImageView imageView = (ImageView) view;
                if (category != null) {
                    imageView.setImageBitmap(category.icon(new FileCache.OnBitmapDownloadedListener() {
                        @Override
                        public void onDownloaded(Bitmap bmp) {
                            adapter.notifyDataSetChanged();
                        }
                    }));
                } else {
                    imageView.setImageBitmap(null);
                }
            } else if (view instanceof TextView) {
                TextView textView = (TextView) view;
                if (category != null) {
                    textView.setText(category.getName());
                } else {
                    textView.setText("");
                }
            }
        }

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onSelected(getCategory());
                }
            }
        });

        if (selected != null && category != null && (selected.getId().longValue() == category.getId().longValue())) {
            getBackground().setColorFilter(context.getResources().getColor(R.color.colorCategorySelected), PorterDuff.Mode.DST_ATOP);
        } else {
            getBackground().setColorFilter(context.getResources().getColor(R.color.colorCategoryNotSelected), PorterDuff.Mode.DST_ATOP);
        }
    }

}
