package br.udesc.esag.participactbrasil.activities.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.viewpagerindicator.CirclePageIndicator;

import br.com.bergmannsoft.util.SensorUtils;
import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.activities.login.LoginActivity;
import br.udesc.esag.participactbrasil.activities.dashboard.DashboardActivity;
import br.udesc.esag.participactbrasil.domain.local.UserAccount;
import br.udesc.esag.participactbrasil.support.preferences.UserAccountPreferences;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = WelcomeActivity.class.getSimpleName();
    private Button btSkip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if (UserAccountPreferences.getInstance(this).isUserAccountValid()) {
            UserAccount userAccount = UserAccountPreferences.getInstance(this).getUserAccount();
            Log.i(TAG, "Logged as " + userAccount.getUsername() + " - " + userAccount.getPassword());
            Crashlytics.setUserIdentifier(userAccount.getUsername());
            Crashlytics.setUserName(userAccount.getName());
        }

        SensorUtils.debugAvailableSensorsList(this);

        btSkip = (Button) findViewById(R.id.btSkip);
        btSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
            }
        });

        checkUserAccount();
    }

    private void checkUserAccount() {
        if (UserAccountPreferences.getInstance(this).isUserAccountValid()) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        }else{
            loadViewPager();
        }
    }

    private void loadViewPager() {
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 2){
                    btSkip.setText("Continuar");
                }else{
                    btSkip.setText("Pular apresentação");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        CirclePageIndicator pageIndicator = (CirclePageIndicator) findViewById(R.id.pageIndicator);
        pageIndicator.setViewPager(viewPager);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return WelcomeFragment1.newInstance(null,null);
                case 1:
                    return WelcomeFragment2.newInstance(null,null);
                case 2:
                    return WelcomeFragment3.newInstance(null,null);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
