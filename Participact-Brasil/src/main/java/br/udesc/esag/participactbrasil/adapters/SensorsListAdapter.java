package br.udesc.esag.participactbrasil.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.most.MoSTApplication;

import java.util.ArrayList;

import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.domain.enums.SensingActionEnum;
import br.udesc.esag.participactbrasil.domain.enums.TaskState;
import br.udesc.esag.participactbrasil.domain.persistence.ActionFlat;

/**
 * Created by felipe on 02/04/2016.
 */
public class SensorsListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<ActionFlat> actions;
    public SensorsListAdapter(Context context, ArrayList<ActionFlat> actions){
        this.actions = actions;
        this.inflater = LayoutInflater.from(context);
    }

    public void setActions(ArrayList<ActionFlat> actions) {
        this.actions = actions;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return actions.size();
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
        ViewHolder viewHolder;

        ActionFlat actionFlat = actions.get(position);
        if(convertView==null){
            convertView = inflater.inflate(R.layout.list_item_sensor, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.imgViewIconOn =  convertView.findViewById(R.id.iconStatusOn);
            viewHolder.imgViewIconOff = convertView.findViewById(R.id.iconStatusOff);
            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.txtViewSensorTitle);
            viewHolder.txtDescription = (TextView) convertView.findViewById(R.id.txtViewSensorDescription);

            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

//        if(actionFlat.getTask().getTaskStatus().getState() == TaskState.RUNNING
//                || actionFlat.getTask().getTaskStatus().getState() == TaskState.RUNNING_BUT_NOT_EXEC ) {
        if (MoSTApplication.getInstance().isPipelineActive(actionFlat.getInput_type())) {
            viewHolder.imgViewIconOn.setVisibility(View.VISIBLE);
            viewHolder.imgViewIconOff.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.imgViewIconOn.setVisibility(View.INVISIBLE);
            viewHolder.imgViewIconOff.setVisibility(View.VISIBLE);
        }


        if(actionFlat.getTitle() != null) {
            viewHolder.txtDescription.setText(actionFlat.getTitle());
        }else if(actionFlat.getName() != null){
            viewHolder.txtDescription.setText(actionFlat.getName());
        }else{
            viewHolder.txtDescription.setText(actionFlat.getType().toString());
        }

        viewHolder.txtTitle.setText(SensingActionEnum.Type.fromIntToHumanReadable(actionFlat.getInput_type()));

        return convertView;
    }

    static class ViewHolder {
        View imgViewIconOff;
        View imgViewIconOn;
        TextView txtTitle;
        TextView txtDescription;
    }
}
