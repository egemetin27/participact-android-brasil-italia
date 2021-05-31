package br.udesc.esag.participactbrasil.activities.questionnaire;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import br.udesc.esag.participactbrasil.domain.persistence.ActionFlat;
import br.udesc.esag.participactbrasil.domain.persistence.Question;
import br.udesc.esag.participactbrasil.activities.questionnaire.QuestionnaireFragment;
import br.udesc.esag.participactbrasil.activities.questionnaire.QuestionnaireInfoFragment;

/**
 * Created by felipe on 15/05/2016.
 */
public class QuestionnairePageAdapter extends FragmentStatePagerAdapter {

    private ActionFlat actionFlat;
    private List<Question> questionList;
    public QuestionnairePageAdapter(FragmentManager fm, ActionFlat actionFlat) {
        super(fm);
        this.actionFlat = actionFlat;
        this.questionList = actionFlat.getQuestions();
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return QuestionnaireInfoFragment.newInstance(actionFlat);
        }
        return QuestionnaireFragment.newInstance(this.questionList.get(position-1));
    }

    @Override
    public int getCount() {
        return questionList.size()+1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0){
            return "INFO";
        }
        return "Questão "+position;
    }
}
