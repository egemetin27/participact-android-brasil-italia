package br.udesc.esag.participactbrasil.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import br.udesc.esag.participactbrasil.R;

/**
 * Created by felipe on 02/04/2016.
 */
public class PicturesListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<String> photosPathList;
    public PicturesListAdapter(Context context, List<String> photosList){
        this.context = context;
        this.photosPathList = photosList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return photosPathList.size();
    }

    @Override
    public Object getItem(int position) {
        return photosPathList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView==null){
            convertView = inflater.inflate(R.layout.list_item_picture, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imgViewPicture = (ImageView) convertView.findViewById(R.id.imgViewListItemPicture);

            ImageLoader.getInstance().displayImage("file:/"+photosPathList.get(position),viewHolder.imgViewPicture);

            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView imgViewPicture;
    }
}
