package br.udesc.esag.participactbrasil.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.domain.enums.TaskState;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlat;
import br.udesc.esag.participactbrasil.domain.persistence.TaskStatus;

/**
 * Created by felipe on 02/04/2016.
 */
public class CampaignsListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<TaskFlat> taskList;
    private DateTimeFormatter formatter;
    public CampaignsListAdapter(Context context, List<TaskFlat> taskList){
        this.context = context;
        this.taskList = taskList;
        this.inflater = LayoutInflater.from(context);
        this.formatter = DateTimeFormat.forPattern("dd/MM/YYYY HH:mm");
    }

    public void setTaskList(List<TaskFlat> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return taskList.size();
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

        TaskFlat taskFlat = taskList.get(position);
        TaskStatus status = taskFlat.getTaskStatus();
        convertView = inflater.inflate(R.layout.list_item_campaign, parent, false);

        ImageView imageViewIcon = (ImageView) convertView.findViewById(R.id.imgViewIcon);

        if(status.getState().equals(TaskState.RUNNING) || status.getState().equals(TaskState.ACCEPTED)) {
            imageViewIcon.setImageResource(R.drawable.ic_running2);
        }else if (status.getState().equals(TaskState.AVAILABLE) || status.getState().equals(TaskState.UNKNOWN)){
            imageViewIcon.setImageResource(R.drawable.ic_list);
        }else if(status.getState().equals(TaskState.REJECTED)){
            imageViewIcon.setImageResource(R.drawable.ic_rejected);
        }else if(status.getState().equals(TaskState.ERROR) || status.getState().equals(TaskState.FAILED)){
            imageViewIcon.setImageResource(R.drawable.ic_error);
        }else{
            imageViewIcon.setImageResource(R.drawable.ic_done_circle);
        }

        TextView txtViewTitle = (TextView) convertView.findViewById(R.id.txtViewTitle);
        txtViewTitle.setText(taskFlat.getName());

        TextView txtViewDescription = (TextView) convertView.findViewById(R.id.txtViewDescription);
        txtViewDescription.setText(taskFlat.getDescription());

        String expirationDate = context.getResources().getString(R.string.expires_on) +" "+ formatter.print(taskFlat.getDeadline());
        TextView txtViewDate = (TextView) convertView.findViewById(R.id.txtViewCampaignDate);
        txtViewDate.setText(expirationDate);

        return convertView;
    }
}
