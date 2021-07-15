package br.com.participact.participactbrasil.modules.activities.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.App;
import br.com.participact.participactbrasil.modules.support.UPCategory;
import br.com.participact.participactbrasil.modules.ui.CategoryListItemView;

public class CategoriesAdapter extends BaseAdapter {

    private final OnCategorySelectedListener listener;
    private final Context context;

    public interface OnCategorySelectedListener {
        void onSelected(UPCategory category);
    }

    private List<List<UPCategory>> categories;

    private UPCategory selected;

    public CategoriesAdapter(Context context, List<List<UPCategory>> categories, OnCategorySelectedListener listener) {
        this.context = context;
        this.categories = categories;
        this.listener = listener;
    }

    public void setCategories(List<List<UPCategory>> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    public void setSelected(UPCategory selected) {
        this.selected = selected;
    }

    @Override
    public int getCount() {
        if (categories != null) {
            return categories.size();
        }
        return 0;
    }

    @Override
    public List<UPCategory> getItem(int i) {
        return categories.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = App.getInstance().getLayoutInflater().inflate(R.layout.list_item_urban_problem_categories, null);

        List<UPCategory> items = getItem(i);

        CategoryListItemView b1 = view.findViewById(R.id.button1);
        CategoryListItemView b2 = view.findViewById(R.id.button2);
        CategoryListItemView b3 = view.findViewById(R.id.button3);

        b1.init(context, items.size() > 0 ? items.get(0) : null, this, listener, selected);
        b2.init(context, items.size() > 1 ? items.get(1) : null, this, listener, selected);
        b3.init(context, items.size() > 2 ? items.get(2) : null, this, listener, selected);

        return view;
    }

}