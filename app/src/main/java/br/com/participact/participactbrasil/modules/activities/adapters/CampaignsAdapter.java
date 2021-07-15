package br.com.participact.participactbrasil.modules.activities.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bergmannsoft.util.FileCache;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.App;
import br.com.participact.participactbrasil.modules.activities.CampaignDetailsActivity;
import br.com.participact.participactbrasil.modules.activities.CampaignTaskPhotoActivity;
import br.com.participact.participactbrasil.modules.db.Action;
import br.com.participact.participactbrasil.modules.db.ActionWrapper;
import br.com.participact.participactbrasil.modules.db.Campaign;
import br.com.participact.participactbrasil.modules.db.CampaignDaoImpl;
import br.com.participact.participactbrasil.modules.db.CampaignWrapper;
import br.com.participact.participactbrasil.modules.dialog.QuestionStatusDialog;
import br.com.participact.participactbrasil.modules.support.MessageType;

public class CampaignsAdapter extends BaseAdapter {

    private static final String TAG = CampaignsAdapter.class.getSimpleName();
    Context context;

    List<Campaign> campaigns = new ArrayList<>();

    boolean showingArchived = false;

    QuestionStatusDialog questionStatusDialog;

    public interface OnHeaderClickListener {
        void onClick(Campaign campaign);
    }

    OnHeaderClickListener onHeaderClickListener;

    public CampaignsAdapter(Context context) {
        this.context = context;
    }

    public void setOnHeaderClickListener(OnHeaderClickListener onHeaderClickListener) {
        this.onHeaderClickListener = onHeaderClickListener;
    }

    public void reloadData() {
        App.getInstance().getJobHandler().post(new Runnable() {
            @Override
            public void run() {
                CampaignDaoImpl dao = new CampaignDaoImpl();
                campaigns = dao.fetch();
                if (showingArchived) {
                    campaigns.addAll(dao.fetchArchived());
                }
                App.getInstance().getUiHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public int getCount() {
        return campaigns.size();
    }

    @Override
    public Campaign getItem(int i) {
        try {
            return campaigns.get(i);
        } catch (Exception e) {
            Crashlytics.log(Log.ERROR, TAG, e.toString());
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return campaigns.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Campaign campaign = getItem(i);
        if (campaign == null) {
            return view;
        }
        CampaignWrapper campaignWrapper = CampaignWrapper.wrap(campaign);
        if (campaignWrapper.isArchivedHeader()) {
            View view1 = App.getInstance().getLayoutInflater().inflate(R.layout.card_campaign_archiveds, null);
            TextView t = view1.findViewById(R.id.textArchived);
            t.setText(showingArchived ? "Ocultar Arquivadas" : "Mostrar Arquivadas (" + campaign.getArchivedCount() + ")");
            view1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showingArchived = !showingArchived;
                    reloadData();
                }
            });
            return view1;
        }
        if (campaignWrapper.isMap()) {
            View view1 = App.getInstance().getLayoutInflater().inflate(R.layout.card_campaign, null);
            view1.findViewById(R.id.buttonParticipate).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    App.getInstance().dispatchMessage(MessageType.PARTICIPATE, campaign);
                }
            });
            return view1;
        } else {
            if (campaignWrapper.getState() == CampaignWrapper.State.available) {
                view = buildView(
                        campaignWrapper.isCardOpen() ?
                                App.getInstance().getLayoutInflater().inflate(R.layout.card_campaign_participate_open, null) :
                                App.getInstance().getLayoutInflater().inflate(R.layout.card_campaign_participate_close, null),
                        campaign,
                        campaignWrapper
                );
            } else {
                view = buildView(
                        campaignWrapper.isCardOpen() ?
                                App.getInstance().getLayoutInflater().inflate(R.layout.card_campaign_progress_open, null) :
                                App.getInstance().getLayoutInflater().inflate(R.layout.card_campaign_progress_close, null),
                        campaign,
                        campaignWrapper
                );
            }
        }
        return view;
    }

    private View buildView(View view, final Campaign campaign, final CampaignWrapper campaignWrapper) {
        long t1 = System.currentTimeMillis();
        View cardHeader = view.findViewById(R.id.cardHeader);
        Log.d(TAG, "Card color: " + campaign.getCardColor());
        int color = Color.parseColor(campaign.getCardColor());
        if (cardHeader != null) {
            cardHeader.setBackgroundTintList(ColorStateList.valueOf(color));
            cardHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onHeaderClickListener != null)
                        onHeaderClickListener.onClick(campaign);
                }
            });
        }
        //cardHeader.setBackgroundColor(Color.parseColor(campaign.getCardColor()));
        View cardHeaderLine = view.findViewById(R.id.cardHeaderLine);
        if (cardHeaderLine != null)
            cardHeaderLine.setBackgroundColor(color);
        View progressBackground = view.findViewById(R.id.progressBackground);
        if (progressBackground != null)
            progressBackground.setBackgroundColor(color);

        ImageView imageIcon = view.findViewById(R.id.imageIcon);
        imageIcon.setImageBitmap(campaignWrapper.icon(context, new FileCache.OnBitmapDownloadedListener() {
            @Override
            public void onDownloaded(Bitmap bmp) {
                notifyDataSetChanged();
            }
        }));

        TextView textTitle = view.findViewById(R.id.textTitle);
        textTitle.setText(campaign.getName());

        TextView textDescription = view.findViewById(R.id.textDescription);
        if (textDescription != null)
            textDescription.setText(campaign.getText());

        TextView textDateBegin = view.findViewById(R.id.textDateBegin);
        if (textDateBegin != null)
            textDateBegin.setText(campaignWrapper.getStartDateFormatted());

        TextView textDateEnd = view.findViewById(R.id.textDateEnd);
        if (textDateEnd != null)
            textDateEnd.setText(campaignWrapper.getEndDateFormatted());

        CheckBox checkBoxAccept = view.findViewById(R.id.checkboxAccept);
        if (checkBoxAccept != null) {
            checkBoxAccept.setOnCheckedChangeListener(null);
            checkBoxAccept.setChecked(campaignWrapper.isAgreementAccepted());
            checkBoxAccept.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    campaignWrapper.setAgreementAccepted(checked);
                }
            });
        }

        TextView termsConditions = view.findViewById(R.id.textTermsConditions);
        if (termsConditions != null)
            termsConditions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    App.getInstance().dispatchMessage(MessageType.SHOW_AGREEMENT, campaign);
                }
            });

        Button participate = view.findViewById(R.id.buttonParticipate);
        if (participate != null)
            participate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    App.getInstance().dispatchMessage(MessageType.PARTICIPATE, campaign);
                }
            });

        boolean isEnded = campaignWrapper.isStatusEnded();
        boolean isCompleted = campaignWrapper.isStatusCompleted();
        int progress = campaignWrapper.getStatusProgress();
        int dateProgress = campaignWrapper.percentStatusDateProgress();

        View imageFinished = view.findViewById(R.id.imageFinished);
        if (imageFinished != null)
            imageFinished.setVisibility(isEnded || isCompleted ? View.VISIBLE : View.GONE);

        Button pauseResume = view.findViewById(R.id.buttonPauseResumeCampaign);
        if (pauseResume != null) {
            pauseResume.setEnabled(true);
            pauseResume.setAlpha(1.0f);
            if (campaignWrapper.getState() == CampaignWrapper.State.running) {
                pauseResume.setText("PAUSAR CAMPANHA");
            } else {
                pauseResume.setText("CONTINUAR CAMPANHA");
            }
            if (isEnded || isCompleted) {
                pauseResume.setText("ARQUIVAR CAMPANHA");
                if (campaignWrapper.isArchived()) {
                    pauseResume.setText("CAMPANHA ARQUIVADA");
                    pauseResume.setEnabled(false);
                    pauseResume.setAlpha(0.4f);
                } else {
                    pauseResume.setEnabled(true);
                    pauseResume.setAlpha(1.0f);
                }
            }
            pauseResume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    App.getInstance().dispatchMessage(MessageType.PAUSE_RESUME, campaign);
                }
            });
        }

        ProgressBar progressDate = view.findViewById(R.id.progressDate);
        if (progressDate != null) {
            progressDate.setProgress(dateProgress);
        }

        LinearLayout layoutTaskItems = view.findViewById(R.id.layoutTaskItems);
        if (layoutTaskItems != null) {
            layoutTaskItems.removeAllViews();
            if (campaign.getActions() != null) {
                for (Action action : campaign.getActions()) {
                    View taskItem = App.getInstance().getLayoutInflater().inflate(R.layout.card_campaign_task_item, null);
                    ImageView icon = taskItem.findViewById(R.id.imageIcon);
                    TextView desc = taskItem.findViewById(R.id.textDescription);
                    ActionWrapper actionWrapper = ActionWrapper.wrap(campaign, action);
                    icon.setImageBitmap(actionWrapper.icon(context));
                    //icon.setColorFilter(actionWrapper.tintColor());
                    desc.setText(action.getName());
                    layoutTaskItems.addView(taskItem);
                }
            }
        }

        ProgressBar progressCampaign = view.findViewById(R.id.progressCampaign);
        if (progressCampaign != null) {
            progressCampaign.setProgress(progress);
        }
        TextView textProgressCampaign = view.findViewById(R.id.textProgressCampaign);
        if (textProgressCampaign != null) {
            textProgressCampaign.setText(progress + "%");
        }

        ImageView sensorIcon1 = view.findViewById(R.id.sensorIcon1);
        TextView sensorText1 = view.findViewById(R.id.sensorText1);
        ImageView sensorIcon2 = view.findViewById(R.id.sensorIcon2);
        TextView sensorText2 = view.findViewById(R.id.sensorText2);
        ImageView sensorIconMore = view.findViewById(R.id.sensorIconMore);
        TextView sensorTextMore = view.findViewById(R.id.sensorTextMore);
        TextView textNoSensors = view.findViewById(R.id.textNoSensors);

        ImageView taskIcon1 = view.findViewById(R.id.taskIcon1);
        TextView taskText1 = view.findViewById(R.id.taskText1);
        ImageView taskIcon2 = view.findViewById(R.id.taskIcon2);
        TextView taskText2 = view.findViewById(R.id.taskText2);
        ImageView taskIconMore = view.findViewById(R.id.taskIconMore);
        TextView taskTextMore = view.findViewById(R.id.taskTextMore);
        TextView textNoTasks = view.findViewById(R.id.textNoTasks);

        Button status1 = view.findViewById(R.id.buttonStatus1);
        Button status2 = view.findViewById(R.id.buttonStatus2);

        if (status1 != null && status2 != null) {
            status1.setVisibility(View.GONE);
            status2.setVisibility(View.GONE);
        }

        if (sensorIcon1 != null && sensorText1 != null && sensorIcon2 != null && sensorText2 != null && sensorIconMore != null && sensorTextMore != null && textNoSensors != null) {

            sensorIcon1.setImageBitmap(null);
            sensorIcon2.setImageBitmap(null);
            sensorIconMore.setVisibility(View.GONE);
            sensorText1.setText("");
            sensorText2.setText("");
            sensorTextMore.setText("");

            taskIcon1.setImageBitmap(null);
            taskIcon2.setImageBitmap(null);
            taskIconMore.setVisibility(View.GONE);
            taskText1.setText("");
            taskText2.setText("");
            taskTextMore.setText("");

            textNoSensors.setVisibility(View.VISIBLE);
            textNoTasks.setVisibility(View.VISIBLE);

            if (campaign.getActions() != null) {
                int sensors = 0;
                int tasks = 0;
                for (Action action : campaign.getActions()) {
                    ActionWrapper actionWrapper = ActionWrapper.wrap(campaign, action);
                    if (actionWrapper.isSensing()) {
                        if (sensors < 2) {
                            if (sensors == 0) {
                                sensorIcon1.setImageBitmap(actionWrapper.icon(context));
                                sensorIcon1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        App.getInstance().dispatchMessage(MessageType.CAMPAIGN_DETAILS, campaign);
                                    }
                                });
                                sensorText1.setText(action.getName());
                                //sensorIcon1.setColorFilter(actionWrapper.tintColor());
                            } else {
                                sensorIcon2.setImageBitmap(actionWrapper.icon(context));
                                sensorIcon2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        App.getInstance().dispatchMessage(MessageType.CAMPAIGN_DETAILS, campaign);
                                    }
                                });
                                sensorText2.setText(action.getName());
                                //sensorIcon2.setColorFilter(actionWrapper.tintColor());
                            }
                        }
                        sensors++;
                    } else {
                        if (tasks < 2) {
                            if (tasks == 0) {
                                taskIcon1.setImageBitmap(actionWrapper.icon(context));
                                taskIcon1.setTag(actionWrapper);
                                taskIcon1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        showTask((ActionWrapper) view.getTag(), campaign);
                                    }
                                });
                                taskText1.setText(action.getName());
                                //taskIcon1.setColorFilter(actionWrapper.tintColor());
                                if (actionWrapper.isQuestion()) {
                                    status1.setVisibility(View.VISIBLE);
                                    status1.setTag(actionWrapper);
                                    status1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            showQuestionStatusDialog();
                                        }
                                    });
                                }
                            } else {
                                taskIcon2.setImageBitmap(actionWrapper.icon(context));
                                taskIcon2.setTag(actionWrapper);
                                taskIcon2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        showTask((ActionWrapper) view.getTag(), campaign);
                                    }
                                });
                                taskText2.setText(action.getName());
                                //taskIcon2.setColorFilter(actionWrapper.tintColor());
                                status2.setVisibility(View.VISIBLE);
                                status2.setTag(actionWrapper);
                                status2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        showQuestionStatusDialog();
                                    }
                                });
                            }
                        }
                        tasks++;
                    }
                }
                textNoSensors.setVisibility(sensors == 0 ? View.VISIBLE : View.GONE);
                textNoTasks.setVisibility(tasks == 0 ? View.VISIBLE : View.GONE);
                int moreSensors = sensors - 2;
                int moreTasks = tasks - 2;
                if (moreSensors > 0) {
                    sensorIconMore.setVisibility(View.VISIBLE);
                    sensorIconMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            App.getInstance().dispatchMessage(MessageType.CAMPAIGN_DETAILS, campaign);
                        }
                    });
                    sensorTextMore.setText("+ " + moreSensors + " SENSORES");
                }
                if (moreTasks > 0) {
                    taskIconMore.setVisibility(View.VISIBLE);
                    taskIconMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            App.getInstance().dispatchMessage(MessageType.CAMPAIGN_DETAILS, campaign);
                        }
                    });
                    taskTextMore.setText("+ " + moreTasks + " TAREFAS");
                }
                //sensorIconMore.setColorFilter(Color.DKGRAY);
                //taskIconMore.setColorFilter(Color.DKGRAY);
            }
        }

//        View layoutSensors = view.findViewById(R.id.layoutSensors);
//        if (layoutSensors != null) {
//            layoutSensors.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    App.getInstance().dispatchMessage(MessageType.CAMPAIGN_DETAILS, campaign);
//                }
//            });
//        }
//        View layoutTasks = view.findViewById(R.id.layoutTasks);
//        if (layoutTasks != null) {
//            layoutTasks.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    App.getInstance().dispatchMessage(MessageType.CAMPAIGN_DETAILS, campaign);
//                }
//            });
//        }

        Log.d(TAG, "Adapter build time: " + (System.currentTimeMillis() - t1));

        return view;
    }

    private void showTask(ActionWrapper actionWrapper, Campaign campaign) {
        if (actionWrapper.isPhoto()) {
            Intent i = new Intent(context, CampaignTaskPhotoActivity.class);
            i.putExtra("campaignId", campaign.getId());
            i.putExtra("actionId", actionWrapper.getAction().getId());
            context.startActivity(i);
        } else if (actionWrapper.isQuestion()) {
            actionWrapper.presentNext(context);
        }
    }

    private void showQuestionStatusDialog() {
        if (questionStatusDialog != null) {
            questionStatusDialog.dismiss();
        }
        questionStatusDialog = new QuestionStatusDialog(context);
        questionStatusDialog.show();
    }


}
