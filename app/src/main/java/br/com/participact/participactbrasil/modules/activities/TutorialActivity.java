package br.com.participact.participactbrasil.modules.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import br.com.participact.participactbrasil.R;

public class TutorialActivity extends BaseActivity {

    ViewPager mViewPager;
    Button buttonSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        buttonSkip = findViewById(R.id.buttonSkip);

        mViewPager = findViewById(R.id.pager);
        TabLayout tabDots = findViewById(R.id.tabDots);
        tabDots.setupWithViewPager(mViewPager);
        mViewPager.setAdapter(new TutorialPagerAdapter());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 4) {
                    buttonSkip.setText("FINALIZAR TUTORIAL");
                } else {
                    buttonSkip.setText("PULAR TUTORIAL");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void skipClick(View view) {
        finish();
    }

    class TutorialPagerAdapter extends PagerAdapter {

        int[] items = new int[] {R.layout.activity_tutorial1, R.layout.activity_tutorial2, R.layout.activity_tutorial3, R.layout.activity_tutorial4, R.layout.activity_tutorial5};

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater inflater = LayoutInflater.from(TutorialActivity.this);
            ViewGroup layout = (ViewGroup) inflater.inflate(items[position], container, false);
            container.addView(layout);

            return layout;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

}
