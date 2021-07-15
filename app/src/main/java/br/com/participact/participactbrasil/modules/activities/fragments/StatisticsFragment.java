package br.com.participact.participactbrasil.modules.activities.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bergmannsoft.location.BSLocationListener;
import com.bergmannsoft.location.BSLocationManager;
import com.bergmannsoft.location.LocationHelpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.App;
import br.com.participact.participactbrasil.modules.controller.StatisticsController;
import br.com.participact.participactbrasil.modules.network.SessionManager;
import br.com.participact.participactbrasil.modules.network.requests.StatisticsResponse;
import br.com.participact.participactbrasil.modules.support.StatisticsCategory;
import br.com.participact.participactbrasil.modules.ui.StatisticsView;

public class StatisticsFragment extends PABaseFragment {

    List<StatisticsCategory> items = new ArrayList<>();
    StatisticsAdapter adapter;

    TextView textNoItems;

    Timer getDataTimeout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflateLayout(R.layout.fragment_statistics, inflater, container);
    }

    @Override
    public void onShow() {
        super.onShow();

        view.findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
        view.findViewById(R.id.layoutNoItems).setVisibility(View.GONE);

        items.clear();

        if (LocationHelpers.isLocationEnabled(getActivity())) {
            cancelGetDataTimeout();
            getDataTimeout = new Timer();
            getDataTimeout.schedule(new TimerTask() {
                @Override
                public void run() {
                    showErrorMessage(R.mipmap.ic_graph, "Não foi possível obter dados estatísticos no momento. Verifique sua conexão com a internet e tente novamente.");
                }
            }, 20000);

            BSLocationManager.getInstance(getActivity()).requestLocationUpdates(new StatisticsListener());
        } else {
            showErrorMessage(R.mipmap.ic_location, "Sua localização está indisponível. O ParticipAct necessita de acesso à sua localização para obter dados estatísticos.");
        }

    }

    private void showErrorMessage(final int icon, final String message) {
        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.findViewById(R.id.progressbar).setVisibility(View.GONE);
                view.findViewById(R.id.layoutNoItems).setVisibility(items.size() > 0 ? View.GONE : View.VISIBLE);
                textNoItems.setText(message);
                ImageView imageIcon = view.findViewById(R.id.imageNoItemsIcon);
                imageIcon.setImageResource(icon);
            }
        });
    }

    private void cancelGetDataTimeout() {
        if (getDataTimeout != null) {
            getDataTimeout.cancel();
        }
    }

    class StatisticsListener extends BSLocationListener {

        @Override
        public void onLocationChanged(Location location) {
            SessionManager.getInstance().statistics(location.getLatitude(), location.getLongitude(), new SessionManager.RequestCallback<StatisticsResponse>() {
                @Override
                public void onResponse(StatisticsResponse response) {
                    cancelGetDataTimeout();
                    if (response != null && response.isSuccess()) {
                        view.findViewById(R.id.progressbar).setVisibility(View.GONE);
                        items = StatisticsController.build(getContext(), response);
                        adapter.notifyDataSetChanged();
                        view.findViewById(R.id.layoutNoItems).setVisibility(View.GONE);
                        if (items.size() == 0) {
                            showErrorMessage(R.mipmap.ic_graph, "Não há itens estatísticos na sua região.");
                        }
                    } else {
                        showErrorMessage(R.mipmap.ic_graph, "Ocorreu um erro obtendo dados estatísticos na sua região.");
                        Toast.makeText(getActivity(), "Erro atualizando estatísticas.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    cancelGetDataTimeout();
                    showErrorMessage(R.mipmap.ic_graph, "Não foi possível obter dados estatísticos nesse momento. Tente novamente mais tarde.");
                    Toast.makeText(getActivity(), "Falha atualizando estatísticas.", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public boolean shouldContinueUpdating() {
            return false;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new StatisticsAdapter();
        ListView listView = view.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        textNoItems = view.findViewById(R.id.textNoItems);

    }

    class StatisticsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public StatisticsCategory getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Holder holder;
            view = App.getInstance().getLayoutInflater().inflate(R.layout.item_statistic, null);
            holder = new Holder();
            holder.update(view, getItem(i));
            return view;
        }
    }

    class Holder {

        public void update(View view, StatisticsCategory item) {
            TextView title = view.findViewById(R.id.title);
            StatisticsView statisticsView = view.findViewById(R.id.statisticView);

            title.setText(item.getName());
            statisticsView.setup(item.getStatistics());
        }
    }

}
