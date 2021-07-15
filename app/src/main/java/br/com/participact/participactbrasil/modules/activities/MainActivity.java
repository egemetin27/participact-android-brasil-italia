package br.com.participact.participactbrasil.modules.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bergmannsoft.dialog.AlertDialogUtils;
import com.bergmannsoft.location.LocationHelpers;
import com.bergmannsoft.rest.Response;
import com.bergmannsoft.social.Facebook;
import com.bergmannsoft.social.Google;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.App;
import br.com.participact.participactbrasil.modules.activities.fragments.CampaignsFragment;
import br.com.participact.participactbrasil.modules.activities.fragments.MapFragment;
import br.com.participact.participactbrasil.modules.activities.fragments.PABaseFragment;
import br.com.participact.participactbrasil.modules.activities.fragments.StatisticsFragment;
import br.com.participact.participactbrasil.modules.db.Campaign;
import br.com.participact.participactbrasil.modules.db.CampaignWrapper;
import br.com.participact.participactbrasil.modules.db.PANotification;
import br.com.participact.participactbrasil.modules.db.PANotificationDaoImpl;
import br.com.participact.participactbrasil.modules.dialog.NotificationDialog;
import br.com.participact.participactbrasil.modules.network.SessionManager;
import br.com.participact.participactbrasil.modules.network.requests.NotificationsResponse;
import br.com.participact.participactbrasil.modules.support.Logger;
import br.com.participact.participactbrasil.modules.support.MessageType;
import br.com.participact.participactbrasil.modules.support.UserSettings;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final Logger logger = Logger.getLogger(MainActivity.class);

    public static final int REQUEST_CODE_TERMS_CONDITION = 100;

    private PABaseFragment currentFragment;
    private CampaignsFragment campaignsFragment = new CampaignsFragment();
    private MapFragment mapFragment = new MapFragment();
    private StatisticsFragment statisticsFragment = new StatisticsFragment();

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        menu = navigationView.getMenu();

        /*

        getSupportFragmentManager().beginTransaction().add(R.id.content, profileFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.content, profileEditFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.content, reportsFragment).commit();
        getSupportFragmentManager().beginTransaction().hide(profileFragment).commit();
        getSupportFragmentManager().beginTransaction().hide(profileEditFragment).commit();
        getSupportFragmentManager().beginTransaction().hide(reportsFragment).commit();

        mapFragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.content, mapFragment).commit();
        currentFragment = mapFragment;

         */

        getSupportFragmentManager().beginTransaction().add(R.id.content, statisticsFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.content, mapFragment).commit();
        getSupportFragmentManager().beginTransaction().hide(statisticsFragment).commit();
        getSupportFragmentManager().beginTransaction().hide(mapFragment).commit();

        getSupportFragmentManager().beginTransaction().add(R.id.content, campaignsFragment).commit();
        currentFragment = campaignsFragment;
        campaignsFragment.setCallback(new CampaignsFragment.CampaignCallback() {
            @Override
            public void onCampaignSelected(Campaign campaign) {
                if (CampaignWrapper.wrap(campaign).isMap()) {
                    tabMapClick(null);
                }
            }
        });

        logger.info("App opened with location " + (LocationHelpers.isLocationEnabled(this) ? "enabled" : "disabled"));

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentFragment != null) {
            currentFragment.onShow();
            uiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    application.setBaseFragment(currentFragment);
                }
            }, 2000);
        }
        Log.i(TAG, "Username: " + UserSettings.getInstance().getUsername());
        Log.i(TAG, "RegID: " + UserSettings.getInstance().getRegid());
        if (UserSettings.getInstance().getSendRegId()) {
            UserSettings.getInstance().setSendRegid(false);
            SessionManager.getInstance().sendRegId(UserSettings.getInstance().getRegid(), new SessionManager.RequestCallback<Response>() {
                @Override
                public void onResponse(Response response) {
                    if (response == null || !response.isSuccess()) {
                        UserSettings.getInstance().setSendRegid(true);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    UserSettings.getInstance().setSendRegid(true);
                }
            });
        }
        updateBadge();

        if (!UserSettings.getInstance().getSkipTutorial()) {
            UserSettings.getInstance().setSkipTutorial(true);
            present(TutorialActivity.class);
        }

        SessionManager.getInstance().updateUrbanProblemsGPSSettings();

        try {
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void updateData() {
        updateCampaigns();
        SessionManager.getInstance().notifications(new SessionManager.RequestCallback<NotificationsResponse>() {
            @Override
            public void onResponse(NotificationsResponse response) {
                if (response != null && response.isSuccess()) {
                    if (response.getNotifications() != null) {
                        PANotificationDaoImpl dao = new PANotificationDaoImpl();
                        for (PANotification notification : response.getNotifications()) {
                            dao.save(notification);
                        }
                        if (response.getNotifications().size() > 0) {
                            // show badge
                            showNotification(response.getNotifications().get(0));
                            updateBadge();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, null, t);
            }
        });
    }

    NotificationDialog mNotificationDialog;

    private void showNotification(PANotification notification) {
        if (mNotificationDialog != null) {
            mNotificationDialog.dismiss();
        }
        mNotificationDialog = new NotificationDialog(this, notification, new NotificationDialog.OnCallBack() {
            @Override
            public void onShowAllNotifications() {
                present(NotificationsActivity.class);
            }
        });
        mNotificationDialog.show();
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case MessageType.NOTIFICATION:
                Log.i(TAG, "Notification received.");
                updateData();
                return true;
            case MessageType.SHOW_PROFILE:
                if (UserSettings.getInstance().getHasProfile()) {
                    present(ProfileEditActivity.class);
                } else {
                    present(ProfileCreateActivity.class);
                }
                return true;
        }
        return super.handleMessage(message);
    }

    @Override
    protected void updateCampaigns() {
        if (currentFragment == campaignsFragment) {
            campaignsFragment.getCampaigns();
        }
    }

    @Override
    protected void updateBadge() {
        TextView badge = findViewById(R.id.text_notifications_badge);
        badge.setVisibility(View.INVISIBLE);
        MenuItem notifications = menu.findItem(R.id.nav_notifications);
        notifications.setTitle("NOTIFICAÇÕES");
        int count = new PANotificationDaoImpl().getUnreadCount();
        if (count > 0) {
            badge.setText(String.valueOf(count));
            badge.setVisibility(View.VISIBLE);
            notifications.setTitle("NOTIFICAÇÕES (" + count + ")");
        }
    }

    private void showFragment(PABaseFragment fragment) {
        if (fragment != currentFragment) {
            getSupportFragmentManager().beginTransaction().hide(currentFragment).commit();
            currentFragment.onHide();
            getSupportFragmentManager().beginTransaction().show(fragment).commit();
            currentFragment = fragment;
            currentFragment.onShow();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void updateProfileData() {
        super.updateProfileData();
        if (currentFragment != null) {
            currentFragment.updateHeader();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_about:
                present(AboutActivity.class);
                break;
            case R.id.nav_config:
                present(SettingsActivity.class);
                break;
            case R.id.nav_feedback:
                present(FeedbackActivity.class);
                break;
            case R.id.nav_logout:
                AlertDialogUtils.createDialog(this, "Alerta", "Tem certeza que deseja deslogar?", "Sim", "Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        UserSettings.getInstance().clear();
                        App.getInstance().getFileCache().remove("profile_image");
                        Bitmap bmp = App.getInstance().getFileCache().getBitmap("profile_image");
                        String name = UserSettings.getInstance().getName();
                        Log.d(TAG, "Logout... name: " + name);
                        Log.d(TAG, "Logout... picture: " + bmp);
                        Toast.makeText(MainActivity.this, "Agora você está anônimo. Nem todas as funcionalidades estarão disponíveis.", Toast.LENGTH_LONG).show();
                        uiHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                updateProfileData();
                            }
                        }, 1000);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
                break;
            case R.id.nav_notifications:
                present(NotificationsActivity.class);
                break;
            case R.id.nav_profile:
                if (UserSettings.getInstance().getHasProfile()) {
                    present(ProfileEditActivity.class);
                } else {
                    present(ProfileCreateActivity.class);
                }
                break;
            case R.id.nav_qr_code:
                present(TokenInsertActivity.class);
                break;
            case R.id.nav_tutorial:
                present(TutorialActivity.class);
                break;
        }

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    public void menuClick(View view) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.END);
    }

    public void tabCampaignsClick(View view) {
        unselectTabs();
        findViewById(R.id.tabCampaignsSelection).setBackgroundColor(getResources().getColor(R.color.colorTabSelected));
        ImageView i = findViewById(R.id.tabCampaignsIcon);
        i.setImageResource(R.mipmap.ic_tab_campaign_grey);
        showFragment(campaignsFragment);
        TextView t = findViewById(R.id.tabCampaignsText);
        t.setTextColor(getResources().getColor(R.color.colorTabSelectedText));
    }

    public void tabMapClick(View view) {
        unselectTabs();
        findViewById(R.id.tabMapSelection).setBackgroundColor(getResources().getColor(R.color.colorTabSelected));
        ImageView i = findViewById(R.id.tabMapIcon);
        i.setImageResource(R.mipmap.ic_tab_map_grey);
        showFragment(mapFragment);
        TextView t = findViewById(R.id.tabMapText);
        t.setTextColor(getResources().getColor(R.color.colorTabSelectedText));
    }

    public void tabStatisticsClick(View view) {
        unselectTabs();
        findViewById(R.id.tabStatisticsSelection).setBackgroundColor(getResources().getColor(R.color.colorTabSelected));
        ImageView i = findViewById(R.id.tabStatisticsIcon);
        i.setImageResource(R.mipmap.ic_tab_statistics_grey);
        showFragment(statisticsFragment);
        TextView t = findViewById(R.id.tabStatisticsText);
        t.setTextColor(getResources().getColor(R.color.colorTabSelectedText));
    }

    private void unselectTabs() {
        findViewById(R.id.tabCampaignsSelection).setBackgroundColor(Color.WHITE);
        findViewById(R.id.tabMapSelection).setBackgroundColor(Color.WHITE);
        findViewById(R.id.tabStatisticsSelection).setBackgroundColor(Color.WHITE);
        ImageView i = findViewById(R.id.tabCampaignsIcon);
        i.setImageResource(R.mipmap.ic_tab_campaign);
        i = findViewById(R.id.tabMapIcon);
        i.setImageResource(R.mipmap.ic_tab_map);
        i = findViewById(R.id.tabStatisticsIcon);
        i.setImageResource(R.mipmap.ic_tab_statistics);
        TextView t = findViewById(R.id.tabCampaignsText);
        t.setTextColor(getResources().getColor(R.color.colorTabUnselectedText));
        t = findViewById(R.id.tabMapText);
        t.setTextColor(getResources().getColor(R.color.colorTabUnselectedText));
        t = findViewById(R.id.tabStatisticsText);
        t.setTextColor(getResources().getColor(R.color.colorTabUnselectedText));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_TERMS_CONDITION) {
            campaignsFragment.onActivityResult(requestCode, resultCode, data);
        } else {
            Facebook.getInstance(this).onActivityResult(requestCode, resultCode, data);
            Google.getInstance(this).onActivityResult(requestCode, resultCode, data);
        }
    }
}
