package com.apicloud.devlop.FNImageClip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.text.TextUtils;

import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import com.uzmap.pkg.uzkit.UZUtility;

public class SaveUtil {
	private UZModuleContext mModuleContext;
	private boolean isSaveToAlbum;
	private Context mContext;
	private String mImgPath;
	private String mImgName;
	private double mQuality;
	private UzImgClip mMoudle;
	private ImgClipOpen mImgClipOpen;

	public SaveUtil(UZModuleContext mModuleContext, Context context,
			UzImgClip moudle, ImgClipOpen imgClipOpen, double quality) {
		this.mModuleContext = mModuleContext;
		this.mContext = context;
		this.mMoudle = moudle;
		this.mImgClipOpen = imgClipOpen;
		isSaveToAlbum = mModuleContext.optBoolean("copyToAlbum", false);
		mQuality = quality;
		initImgPath();
	}

	private boolean existSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else
			return false;
	}

	private void initImgPath() {
		File file = null;
		if (existSDCard()) {
			file = mContext.getExternalCacheDir();
		} else {
			file = mContext.getCacheDir();
		}
		String path = file.getAbsolutePath();
		String savePath = mModuleContext.optString("destPath");
		if (mModuleContext.isNull("destPath")) {
			savePath = mImgClipOpen.getSavePath();
		}
		if (savePath.trim().length() > 0) {
			mImgPath = mMoudle.makeRealPath(savePath);
			mImgName = mImgPath.substring(mImgPath.lastIndexOf('/') + 1);
			mImgPath = mImgPath.substring(0, mImgPath.lastIndexOf('/') + 1);
			if (mImgName.trim().length() <= 0) {
				mImgName = System.currentTimeMillis() + ".jpg";
			}
		} else {
			mImgPath = path;
			mImgName = System.currentTimeMillis() + ".jpg";
		}
	}

	private String mAlbumPath;

	public void saveOutput(Bitmap croppedImage) {
		if (isSaveToAlbum) {
			String imageName = System.currentTimeMillis() + ".png";
			File mediaStorageDir = new File(
					Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
							+ "/Camera");
			if (!mediaStorageDir.exists()) {
				mediaStorageDir.mkdirs();
			}
			saveImage(croppedImage, mModuleContext, mediaStorageDir, imageName,
					mQuality, false);
		}
		if (!TextUtils.isEmpty(mImgPath) && !TextUtils.isEmpty(mImgName)) {
			File path = new File(mImgPath);
			if (!path.exists()) {
				path.mkdirs();
			}
			saveImage(croppedImage, mModuleContext, path, mImgName, mQuality,
					true);
		}
	}

	public void saveImage(Bitmap bitmap, UZModuleContext context, File path,
			String imgName, double quality, boolean callback) {
		FileOutputStream outStream;
		String callBackPath = null;
		int intQuality = (int) (quality * 100);
		try {
			if (imgName != null && imgName.endsWith(".jpg")) {
				File savePath = new File(path, imgName);
				callBackPath = savePath.getAbsolutePath();
				outStream = new FileOutputStream(savePath);
				bitmap.compress(CompressFormat.JPEG, intQuality, outStream);

			} else if (imgName != null && imgName.endsWith(".png")) {
				File savePath = new File(path, imgName);
				callBackPath = savePath.getAbsolutePath();
				outStream = new FileOutputStream(savePath);
				bitmap.compress(CompressFormat.PNG, intQuality, outStream);

			} else {
				if (imgName.endsWith(".")) {
					imgName += "png";
				} else {
					imgName += ".png";
				}
				File savePath = new File(path, imgName);
				callBackPath = savePath.getAbsolutePath();
				outStream = new FileOutputStream(savePath);
				bitmap.compress(CompressFormat.PNG, intQuality, outStream);
			}

			if (outStream != null) {
				outStream.close();
			}

			if (callback) {
				JSONObject retObj = new JSONObject();
				try {
					retObj.put("destPath", callBackPath);
					if (mAlbumPath != null)
						retObj.put("albumPath", mAlbumPath);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				context.success(retObj, false);
			} else {
				mAlbumPath = callBackPath;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
