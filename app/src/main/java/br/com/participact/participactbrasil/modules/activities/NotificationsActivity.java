package br.com.participact.participactbrasil.modules.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.App;
import br.com.participact.participactbrasil.modules.db.PANotification;
import br.com.participact.participactbrasil.modules.db.PANotificationDaoImpl;
import br.com.participact.participactbrasil.modules.network.SessionManager;
import br.com.participact.participactbrasil.modules.network.requests.NotificationsResponse;

public class NotificationsActivity extends BaseActivity {

    PANotificationDaoImpl dao = new PANotificationDaoImpl();
    List<PANotification> notifications;
    private NotificationsAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        mSwipeRefreshLayout = findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNotifications();
            }
        });

        adapter = new NotificationsAdapter();
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        updateUI();

        getNotifications();
    }

    private void getNotifications() {
        SessionManager.getInstance().notifications(new SessionManager.RequestCallback<NotificationsResponse>() {
            @Override
            public void onResponse(NotificationsResponse response) {
                if (response != null && response.isSuccess()) {
                    if (response.getNotifications() != null) {
                        for (PANotification notification : response.getNotifications()) {
                            dao.save(notification);
                        }
                    }
                }
                updateUI();
            }

            @Override
            public void onFailure(Throwable t) {
                showError(t.getLocalizedMessage());
                updateUI();
            }

        });
    }

    private void updateUI() {
        notifications = dao.fetch();
        jobHandler.post(new Runnable() {
            @Override
            public void run() {
                dao.markAllRead();
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView noItems = findViewById(R.id.textNoItems);
                noItems.setVisibility(notifications.size() > 0 ? View.GONE : View.VISIBLE);
                adapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void closeClick(View view) {
        finish();
    }

    class NotificationsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return notifications == null ? 0 : notifications.size();
        }

        @Override
        public PANotification getItem(int i) {
            return notifications.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Holder holder;
            if (view == null) {
                view = App.getInstance().getLayoutInflater().inflate(R.layout.simple_list_item, null);
                holder = new Holder();
                view.setTag(holder);
            } else {
                holder = (Holder) view.getTag();
            }
            holder.update(view, getItem(i));
            return view;
        }
    }

    class Holder {

        TextView message;

        public void update(View view, PANotification item) {
            message = view.findViewById(R.id.text);
            message.setText(item.getMessage());
        }
    }

}
