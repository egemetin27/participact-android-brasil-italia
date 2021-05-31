package br.udesc.esag.participactbrasil.activities.campaign;

import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import br.com.bergmannsoft.activity.BActivity;
import br.udesc.esag.participactbrasil.MessageType;
import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlat;
import br.udesc.esag.participactbrasil.views.CustomViewPager;

public class CampaignActivity extends BActivity {

    private TaskFlat task;
    private CustomViewPager viewPager;
    private CampaignActivityPageAdapter campaignInfoPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign);

        this.task = (TaskFlat) getIntent().getSerializableExtra("task");
        if(this.task != null) {
            adjustToolbar();
            loadViewPager();
        }
    }

    private void adjustToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(task.getName());
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void loadViewPager(){
        campaignInfoPageAdapter = new CampaignActivityPageAdapter(getSupportFragmentManager(), task);
        viewPager = (CustomViewPager) findViewById(R.id.container);
        viewPager.setAdapter(campaignInfoPageAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                campaignInfoPageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("task","task");
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.task = (TaskFlat) savedInstanceState.getSerializable("task");
        loadViewPager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_campaign, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_update) {
            campaignInfoPageAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case MessageType.CAMPAIGN_ACCEPTED:
                campaignInfoPageAdapter.notifyDataSetChanged();
                return true;
            default:
                return false;
        }
    }
}
