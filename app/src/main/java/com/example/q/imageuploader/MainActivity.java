package com.example.q.imageuploader;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import org.apache.commons.io.FilenameUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.example.q.imageuploader.R;
import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.BasicNameValuePair;
import com.koushikdutta.async.http.NameValuePair;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.concurrent.Future;
import com.mongodb.ServerAddress;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import org.bson.Document;
import java.util.Arrays;
import com.mongodb.Block;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPatch;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;


public class MainActivity extends Activity {
    Button imgsel,upload,getimg;
    ImageView img,gottenimg;
    String path;
    public String file_name;
    //MongoClient mongoClient = MongoClients.create("mongodb://143.248.140.106:2580");
    public File f;
    JSONObject json = new JSONObject();
    List<JSONObject> linkerList = new ArrayList<>();
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (ImageView)findViewById(R.id.img);
        gottenimg = (ImageView)findViewById(R.id.gottenimg);
        Ion.getDefault(this).configure().setLogging("ion-sample", Log.DEBUG);
        imgsel = (Button)findViewById(R.id.selimg);
        getimg = (Button)findViewById(R.id.getimg);
        upload =(Button)findViewById(R.id.uploadimg);
        upload.setVisibility(View.INVISIBLE);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                f = new File(path);
                file_name = f.getName();
                Log.d("file name is", file_name);
                // 파일이름 : file_name

                // 보낼 데이터를 Json 파일로 만들기
                try {
                    json.put("value",file_name);
                    json.put("propName","photo");
                    linkerList.add(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    //http에 넣을 수 있는 형식으로 만들기
                    StringEntity json_string = new StringEntity(linkerList.toString());
                    //httprequestclass 로 보내서 실행시키기
                    new httpRequsetClass(json_string).execute();

                } catch (Exception e) {
                    e.printStackTrace();
                }


                Future uploading = Ion.with(MainActivity.this)
                        .load("http://143.248.140.106:2980/upload")
                        .setMultipartFile("image", f)
                        .asString()
                        .withResponse()
                        .setCallback(new FutureCallback<Response<String>>() {
                            @Override
                            public void onCompleted(Exception e, Response<String> result) {
                                try {
                                    JSONObject jobj = new JSONObject(result.getResult());
                                    Toast.makeText(getApplicationContext(), jobj.getString("response"), Toast.LENGTH_SHORT).show();

                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }

                            }
                        });
            }

        });

        imgsel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fintent = new Intent(Intent.ACTION_GET_CONTENT);
                fintent.setType("image/jpeg");
                try {
                    startActivityForResult(fintent, 100);
                } catch (ActivityNotFoundException e) {

                }
            }
        });
        getimg.setOnClickListener(new View.OnClickListener() {
            Bitmap bitmap;
            String url;
            @Override
            public void onClick(View v) {
                /*try {
                    Log.d("GET", "is now loading");
                    url = "http://143.248.140.106:2980/uploads" + "/"+ "movie-dictators-02.jpg";
                    //String url = new StringEntity(linkerList.toString());
                    bitmap = new getImageBackground(url).doInBackground();
                    //gottenimg.setImageBitmap(bitmap);
                } catch (Exception e){
                    e.printStackTrace();
                }*/
                String imageUrl = "http://143.248.140.106:2980/uploads" + "/"+ "movie-dictators-02.jpg";
                ImageAdapter imageAdapter = new ImageAdapter(getApplicationContext(),url);
                //ImageView imageView = (ImageView) findViewById(R.id.full_image_view);
                //ImageView imageView = new ImageView(this);
                RequestManager requestManager = Glide.with(imageAdapter.getContext());
                // Create request builder and load image.
                RequestBuilder requestBuilder = requestManager.load(imageUrl);
                //requestBuilder = requestBuilder.apply(new RequestOptions().override(250, 250));
                // Show image into target imageview.
                requestBuilder.into(gottenimg);
                gottenimg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                //images.add(gottenimg);
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    path = getRealPathFromURI(this,data.getData());
                    Log.d(" Real Path : ", path);
                    img.setImageURI(data.getData());
                    upload.setVisibility(View.VISIBLE);
                }
        }
    }
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


}