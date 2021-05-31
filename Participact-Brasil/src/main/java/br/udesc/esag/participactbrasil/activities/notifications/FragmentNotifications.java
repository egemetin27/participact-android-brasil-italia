package br.udesc.esag.participactbrasil.activities.notifications;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.List;
import java.util.Objects;

import br.com.bergmannsoft.util.AlertDialogUtils;
import br.com.bergmannsoft.util.Utils;
import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.dialog.ProgressDialog;
import br.udesc.esag.participactbrasil.domain.persistence.Message;
import br.udesc.esag.participactbrasil.domain.persistence.support.NotificationsDao;
import br.udesc.esag.participactbrasil.domain.rest.NotificationsResult;
import br.udesc.esag.participactbrasil.network.request.NotificationsRequest;
import br.udesc.esag.participactbrasil.network.request.ParticipactSpringAndroidService;

/**
 * Created by fabiobergmann on 23/02/17.
 */

public class FragmentNotifications extends Fragment {

    private View fragment;
    private SpiceManager contentManager = new SpiceManager(ParticipactSpringAndroidService.class);

    private NotificationsDao dao;
    private List<Message> items;
    private NotificationsAdapter adapter;

    public static FragmentNotifications newInstance() {
        return new FragmentNotifications();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_messages, container, false);

        dao = NotificationsDao.getInstance(getActivity().getApplicationContext());

        items = dao.fetchAll();

        if (!contentManager.isStarted()) {
            contentManager.start(getActivity());
        }

        if (items.size() == 0)
            ProgressDialog.show(getActivity(), null);

        adapter = new NotificationsAdapter(items);
        ListView listView = (ListView) fragment.findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Utils.savePreferenceBoolean(getActivity(), "notifications", true);
                Message message = items.get(position);
                Intent i = new Intent(getActivity(), NotificationActivity.class);
                i.putExtra("text", message.getText());
                getActivity().startActivity(i);
            }
        });

        NotificationsRequest request = new NotificationsRequest(getActivity());
        contentManager.execute(request, request.getRequestCacheKey(), DurationInMillis.ALWAYS_EXPIRED, new RequestListener<NotificationsResult>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                ProgressDialog.dismiss(getActivity());
                AlertDialogUtils.showError(getActivity(), spiceException.getMessage());
            }

            @Override
            public void onRequestSuccess(final NotificationsResult result) {

                ProgressDialog.dismiss(getActivity());
                if (result.isSuccess()) {
                    if (result.getItems() != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (List<Object> objects : result.getItems()) {
                                    long id = Long.parseLong(objects.get(0).toString());
                                    String text = (String) objects.get(1);
                                    dao.save(getActivity().getApplicationContext(), new Message(id, text));
                                }
                                items = dao.fetchAll();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.setMessages(items);
                                    }
                                });
                            }
                        }).start();
                    }
                }

            }
        });

        return fragment;
    }

    class NotificationsAdapter extends BaseAdapter {

        private final LayoutInflater layoutInflater;
        private List<Message> messages;

        public NotificationsAdapter(List<Message> messages) {
            this.messages = messages;
            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return messages.size();
        }

        @Override
        public Message getItem(int position) {
            return messages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = layoutInflater.inflate(R.layout.list_item_message, null);
            TextView tv = (TextView) convertView.findViewById(R.id.text_message);
            tv.setText(getItem(position).getText());
            return convertView;
        }

        public void setMessages(List<Message> messages) {
            this.messages = messages;
            notifyDataSetChanged();
        }
    }

}
