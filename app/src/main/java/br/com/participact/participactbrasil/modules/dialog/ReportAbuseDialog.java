package br.com.participact.participactbrasil.modules.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bergmannsoft.dialog.AlertDialogUtils;
import com.bergmannsoft.dialog.BProgressDialog;
import com.bergmannsoft.dialog.ColoredDialog;
import com.bergmannsoft.rest.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.App;
import br.com.participact.participactbrasil.modules.db.AbuseType;
import br.com.participact.participactbrasil.modules.db.AbuseTypeDaoImpl;
import br.com.participact.participactbrasil.modules.network.SessionManager;
import br.com.participact.participactbrasil.modules.network.requests.AbuseTypeResponse;

public class ReportAbuseDialog extends ColoredDialog {

    private final Long reportId;
    private List<AbuseType> items = new ArrayList<>();

    private Adapter mAdapter;

    public ReportAbuseDialog(Context context, Long reportId) {
        super(context, null, R.layout.dialog_report_abuse);

        this.reportId = reportId;

        ListView listView = view.findViewById(R.id.listView);
        mAdapter = new Adapter();

        final AbuseTypeDaoImpl dao = new AbuseTypeDaoImpl();

        items = dao.fetch();

        listView.setAdapter(mAdapter);

        if (items.size() == 0) {
            BProgressDialog.showProgressBar(getActivity(), false);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                for (AbuseType type : items) {
                    type.setSelected(false);
                }
                items.get(i).setSelected(true);
                mAdapter.notifyDataSetChanged();
            }
        });

        SessionManager.getInstance().abuseTypes(new SessionManager.RequestCallback<AbuseTypeResponse>() {
            @Override
            public void onResponse(AbuseTypeResponse response) {
                BProgressDialog.hideProgressBar();
                if (response != null && response.getTypes() != null) {
                    dao.removeAll();
                    for (AbuseType type : response.getTypes()) {
                        dao.save(type);
                    }
                    items = dao.fetch();
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {
                BProgressDialog.hideProgressBar();
            }
        });

        view.findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        view.findViewById(R.id.buttonSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send();
            }
        });

    }

    private void send() {
        AbuseType selected = null;
        for (AbuseType type : items) {
            if (type.getSelected()) {
                selected = type;
                break;
            }
        }
        if (selected != null) {
            BProgressDialog.showProgressBar(getActivity(), false);
            SessionManager.getInstance().abuseSubmit(reportId, selected.getId(), new SessionManager.RequestCallback<Response>() {
                @Override
                public void onResponse(Response response) {
                    BProgressDialog.hideProgressBar();
                    if (response != null && response.isSuccess()) {
                        Toast.makeText(getActivity(), "Relato enviado com sucesso!", Toast.LENGTH_LONG).show();
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dismiss();
                                    }
                                });
                            }
                        }, 1000);
                    } else {
                        AlertDialogUtils.showAlert(getActivity(), "Ocorreu um erro enviando seu relato.");
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    BProgressDialog.hideProgressBar();
                    AlertDialogUtils.showAlert(getActivity(), "Ocorreu um erro enviando seu relato.");
                }
            });
        } else {
            AlertDialogUtils.showAlert(getActivity(), "Por favor, selecione uma opção.");
        }
    }

    class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public AbuseType getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = App.getInstance().getLayoutInflater().inflate(R.layout.item_report_abuse, null);
                view.setTag(new Holder(view));
            }
            ((Holder)view.getTag()).update(getItem(i));
            return view;
        }
    }

    class Holder {
        ImageView check;
        TextView text;

        public Holder(View view) {
            check = view.findViewById(R.id.imageCheck);
            text = view.findViewById(R.id.textOption);
        }

        public void update(AbuseType item) {
            check.setImageResource(item.getSelected() ? R.mipmap.ic_bt_radio_on : R.mipmap.ic_bt_radio_off);
            text.setText(item.getText());
        }
    }

}
