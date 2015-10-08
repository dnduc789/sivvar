package com.sivvar.ui.grid;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.sivvar.AppUtil;
import com.sivvar.LocalBundleData;

public class AssignImageTask extends AsyncTask<String, Void, Bitmap> {
	ImageView bmImage;
	ProgressBar loadingImage;
	boolean needGrayOut = false;
	String urldisplay;

	public AssignImageTask(ImageView bmImage, ProgressBar progressBar, boolean grayOut) {
		this.bmImage = bmImage;
		this.loadingImage = progressBar;
		this.needGrayOut = grayOut;
	}

	protected Bitmap doInBackground(String... urls) {
		urldisplay = urls[0];
		return LocalBundleData.cachedImages.get(urldisplay);
	}

	protected void onPostExecute(Bitmap result) {
		if (result == null) {
			return;
		}
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
	}
}
