package com.sivvar.ui.grid;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.sivvar.AppUtil;
import com.sivvar.LocalBundleData;
import com.sivvar.objects.LogoDownloading;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	ImageView bmImage;
	ProgressBar loadingImage;
	boolean needGrayOut = false;
	String urldisplay;
	LogoDownloading logoDownloading;

	public DownloadImageTask(ImageView bmImage, ProgressBar progressBar, boolean grayOut, LogoDownloading logoDownloading) {
		this.bmImage = bmImage;
		this.loadingImage = progressBar;
		this.needGrayOut = grayOut;
		this.logoDownloading = logoDownloading;
	}
	
	public DownloadImageTask() {}

	protected Bitmap doInBackground(String... urls) {
		urldisplay = urls[0];
		Bitmap mIcon11 = null;
		try {
			InputStream in = new java.net.URL(urldisplay).openStream();
			mIcon11 = BitmapFactory.decodeStream(in);
		} catch (Exception e) {
			Log.e("Error", e.getMessage());
			e.printStackTrace();
		}
		return mIcon11;
	}

	protected void onPostExecute(Bitmap result) {
		LocalBundleData.cachedImages.put(urldisplay, result);
		if (needGrayOut) {
			result = AppUtil.toGrayscale(result);
		}
		if (bmImage != null) {
			bmImage.setImageBitmap(result);
		}
		if (loadingImage != null) {
			loadingImage.setVisibility(View.GONE);
		}
		logoDownloading.setLogoLoading(false);
	}
}
