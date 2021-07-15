package br.com.participact.participactbrasil.modules.activities.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bergmannsoft.dialog.AlertDialogUtils;
import com.bergmannsoft.util.ConnectionUtils;
import com.crashlytics.android.Crashlytics;

import java.util.Objects;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.activities.CampaignDetailsActivity;
import br.com.participact.participactbrasil.modules.activities.MainActivity;
import br.com.participact.participactbrasil.modules.activities.TermsConditionsActivity;
import br.com.participact.participactbrasil.modules.activities.adapters.CampaignsAdapter;
import br.com.participact.participactbrasil.modules.db.Campaign;
import br.com.participact.participactbrasil.modules.db.CampaignDaoImpl;
import br.com.participact.participactbrasil.modules.db.CampaignWrapper;
import br.com.participact.participactbrasil.modules.db.PendingRequestDaoImpl;
import br.com.participact.participactbrasil.modules.network.SessionManager;
import br.com.participact.participactbrasil.modules.network.requests.CampaignsResponse;
import br.com.participact.participactbrasil.modules.network.requests.ParticipateResponse;
import br.com.participact.participactbrasil.modules.support.UserSettings;

import static br.com.participact.participactbrasil.modules.support.MessageType.CAMPAIGN_DETAILS;
import static br.com.participact.participactbrasil.modules.support.MessageType.PARTICIPATE;
import static br.com.participact.participactbrasil.modules.support.MessageType.PAUSE_RESUME;
import static br.com.participact.participactbrasil.modules.support.MessageType.SHOW_AGREEMENT;

public class CampaignsFragment extends PABaseFragment {

    public interface CampaignCallback {
        void onCampaignSelected(Campaign campaign);
    }

    private CampaignCallback callback;

    private CampaignsAdapter campaignsAdapter;

    private CampaignDaoImpl campaignDao = new CampaignDaoImpl();

    private SwipeRefreshLayout mSwipeRefreshLayout;

    public void setCallback(CampaignCallback callback) {
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflateLayout(R.layout.fragment_campaigns, inflater, container);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCampaigns();
            }
        });

        ListView listView = view.findViewById(R.id.listView);
        campaignsAdapter = new CampaignsAdapter(getActivity());
        campaignsAdapter.setOnHeaderClickListener(new CampaignsAdapter.OnHeaderClickListener() {
            @Override
            public void onClick(Campaign campaign) {
                onCampaignSelected(campaign);
            }
        });
        listView.setAdapter(campaignsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Campaign campaign = campaignsAdapter.getItem(i);
                onCampaignSelected(campaign);
            }
        });
        campaignsAdapter.reloadData();

        getCampaigns();
    }

    @Override
    public void onShow() {
        super.onShow();
        if (campaignsAdapter != null) {
            campaignsAdapter.reloadData();
        }
    }

    private void onCampaignSelected(Campaign campaign) {
        if (callback != null)
            callback.onCampaignSelected(campaign);
        CampaignWrapper wrapper = CampaignWrapper.wrap(campaign);
        if (!wrapper.isMap()) {
            wrapper.toggleCardOpen();
            campaignsAdapter.reloadData();
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_AGREEMENT:
                Campaign campaign = (Campaign) msg.obj;
                showAgreement(campaign);
                return true;
            case PARTICIPATE:
                campaign = (Campaign) msg.obj;
                if (CampaignWrapper.wrap(campaign).isMap()) {
                    callback.onCampaignSelected(campaign);
                } else {
                    participate(campaign);
                }
                return true;
            case PAUSE_RESUME:
                campaign = (Campaign) msg.obj;
                pauseResume(campaign);
                return true;
            case CAMPAIGN_DETAILS:
                campaign = (Campaign) msg.obj;
                Intent i = new Intent(getActivity(), CampaignDetailsActivity.class);
                i.putExtra("id", campaign.getId());
                getActivity().startActivity(i);
                return true;
        }
        return super.handleMessage(msg);
    }

    public void showAgreement(Campaign campaign) {
        Intent i = new Intent(getActivity(), TermsConditionsActivity.class);
        i.putExtra("id", campaign.getId());
        getActivity().startActivityForResult(i, MainActivity.REQUEST_CODE_TERMS_CONDITION);
    }

    public void participate(final Campaign campaign) {
        CampaignWrapper wrapper = CampaignWrapper.wrap(new CampaignDaoImpl().find(campaign.getId()));
        if (wrapper.isCardOpen()) {
            if (wrapper.isAgreementAccepted()) {
                if (wrapper.isStatusEnded()) {
                    AlertDialogUtils.showAlert(getActivity(), "Campanha finalizada.");
                } else {
                    ConnectionUtils.isOnline(getActivity(), new ConnectionUtils.CheckOnlineCallback() {
                        @Override
                        public void onResponse(boolean isOnline) {
                            if (isOnline) {
                                SessionManager.getInstance().participate(campaign.getId(), new SessionManager.RequestCallback<ParticipateResponse>() {
                                    @Override
                                    public void onResponse(ParticipateResponse response) {
                                        if (response != null && response.isSuccess()) {
                                            CampaignWrapper.wrap(campaign).resume();
                                            campaignsAdapter.reloadData();
                                        } else {
//                                    if (response != null) {
//                                        AlertDialogUtils.showError(getActivity(), response.getMessage());
//                                    } else {
//                                        AlertDialogUtils.showError(getActivity(), "Erro iniciando campanha.");
//                                    }
                                            onParticipateFailure(campaign);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {
                                        //AlertDialogUtils.showError(getActivity(), t.getLocalizedMessage());
                                        onParticipateFailure(campaign);
                                    }
                                });
                            } else {
                                onParticipateFailure(campaign);
                            }
                        }
                    });

                }
            } else {
                AlertDialogUtils.showAlert(getActivity(), "Por favor, aceite os \"Termos e Condições\" para poder participar da campanha.");
            }
        } else {
            wrapper.setCardOpen(true);
            campaignsAdapter.reloadData();
        }
    }

    private void onParticipateFailure(Campaign campaign) {
        new PendingRequestDaoImpl().save(campaign);
        CampaignWrapper.wrap(campaign).resume();
        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                campaignsAdapter.reloadData();
            }
        });
    }

    public void pauseResume(Campaign campaign) {
        final CampaignWrapper wrapper = CampaignWrapper.wrap(new CampaignDaoImpl().find(campaign.getId()));
        if (wrapper.isStatusEnded() || wrapper.isStatusCompleted()) {
            AlertDialogUtils.createDialog(getActivity(), "Arquivar campanha", "Tem certeza que deseja arquivar essa campanha?", "Sim", "Não", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    wrapper.archive();
                    wrapper.commit();
                    campaignsAdapter.reloadData();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        } else {
            wrapper.pauseResume();
            campaignsAdapter.reloadData();
        }
    }

    private long serverErrorLastTime = 0;

    public void getCampaigns() {
        SessionManager.getInstance().campaigns(new SessionManager.RequestCallback<CampaignsResponse>() {
            @Override
            public void onResponse(CampaignsResponse response) {
                if (response != null && response.isSuccess()) {
                    if (response.getCampaigns() != null) {
                        UserSettings.getInstance().setCampaignsVersion(response.getVersion());
                        for (Campaign campaign : response.getCampaigns()) {
                            campaignDao.save(campaign);
                        }
                    }
                } else {
                    long now = System.currentTimeMillis();
                    if (now - serverErrorLastTime > 300000) {
                        serverErrorLastTime = now;
                        String msg = "";
                        if (response != null && response.getMessage() != null && response.getMessage().length() > 0) {
                            msg = response.getMessage();
                            //AlertDialogUtils.showError(getActivity(), response.getMessage());
                            //} else {
                            //AlertDialogUtils.showError(getActivity(), "Erro obtendo campanhas.");
                        }
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                    }
                }
                campaignsAdapter.reloadData();
                mSwipeRefreshLayout.setRefreshing(false);

                SessionManager.getInstance().campaignsDeleted(new SessionManager.RequestCallback<CampaignsResponse>() {
                    @Override
                    public void onResponse(CampaignsResponse response) {
                        if (response != null && response.isSuccess()) {
                            UserSettings.getInstance().setCampaignsVersionDeleted(response.getVersion());
                            if (response.getCampaigns() != null) {
                                CampaignDaoImpl dao = new CampaignDaoImpl();
                                for (Campaign campaign : response.getCampaigns()) {
                                    dao.delete(campaign.getId());
                                }
                            }
                        }
                        campaignsAdapter.reloadData();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e(TAG, null, t);
                    }
                });

            }

            @Override
            public void onFailure(Throwable t) {
                //AlertDialogUtils.showError(getActivity(), t.getLocalizedMessage());
                long now = System.currentTimeMillis();
                if (now - serverErrorLastTime > 300000) {
                    serverErrorLastTime = now;
                    try {
                        Toast.makeText(getActivity(), "Sem conexão com a internet.", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Crashlytics.log(Log.ERROR, TAG, e.toString());
                    }
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MainActivity.REQUEST_CODE_TERMS_CONDITION) {
            if (resultCode == Activity.RESULT_OK) {
                campaignsAdapter.reloadData();
            }
        }
    }
}
