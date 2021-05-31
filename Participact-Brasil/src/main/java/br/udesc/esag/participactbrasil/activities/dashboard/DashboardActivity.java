package br.udesc.esag.participactbrasil.activities.dashboard;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import br.com.bergmannsoft.activity.BActivity;
import br.com.bergmannsoft.util.Utils;
import br.udesc.esag.participactbrasil.MessageType;
import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.activities.profile.ProfileEditFragment;
import br.udesc.esag.participactbrasil.activities.welcome.WelcomeActivity;
import br.udesc.esag.participactbrasil.adapters.SensorsPageAdapter;
import br.udesc.esag.participactbrasil.adapters.SingleFragmentAdapter;
import br.udesc.esag.participactbrasil.adapters.TaskPageAdapter;
import br.udesc.esag.participactbrasil.broadcastreceivers.DailyNotificationBroadcastReceiver;
import br.udesc.esag.participactbrasil.domain.enums.TaskState;
import br.udesc.esag.participactbrasil.domain.persistence.StateUtility;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlat;
import br.udesc.esag.participactbrasil.network.request.GCMRegisterRequest;
import br.udesc.esag.participactbrasil.network.request.GCMRegisterRequestListener;
import br.udesc.esag.participactbrasil.network.request.ParticipactSpringAndroidService;
import br.udesc.esag.participactbrasil.services.LocationService;
import br.udesc.esag.participactbrasil.support.UploadAlarm;
import br.udesc.esag.participactbrasil.support.preferences.UserAccountPreferences;


public class DashboardActivity extends BActivity implements NavigationView.OnNavigationItemSelectedListener {

    //TODO review implementation and remove, used to set action for notifications on the old version
    public static final String GO_TO_TASK_ACTIVE_FRAGMENT = "GO_TO_TASK_ACTIVE_FRAGMENT";
    public static final String GO_TO_PROFILE_FRAGMENT = "GO_TO_PROFILE_FRAGMENT";
    public static final String GO_TO_FRIENDS_FRAGMENT = "GO_TO_FRIENDS_FRAGMENT";
    public static final String GO_TO_APRROVED_TASK_CREATED_FRAGMENT = "GO_TO_APRROVED_TASK_CREATED_FRAGMENT";
    public static final String GO_TO_REFUSED_TASK_CREATED_FRAGMENT = "GO_TO_REFUSED_TASK_CREATED_FRAGMENT";

    private SpiceManager _contentManager = new SpiceManager(ParticipactSpringAndroidService.class);
    private static final Logger logger = LoggerFactory.getLogger(DashboardActivity.class);
    private BroadcastReceiver mBroadcastReceiverGoToActiveTask;

    private DrawerLayout drawer;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private FragmentStatePagerAdapter currentViewPager;

    // region Life Cycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActionBarAndMenu();

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.container);

        // The ugly code below prevent exception IllegalArgumentException: Tab belongs to a different TabLayout.
        TabLayout.Tab uselessTab;
        for (int j = 0; j < 17; j++) {
            uselessTab = tabLayout.newTab();
            Log.d(TAG, "" + uselessTab);
        }

        FloatingActionButton fabMenu = (FloatingActionButton) findViewById(R.id.fabMenu);
        fabMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNavigationDrawer();
            }
        });

        if (Utils.getPreferenceBoolean(this, "notifications", false)) {
            Utils.savePreferenceBoolean(this, "notifications", false);
            setViewPagerNotifications();
        } else {
            setViewPagerCampaigns();
        }
        startServices();
        registerGCMBackground();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!_contentManager.isStarted()) {
            _contentManager.start(this);
        }

        showLoading(false);
        if (mBroadcastReceiverGoToActiveTask == null) {
            mBroadcastReceiverGoToActiveTask = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    setViewPagerCampaigns();
                }
            };
        }
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(mBroadcastReceiverGoToActiveTask, new IntentFilter(GO_TO_TASK_ACTIVE_FRAGMENT));

        if (currentViewPager != null && currentViewPager instanceof CampaignPageAdapter) {
            currentViewPager.notifyDataSetChanged();
        }
    }

    @Override
    public void onStop() {
        if (_contentManager.isStarted()) {
            _contentManager.shouldStop();
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.unregisterReceiver(mBroadcastReceiverGoToActiveTask);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (currentViewPager instanceof SingleFragmentAdapter) {
            Fragment current = ((SingleFragmentAdapter) currentViewPager).getCurrentFragment();
            if (current instanceof ProfileEditFragment) {
                setViewPagerProfile();
            }
        }
    }

    @Override
    protected void onUpdate() {
        List<TaskFlat> taskList = StateUtility.getTaskListByState(this, TaskState.ACCEPTED);
        taskList.addAll(StateUtility.getTaskListByState(this, TaskState.AVAILABLE));
        taskList.addAll(StateUtility.getTaskListByState(this, TaskState.RUNNING));
        taskList.addAll(StateUtility.getTaskListByState(this, TaskState.UNKNOWN));
        taskList.addAll(StateUtility.getTaskListByState(this, TaskState.RUNNING_BUT_NOT_EXEC));
        taskList.addAll(StateUtility.getTaskListByState(this, TaskState.SUSPENDED));
        boolean changed = false;
        for (TaskFlat taskFlat : taskList) {
            if (taskFlat.getTaskStatus().isCompleted() || taskFlat.getTaskStatus().isExpired()) {
                StateUtility.completeTask(this, taskFlat.getTaskStatus());
                changed = true;
            }
        }
        if (changed && currentViewPager != null) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    currentViewPager.notifyDataSetChanged();
                }
            });
        }
    }

    // endregion Life Cycle

    // region Navigation, ActionBar and Menu

    private void openNavigationDrawer() {
        drawer.openDrawer(Gravity.LEFT);
    }

    private void setActionBarAndMenu() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    // endregion

    // region Set View Pager

    private void setViewPagerCampaigns() {
        setActionBarTitle("Campanhas");
        CampaignPageAdapter campaignPagerAdapter = new CampaignPageAdapter(getSupportFragmentManager());
        currentViewPager = campaignPagerAdapter;
        viewPager.invalidate();
        tabLayout.invalidate();
        viewPager.setAdapter(campaignPagerAdapter);
        viewPager.addOnPageChangeListener(campaignsPageListener);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.VISIBLE);
    }

    ViewPager.OnPageChangeListener campaignsPageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            currentViewPager.notifyDataSetChanged();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void setViewPagerSensors() {
        setActionBarTitle(getString(R.string.my_sensors));
        SensorsPageAdapter sensorsPagerAdapter = new SensorsPageAdapter(getSupportFragmentManager(), this);
        currentViewPager = sensorsPagerAdapter;
        viewPager.invalidate();
        viewPager.setAdapter(sensorsPagerAdapter);
        viewPager.removeOnPageChangeListener(campaignsPageListener);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.VISIBLE);
    }

    private void setViewPagerTasks() {
        setActionBarTitle("Minhas tarefas");
        TaskPageAdapter taskPagerAdapter = new TaskPageAdapter(getSupportFragmentManager(), this);
        currentViewPager = taskPagerAdapter;
        viewPager.invalidate();
        viewPager.setAdapter(taskPagerAdapter);
        viewPager.removeOnPageChangeListener(campaignsPageListener);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.VISIBLE);
    }

    private void setViewPagerSettings() {
        setActionBarTitle(getString(R.string.action_settings));
        SingleFragmentAdapter adapter = new SingleFragmentAdapter(
                getSupportFragmentManager(),
                this,
                SingleFragmentAdapter.SingleFragmentType.SETTINGS
        );
        currentViewPager = adapter;
        viewPager.invalidate();
        viewPager.setAdapter(adapter);
        viewPager.removeOnPageChangeListener(campaignsPageListener);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.GONE);
    }

    private void setViewPagerProfile() {
        setActionBarTitle(getString(R.string.profile));
        SingleFragmentAdapter adapter = new SingleFragmentAdapter(
                getSupportFragmentManager(),
                this,
                SingleFragmentAdapter.SingleFragmentType.PROFILE
        );
        currentViewPager = adapter;
        viewPager.invalidate();
        viewPager.setAdapter(adapter);
        viewPager.removeOnPageChangeListener(campaignsPageListener);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.GONE);
    }

    private void setViewPagerProfileEdit() {
        setActionBarTitle(getString(R.string.profile));
        SingleFragmentAdapter adapter = new SingleFragmentAdapter(
                getSupportFragmentManager(),
                this,
                SingleFragmentAdapter.SingleFragmentType.PROFILE_EDIT
        );
        currentViewPager = adapter;
        viewPager.invalidate();
        viewPager.setAdapter(adapter);
        viewPager.removeOnPageChangeListener(campaignsPageListener);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.GONE);
    }

    private void setViewPagerNotifications() {
        setActionBarTitle("Notificações");
        SingleFragmentAdapter adapter = new SingleFragmentAdapter(
                getSupportFragmentManager(),
                this,
                SingleFragmentAdapter.SingleFragmentType.NOTIFICATION
        );
        currentViewPager = adapter;
        viewPager.invalidate();
        viewPager.setAdapter(adapter);
        viewPager.removeOnPageChangeListener(campaignsPageListener);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.GONE);
    }

    private void setViewPagerHelp() {
        setActionBarTitle(getString(R.string.help));
        SingleFragmentAdapter adapter = new SingleFragmentAdapter(
                getSupportFragmentManager(),
                this,
                SingleFragmentAdapter.SingleFragmentType.HELP
        );
        currentViewPager = adapter;
        viewPager.invalidate();
        viewPager.setAdapter(adapter);
        viewPager.removeOnPageChangeListener(campaignsPageListener);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.GONE);
    }

    private void setViewPagerAbout() {
        setActionBarTitle(getString(R.string.about));
        SingleFragmentAdapter adapter = new SingleFragmentAdapter(
                getSupportFragmentManager(),
                this,
                SingleFragmentAdapter.SingleFragmentType.ABOUT
        );
        currentViewPager = adapter;
        viewPager.invalidate();
        viewPager.setAdapter(adapter);
        viewPager.removeOnPageChangeListener(campaignsPageListener);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.GONE);
    }

    // endregion Set View Pager

    // region Commented

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.action_filter:
                //TODO filter tasks/campaigns logic
                return true;
            case R.id.action_order:
                //TODO order tasks/campaigns logic
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    // endregion

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.nav_about:
                setViewPagerAbout();
                break;

            case R.id.nav_campaigns:
                setViewPagerCampaigns();
                break;

            case R.id.nav_help:
                setViewPagerHelp();
                break;

            case R.id.nav_profile:
                setViewPagerProfile();
                break;

            case R.id.nav_settings:
                setViewPagerSettings();
                break;

            //case R.id.nav_share:
            //  break;

            case R.id.nav_tasks:
                setViewPagerTasks();
                break;
            case R.id.nav_sensors:
                setViewPagerSensors();
                break;

            case R.id.nav_notifications:
                setViewPagerNotifications();
                break;

//            case R.id.nav_logout:
//                logout();
//                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    private void logout() {
//        try {
//            StateUtility.clearDataBase(getApplicationContext());
//            UserAccountPreferences.getInstance(this).deleteUserAccount();
//            startActivity(new Intent(this, WelcomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
//            finish();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }


    private ProgressDialog dialog;

    private void showLoading(boolean show) {
        if (show) {
            dialog = ProgressDialog.show(this, null, "Carregando campanhas, por favor aguarde...", true);
        } else {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private void setActionBarTitle(String title) {
        try {
            getSupportActionBar().setTitle(title);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void startServices() {
        Intent i = new Intent(this, LocationService.class);
        i.setAction(LocationService.START);
        startService(i);

        // check if gcm is sync to server or expired
        boolean isSetOnServer = UserAccountPreferences.getInstance(getApplicationContext()).isGCMSetOnServer();
        long expirationTime = UserAccountPreferences.getInstance(getApplicationContext()).getgcmOnServerExpirationTime();

        if (!isSetOnServer || System.currentTimeMillis() > expirationTime) {
            logger.info("Registering gcmid, isSetOnServer={}  expirationTime={}.", isSetOnServer, expirationTime);
            registerGCMBackground();
        }

        // Schedule alarm at 12 or 18
        Calendar current = new GregorianCalendar();
        current.setTimeInMillis(System.currentTimeMillis());

        if (current.get(Calendar.HOUR_OF_DAY) <= 18) {

            Calendar cal = new GregorianCalendar();
            cal.set(Calendar.MINUTE, 10);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            //noinspection ResourceType
            cal.set(current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DAY_OF_MONTH));

            if (current.get(Calendar.HOUR_OF_DAY) <= 12) {
                cal.set(Calendar.HOUR_OF_DAY, 12);
            } else {
                cal.set(Calendar.HOUR_OF_DAY, 18);
            }

            Intent intent = new Intent();
            intent.setAction(DailyNotificationBroadcastReceiver.DAILY_NOTIFICATION_INTENT);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                    DailyNotificationBroadcastReceiver.DAILY_NOTIFICATION_REQUEST_CODE,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarm = (AlarmManager) getApplicationContext()
                    .getSystemService(Context.ALARM_SERVICE);
            alarm.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                    pendingIntent);
            logger.info("Daily notification alarm setted at {}",
                    cal.get(Calendar.HOUR_OF_DAY));

            //Create Receiver used to switch to ActiveTaskFragment

            if (mBroadcastReceiverGoToActiveTask == null) {
                mBroadcastReceiverGoToActiveTask = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        setViewPagerCampaigns();
                    }
                };
            }
            UploadAlarm.getInstance(this).start();
        }
    }

    private void registerGCMBackground() {
        GCMRegisterRequest request = new GCMRegisterRequest(this);
        String lastRequestCacheKey = request.createCacheKey();
        if (!_contentManager.isStarted()) {
            _contentManager.start(this);
        }
        _contentManager.execute(request, lastRequestCacheKey, DurationInMillis.ONE_HOUR, new GCMRegisterRequestListener(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && currentViewPager instanceof SingleFragmentAdapter && ((SingleFragmentAdapter) currentViewPager).getCurrentFragment() instanceof ProfileEditFragment) {
            ProfileEditFragment frag = (ProfileEditFragment) ((SingleFragmentAdapter) currentViewPager).getCurrentFragment();
            frag.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case MessageType.MANDATORY_CAMPAIGN_ACCEPTED:
                Log.d(TAG, "MANDATORY_CAMPAIGN_ACCEPTED");
            case MessageType.GCM_NOTIFICATION_NEW_TASK:
                if (currentViewPager instanceof CampaignPageAdapter) {
                    currentViewPager.notifyDataSetChanged();
                }
                return true;
            case MessageType.EDIT_PROFILE:
                setViewPagerProfileEdit();
                return true;
            case MessageType.EDIT_PROFILE_SAVED:
                setViewPagerProfile();
                return true;
            case MessageType.TASK_UPDATED:
                if (currentViewPager != null) {
                    currentViewPager.notifyDataSetChanged();
                }
                return true;
            default:
                return false;
        }
    }
}
