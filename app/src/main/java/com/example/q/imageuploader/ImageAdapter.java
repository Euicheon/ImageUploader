package com.example.q.imageuploader;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private Uri[] uris;

    public ImageAdapter(Context c, String uris)
    {
        this.mContext = c;
        //this.uris = uris;
    }
    public int getCount() {
        return uris.length;
    }

    public Object getItem(int position) {
        return uris[position];
    }

    public long getItemId(int position) {
        return 0;
    }
    public Context getContext()
    {
        return mContext;
    }

    private RequestListener<Drawable> requestListener = new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            // todo log exception to central service or something like that
            e.printStackTrace();
            // important to return false so the error placeholder can be placed
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            // everything worked out, so probably nothing to do
            return false;
        }
    };

    // Create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // If it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        // Create glide request manager
        RequestManager requestManager = Glide.with(mContext);
        // Create request builder and load image.
        RequestBuilder requestBuilder = requestManager.load(uris[position]).listener(requestListener);
        requestBuilder = requestBuilder.apply(new RequestOptions().override(250, 250));
        // Show image into target imageview.
        requestBuilder.into(imageView);
        return imageView;
    }
}
