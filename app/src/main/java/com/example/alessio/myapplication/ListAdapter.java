package com.example.alessio.myapplication;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Random;


/**
 * Created by Alessio on 17/04/2016.
 */
public class ListAdapter extends ArrayAdapter<ObjectItem> {

    Context mContext;
    int layoutResourceId;
    ObjectItem data[] = null;

    public ListAdapter(Context mContext, int layoutResourceId, ObjectItem[] data){
        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        ObjectItem objectItem = data[position];

        TextView view1 = (TextView) convertView.findViewById(R.id.nomeEvento);
        TextView view2 = (TextView) convertView.findViewById(R.id.descrizioneEvento);
        ImageView myimg = (ImageView) convertView.findViewById(R.id.SampleDocumentImage);
        Random rnd = new Random();
        int color = rnd.nextInt(5 - 1 + 1) + 1;

        if (objectItem.nome != null){
            view1.setText(objectItem.nome);
            view1.setTag(objectItem.id);
            view2.setText("lat: " + objectItem.latitudine + "; long: " + objectItem.longitudine);

            switch(color){
                case 1:
                    myimg.setImageResource(R.drawable.location1a);
                    break;
                case 2:
                    myimg.setImageResource(R.drawable.location1b);
                    break;
                case 3:
                    myimg.setImageResource(R.drawable.location1g);
                    break;
                case 4:
                    myimg.setImageResource(R.drawable.location1r);
                    break;
                case 5:
                    myimg.setImageResource(R.drawable.location1v);
                    break;
            }
        }
        return convertView;

    }
}
