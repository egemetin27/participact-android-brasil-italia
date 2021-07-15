package br.com.participact.participactbrasil.modules.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bergmannsoft.util.FileCache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.App;
import br.com.participact.participactbrasil.modules.db.Action;
import br.com.participact.participactbrasil.modules.db.ActionWrapper;
import br.com.participact.participactbrasil.modules.db.Campaign;
import br.com.participact.participactbrasil.modules.db.CampaignDaoImpl;
import br.com.participact.participactbrasil.modules.db.CampaignWrapper;

public class CampaignDetailsActivity extends BaseActivity {

    private Campaign campaign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_details);

        Long id = getIntent().getLongExtra("id", 0);
        if (id > 0) {
            campaign = new CampaignDaoImpl().find(id);
        }
        if (campaign == null)
            return;

        CampaignWrapper campaignWrapper = CampaignWrapper.wrap(campaign);

        View cardHeader = findViewById(R.id.cardHeader);
        Log.d(TAG, "Card color: " + campaign.getCardColor());
        int color = Color.parseColor(campaign.getCardColor());
        if (cardHeader != null) {
            cardHeader.setBackgroundColor(color);
        }
        View cardHeaderLine = findViewById(R.id.cardHeaderLine);
        if (cardHeaderLine != null)
            cardHeaderLine.setBackgroundColor(color);
        View progressBackground = findViewById(R.id.progressBackground);
        if (progressBackground != null)
            progressBackground.setBackgroundColor(color);

        final ImageView imageIcon = findViewById(R.id.imageIcon);
        imageIcon.setImageBitmap(campaignWrapper.icon(this, new FileCache.OnBitmapDownloadedListener() {
            @Override
            public void onDownloaded(Bitmap bmp) {
                imageIcon.setImageBitmap(bmp);
            }
        }));

        TextView textTitle = findViewById(R.id.textTitle);
        textTitle.setText(campaign.getName());

        TextView textDescription = findViewById(R.id.textDescription);
        if (textDescription != null)
            textDescription.setText(campaign.getText());

        TextView textDateBegin = findViewById(R.id.textDateBegin);
        if (textDateBegin != null)
            textDateBegin.setText(campaignWrapper.getStartDateFormatted());

        TextView textDateEnd = findViewById(R.id.textDateEnd);
        if (textDateEnd != null)
            textDateEnd.setText(campaignWrapper.getEndDateFormatted());

        if (campaign.getActions() != null) {
            int sensors = 0;
            LinearLayout layoutSensors = findViewById(R.id.linearLayoutSensors);
            LinearLayout layoutTasks = findViewById(R.id.linearLayoutTasks);
            List<Action> tasks = new ArrayList<>();
            for (Action action : campaign.getActions()) {
                ActionWrapper actionWrapper = ActionWrapper.wrap(campaign, action);
                if (actionWrapper.isSensing()) {
                    sensors++;
                    View view = App.getInstance().getLayoutInflater().inflate(R.layout.item_campaign_details_sensor, null);
                    ImageView icon = view.findViewById(R.id.icon);
                    TextView text = view.findViewById(R.id.text);
                    TextView description = view.findViewById(R.id.description);
                    icon.setImageBitmap(actionWrapper.icon(this));
                    text.setText(actionWrapper.getSensingName());
                    // TODO - colocar detalhes do sensor
                    description.setText("");
                    view.setTag(action);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Action action = (Action) view.getTag();
                            // TODO - Mostrar animação do sensor
                        }
                    });
                    layoutSensors.addView(view);
                } else {
                    tasks.add(action);
                }
            }
            if (sensors > 0) {
                findViewById(R.id.textNoSensors).setVisibility(View.GONE);
            }
            if (tasks.size() > 0) {
                findViewById(R.id.textNoTasks).setVisibility(View.GONE);
                int i = 0;
                View view = null;
                List<ImageView> icons = null;
                List<TextView> texts = null;
                for (Action action : tasks) {
                    ActionWrapper actionWrapper = ActionWrapper.wrap(campaign, action);
                    if (view == null || i == 3) {
                        view = App.getInstance().getLayoutInflater().inflate(R.layout.item_campaign_details_task, null);
                        layoutTasks.addView(view);
                        ImageView icon1 = view.findViewById(R.id.icon1);
                        TextView text1 = view.findViewById(R.id.text1);
                        ImageView icon2 = view.findViewById(R.id.icon2);
                        TextView text2 = view.findViewById(R.id.text2);
                        ImageView icon3 = view.findViewById(R.id.icon3);
                        TextView text3 = view.findViewById(R.id.text3);
                        icons = Arrays.asList(icon1, icon2, icon3);
                        texts = Arrays.asList(text1, text2, text3);
                        for (int x = 0; x < icons.size(); x++) {
                            icons.get(x).setImageBitmap(null);
                            texts.get(x).setText("");
                        }
                        i = 0;
                    }
                    ImageView icon = icons.get(i);
                    TextView text = texts.get(i);
                    icon.setImageBitmap(actionWrapper.icon(this));
                    text.setText(action.getName());
                    icon.setTag(action);
                    icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Action action = (Action) view.getTag();
                            ActionWrapper actionWrapper = ActionWrapper.wrap(campaign, action);
                            if (actionWrapper.isPhoto()) {
                                Intent i = new Intent(CampaignDetailsActivity.this, CampaignTaskPhotoActivity.class);
                                i.putExtra("campaignId", campaign.getId());
                                i.putExtra("actionId", action.getId());
                                startActivity(i);
                            } else if (actionWrapper.isQuestion()) {
                                actionWrapper.presentNext(CampaignDetailsActivity.this);
                            }
                        }
                    });
                    i++;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CampaignWrapper campaignWrapper = CampaignWrapper.wrap(new CampaignDaoImpl().find(campaign.getId()));
        ProgressBar progressDate = findViewById(R.id.progressDate);
        if (progressDate != null) {
            progressDate.setProgress(campaignWrapper.percentStatusDateProgress());
        }

        ProgressBar progressCampaign = findViewById(R.id.progressCampaign);
        if (progressCampaign != null) {
            progressCampaign.setProgress(campaignWrapper.getStatusProgress());
        }
        TextView textProgressCampaign = findViewById(R.id.textProgressCampaign);
        if (textProgressCampaign != null) {
            textProgressCampaign.setText(campaignWrapper.getStatusProgress() + "%");
        }
    }

    public void closeClick(View view) {
        finish();
    }
}
