package com.webber.simpleemail.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.webber.simpleemail.R;
import com.webber.simpleemail.bean.Attachment;

/**
 * @author Administrator
 *	gridview 附件适配器
 * @param <T>
 */
public class GridViewAdapter<T> extends BaseAdapter {

	private List<T> mList = new ArrayList<T>();
	private Activity mActivity;

	public GridViewAdapter(Activity mActivity) {
		super();
		this.mActivity = mActivity;
	}

	public List<T> getList() {
		return mList;
	}
	//追加数据
	@SuppressWarnings("unchecked")
	public void appendToList(Attachment infos) {
		if (infos == null) {
			return;
		}
		mList.add((T) infos);
		notifyDataSetChanged();
	}
	public List<T> getmList() {
		return mList;
	}

	//数据加掉第一条
	public void appendToTopList(List<T> list) {
		if (list == null) {
			return;
		}
		mList.addAll(0, list);
		notifyDataSetChanged();
	}
	//清楚某个位置数据
	public void clearPositionList(int position) {
		if (position >= 0 && position < mList.size()) {
			mList.remove(position);
			notifyDataSetChanged();
		}
	}
	//清除所有数据
	public void clear() {
		mList.clear();
		notifyDataSetChanged();
	}
	//控件句柄
	class Holder {
		ImageView imageView;
		TextView textView;
		TextView textView2;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		if (position > mList.size() - 1) {
			return null;
		}
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = getLayout(holder);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		Attachment affInfos = (Attachment) mList.get(position);
		holder.textView.setText(affInfos.getFileName());
		holder.textView2.setText(Attachment.convertStorage(affInfos.getFileSize()));
		Bitmap bitmap = getCategoryFromPath(affInfos.getFilePath());
		holder.imageView.setImageBitmap(bitmap);

		return convertView;
	}
	//item布局
	private LinearLayout getLayout(Holder holder) {
		LinearLayout layout = new LinearLayout(mActivity);
		layout.setOrientation(LinearLayout.VERTICAL);

		ImageView imageView = new ImageView(mActivity);
		holder.imageView = imageView;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, (int) (58 * 1.5));
		params.gravity = Gravity.CENTER;
		layout.addView(imageView, params);

		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, (int) (18 * 1.5));
		TextView textView = new TextView(mActivity);
		holder.textView = textView;
		textView.setTextSize(12);
		textView.setSingleLine(true);
		textView.setEllipsize(TruncateAt.MIDDLE);
		textView.setGravity(Gravity.CENTER);
		layout.addView(textView, params1);

		TextView textView2 = new TextView(mActivity);
		holder.textView2 = textView2;
		textView2.setGravity(Gravity.CENTER);
		textView2.setTextSize(12);
		layout.addView(textView2, params1);

		return layout;
	}

	private final int DEFAULT_WIDTH = 312;
	private final int DEFAULT_HEIGHT = 234;

	private int width;
	private int height;
	private int sampleSize = 1;

	public enum FileCategory {
		All, Music, Video, Picture, Theme, Doc,PPT,XSL,TXT,PDF, Zip, Apk, Custom, Other, Favorite
	}

	private String APK_EXT = "apk";
	private String THEME_EXT = "mtz";

	private String[] DOC_EXTS = new String[]{"doc","docx"};
	private String[] PPT_EXTS = new String[]{"ppt","pptx"}; 
	private String[] XSL_EXTS = new String[]{"XSL","XSLX"};
	private String[] TXT_EXTS = new String[]{"txt","log","ini","lrc"};
	private String PDF_EXTS = "pdf";
	
	private String[] ZIP_EXTS = new String[] { "zip", "rar" };
	private String[] VIDEO_EXTS = new String[] { "mp4", "wmv", "mpeg", "m4v",
			"3gp", "3gpp", "3g2", "3gpp2", "asf" };
	private String[] MUSIC_EXTS = new String[]{"mp3","wma","wav","ogg","ape","acc","amr"};
	private String[] PICTURE_EXTS = new String[] { "jpg", "jpeg", "gif", "png",
			"bmp", "wbmp" };

	public Bitmap getCategoryFromPath(String path) {
		Bitmap bitmap = BitmapFactory.decodeResource(mActivity.getResources(),
				R.drawable.default_fileicon);
		int dotPosition = path.lastIndexOf('.');
		if (dotPosition == -1)
			return bitmap;

		String ext = path.substring(dotPosition + 1, path.length());
		if (ext.equalsIgnoreCase(APK_EXT)) {
			BitmapDrawable bd = (BitmapDrawable) getApkIcon(mActivity, path);
			return bd.getBitmap();
		}

		if (ext.equalsIgnoreCase(THEME_EXT)) {
			return bitmap ;
		}
		if (ext.equalsIgnoreCase(PDF_EXTS)) {
			return bitmap = BitmapFactory.decodeResource(mActivity.getResources(),
					R.drawable.pdf);
		}
		if (matchExts(ext, DOC_EXTS)) {
			return bitmap = BitmapFactory.decodeResource(mActivity.getResources(),
					R.drawable.doc);
		}
		if (matchExts(ext, PPT_EXTS)) {
			return bitmap = BitmapFactory.decodeResource(mActivity.getResources(),
					R.drawable.ppt);
		}
		if (matchExts(ext, XSL_EXTS)) {
			return bitmap = BitmapFactory.decodeResource(mActivity.getResources(),
					R.drawable.xls);
		}
		if (matchExts(ext, TXT_EXTS)) {
			return bitmap = BitmapFactory.decodeResource(mActivity.getResources(),
					R.drawable.file_doc);
		}
		

		if (matchExts(ext, ZIP_EXTS)) {
			return bitmap = BitmapFactory.decodeResource(mActivity.getResources(),
					R.drawable.file_archive);
		}

		if (matchExts(ext, VIDEO_EXTS)) {
			return bitmap = BitmapFactory.decodeResource(mActivity.getResources(),
					R.drawable.file_video);
		}
		if (matchExts(ext, MUSIC_EXTS)) {
			return bitmap = BitmapFactory.decodeResource(mActivity.getResources(),
					R.drawable.file_audio);
		}

		if (matchExts(ext, PICTURE_EXTS)) {
			bitmap = getBitmap(path);
			return bitmap;
		}
		return bitmap;
	}

	private static boolean matchExts(String ext, String[] exts) {
		for (String ex : exts) {
			if (ex.equalsIgnoreCase(ext))
				return true;
		}
		return false;
	}

	private void getBitmapSize(String filePaths) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePaths, options);
		width = options.outWidth;
		height = options.outHeight;
	}

	private Bitmap getBitmap(String filePaths) {
		getBitmapSize(filePaths);
		while ((width / sampleSize > DEFAULT_WIDTH * 2)
				|| (height / sampleSize > DEFAULT_HEIGHT * 2)) {
			sampleSize *= 2;
		}

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = sampleSize;

		return BitmapFactory.decodeFile(filePaths, options);
	}

	/**
	 * 获取APK图标
	 * @param context
	 * @param path
	 * @return
	 */
	public static Drawable getApkIcon(Context context, String path) {
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(path,
				PackageManager.GET_ACTIVITIES);
		if (info != null) {
			ApplicationInfo appInfo = info.applicationInfo;
			try {
				return pm.getApplicationIcon(appInfo);
			} catch (OutOfMemoryError e) {
				Log.e("LOG_TAG", e.toString());
			}
		}
		return null;
	}
}
