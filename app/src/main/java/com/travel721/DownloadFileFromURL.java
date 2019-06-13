package com.travel721;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;

public class DownloadFileFromURL extends AsyncTask<String, String, Bitmap> {

    private Bitmap bmp;
    private final IOnFileDownloadedListener Listener;


    public DownloadFileFromURL(IOnFileDownloadedListener FDListener) {
        Listener = FDListener;
    }


    @Override
    protected Bitmap doInBackground(String... strings) {
        String urldisplay = strings[0];
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            bmp = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getLocalizedMessage());
            e.printStackTrace();
        }
        return bmp;
    }

    @Override
    protected void onPostExecute(Bitmap file_url) {
        if (this.Listener != null) this.Listener.onFileDownloaded(this.bmp);
    }
}