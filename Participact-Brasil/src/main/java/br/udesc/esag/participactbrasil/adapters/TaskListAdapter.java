package br.udesc.esag.participactbrasil.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.domain.persistence.ActionFlat;
import br.udesc.esag.participactbrasil.domain.persistence.ActionType;

/**
 * Created by felipe on 02/04/2016.
 */
public class TaskListAdapter extends BaseAdapter {

    private List<ActionFlat> actionList;
    private LayoutInflater inflater;

    public TaskListAdapter(Context context, ArrayList<ActionFlat> actionList) {
        this.actionList = actionList;
        this.inflater = LayoutInflater.from(context);
    }

    public void clear() {
        actionList.clear();
    }

    @Override
    public int getCount() {
        return actionList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ActionFlat action = actionList.get(position);
        convertView = inflater.inflate(R.layout.list_item_task, parent, false);
        convertView.setTag(action);

        //------ TITLE AND DESCRIPTION
        TextView txtTitle = (TextView) convertView.findViewById(R.id.txtViewTitle);
        TextView txtDescription = (TextView) convertView.findViewById(R.id.txtViewDescription);

        if (action.getTitle() != null) {
            txtTitle.setText(action.getTitle());
        } else {
            txtTitle.setText(action.getName());
            if (action.getType().equals(ActionType.PHOTO)) {
                int total = action.getNumeric_threshold();
                int step = total - action.getTask().getTaskStatus().getRemainingPhotoPerAction(action.getId());
                txtTitle.setText(action.getName() + " (" + step + " de " + total + ")");
            }
        }
        txtDescription.setText(action.getDescription());

        //------ ICON
        ImageView imgViewIcon = (ImageView) convertView.findViewById(R.id.imgViewIcon);
        if (action.getType().equals(ActionType.PHOTO)) {
            if(action.getTask().getTaskStatus().getRemainingPhotoPerAction(action.getId()) <= 0){
                imgViewIcon.setImageResource(R.drawable.ic_done_circle);
            }else{
                imgViewIcon.setImageResource(R.drawable.ic_camera);
            }
        } else if (action.getType().equals(ActionType.QUESTIONNAIRE)) {
            if(action.getTask().getTaskStatus().isQuestionnaireCompleted(action.getId())){
                imgViewIcon.setImageResource(R.drawable.ic_done_circle);
            }else{
                imgViewIcon.setImageResource(R.drawable.ic_write);
            }
        }
        return convertView;
    }

}
