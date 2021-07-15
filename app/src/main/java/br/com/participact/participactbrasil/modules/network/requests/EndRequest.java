package br.com.participact.participactbrasil.modules.network.requests;

import com.google.gson.annotations.SerializedName;

import br.com.participact.participactbrasil.modules.db.Campaign;
import br.com.participact.participactbrasil.modules.db.CampaignWrapper;

public class EndRequest {

    @SerializedName("taskId")
    Long campaignId;
    Integer sensingProgress;
    Integer photoProgress;
    Integer questionnaireProgress;

    /**
     * Use this constructor when the campaign is completed with success.
     * @param campaignId
     */
    private EndRequest(Long campaignId) {
        this.campaignId = campaignId;
    }

    /**
     * Use this constructor whent he campaign is completed with error.
     * @param campaignId
     * @param sensingProgress
     * @param photoProgress
     * @param questionnaireProgress
     */
    private EndRequest(Long campaignId, Integer sensingProgress, Integer photoProgress, Integer questionnaireProgress) {
        this.campaignId = campaignId;
        this.sensingProgress = sensingProgress;
        this.photoProgress = photoProgress;
        this.questionnaireProgress = questionnaireProgress;
    }

    public static EndRequest create(Campaign campaign) {
        CampaignWrapper wrapper = CampaignWrapper.wrap(campaign);
        if (wrapper.isStatusCompleted()) {
            return new EndRequest(campaign.getId());
        } else {
            return new EndRequest(campaign.getId(), wrapper.getSensingProgress(), wrapper.getPhotoProgress(), wrapper.getQuestionnaireProgress());
        }
    }

}
