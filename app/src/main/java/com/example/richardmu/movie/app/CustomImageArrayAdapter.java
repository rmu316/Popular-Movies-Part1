package com.example.richardmu.movie.app;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Richard Mu on 5/4/2016.
 */
public class CustomImageArrayAdapter extends ArrayAdapter<String> {
    private static final String LOG_TAG = CustomImageArrayAdapter.class.getSimpleName();
    private Context context;
    private int resourceId;
    ArrayList<String> imageUrls;

    public CustomImageArrayAdapter(Activity context, int resource, ArrayList<String> urls) {
        super(context, resource, urls);
        this.context = context;
        this.resourceId = resource;
        this.imageUrls = urls;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(resourceId, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.grid_item_poster_imageview);
        String url = getItem(position);
        Picasso.with(context).load(url).placeholder(R.drawable.loading_indicator).error(R.drawable.not_found).into(imageView);
        return convertView;
    }

    public void setMovieData(ArrayList<String> imageUrls) {
        this.imageUrls.addAll(imageUrls);
        notifyDataSetChanged();
    }
}
