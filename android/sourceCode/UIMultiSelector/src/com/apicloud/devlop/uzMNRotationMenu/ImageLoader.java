package com.apicloud.devlop.uzMNRotationMenu;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class ImageLoader {

	private int mIndex;

	public void loadImage(ImageLoaderInterface imageLoaderInterface, int index,
			String imgUrl) {
		mIndex = index;
		new ImgLoaderTask(imageLoaderInterface, imgUrl).execute();
	}

	private class ImgLoaderTask extends AsyncTask<String, String, Bitmap> {

		private ImageLoaderInterface mImageLoaderInterface;
		private String mImgUrl;

		public ImgLoaderTask(ImageLoaderInterface mImageLoaderInterface,
				String mImgUrl) {
			this.mImageLoaderInterface = mImageLoaderInterface;
			this.mImgUrl = mImgUrl;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			URL url;
			try {
				url = new URL(mImgUrl);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				InputStream inputStream = conn.getInputStream();
				return BitmapFactory.decodeStream(inputStream);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			mImageLoaderInterface.onLoaderFinish(mIndex, result);
		}

	}

}
