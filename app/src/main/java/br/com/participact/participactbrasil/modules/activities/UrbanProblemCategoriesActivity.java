package br.com.participact.participactbrasil.modules.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.bergmannsoft.dialog.AlertDialogUtils;

import java.util.List;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.App;
import br.com.participact.participactbrasil.modules.activities.adapters.CategoriesAdapter;
import br.com.participact.participactbrasil.modules.network.SessionManager;
import br.com.participact.participactbrasil.modules.network.requests.UPCategoriesResponse;
import br.com.participact.participactbrasil.modules.support.UPCategory;

public class UrbanProblemCategoriesActivity extends BaseActivity {

    private ListView listView;
    CategoriesAdapter adapter;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urban_problem_categories);

        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);

        listView = findViewById(R.id.listView);
        List<List<UPCategory>> ccc = App.getInstance().getCategoriesForList();
        adapter = new CategoriesAdapter(this, ccc, new CategoriesAdapter.OnCategorySelectedListener() {
            @Override
            public void onSelected(UPCategory category) {
                Intent i = new Intent(UrbanProblemCategoriesActivity.this, UrbanProblemSubcategoriesActivity.class);
                i.putExtra("category", category);
                i.putExtra("latitude", latitude);
                i.putExtra("longitude", longitude);
                startActivity(i);
            }
        });
        listView.setAdapter(adapter);
        SessionManager.getInstance().categories(new SessionManager.RequestCallback<UPCategoriesResponse>() {
            @Override
            public void onResponse(UPCategoriesResponse response) {
                if (response.isSuccess()) {
                    App.getInstance().setCategories(response.getCategories());
                    adapter.setCategories(App.getInstance().getCategoriesForList());
                } else {
                    AlertDialogUtils.showError(UrbanProblemCategoriesActivity.this, response.getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                AlertDialogUtils.showError(UrbanProblemCategoriesActivity.this, t.getLocalizedMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (App.getInstance().isCloseCategories() || App.getInstance().hasReportSent()) {
            App.getInstance().setCloseCategories(false);
            finish();
        }
    }

    public void closeClick(View view) {
        finish();
    }

}
