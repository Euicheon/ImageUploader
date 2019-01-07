package com.example.q.imageuploader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class getImageBackground extends AsyncTask<String, Void, Bitmap> {
    String Url;
    Bitmap bitmap;
    //ImageView gottenimg;
    public getImageBackground(String url) { Url = url; }

    public Bitmap doInBackground(String... params) {
        //gottenimg = (ImageView) findViewByld(R.id.gottenimg);l

        try {
            //ImageView i = (ImageView)findViewById(R.id.image);
            //String imageUrl = "http://143.248.140.106:2980/uploads" + "/"+ "movie-dictators-02.jpg";
            Log.d("Url is ",Url);
            bitmap = BitmapFactory.decodeStream((InputStream)new URL(Url).getContent());
            //gottenimg.setImageBitmap(bitmap);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;

    }

}
