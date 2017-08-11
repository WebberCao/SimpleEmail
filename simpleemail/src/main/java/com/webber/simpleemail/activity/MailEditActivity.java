package com.webber.simpleemail.activity;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Session;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images.ImageColumns;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.webber.simpleemail.R;
import com.webber.simpleemail.adapter.GridViewAdapter;
import com.webber.simpleemail.app.MyApplication;
import com.webber.simpleemail.bean.Attachment;
import com.webber.simpleemail.bean.MailInfo;
import com.webber.simpleemail.utils.HttpUtil;

public class MailEditActivity extends Activity implements OnClickListener {
	private EditText mail_to;
	private EditText mail_from;
	private EditText mail_topic;
	private EditText mail_content;

	private Button send;
	private ImageButton add_lianxiren;
	private ImageButton attachment;
	private GridView gridView;
	private GridViewAdapter<Attachment> adapter = null;
	private int mailid=-1;

	 private static final int SUCCESS = 1;
	 private static final int FAILED = -1;
	 private boolean isCaogaoxiang=true;
	    private ProgressDialog dialog;
	    HttpUtil util=new HttpUtil();

		private Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case SUCCESS:
					dialog.cancel();
					//发件成功不存入草稿箱
					isCaogaoxiang=false;
					//判断邮件是否来自草稿箱那么可以从草稿箱删除了
					if(mailid>0){
						Uri uri=Uri.parse("content://com.caogaoxiangprovider");
						getContentResolver().delete(uri, "id=?", new String[]{mailid+""});
						uri=Uri.parse("content://com.attachmentprovider");
						getContentResolver().delete(uri, "mailid=?", new String[]{mailid+""});
						//返回草稿箱
						Toast.makeText(getApplicationContext(), "邮件发送成功", Toast.LENGTH_SHORT).show();
						Intent intent=new Intent(MailEditActivity.this, MailCaogaoxiangActivity.class);
						startActivity(intent);
						finish();
					}else{
						Toast.makeText(getApplicationContext(), "邮件发送成功", Toast.LENGTH_SHORT).show();
						//清空之前填写的数据
						mail_from.getText().clear();
						mail_to.getText().clear();
						mail_topic.getText().clear();
						mail_content.getText().clear();
						adapter=new GridViewAdapter<Attachment>(MailEditActivity.this);
					}

					break;
				case FAILED:
					dialog.cancel();
					//发件失败是否存入草稿箱
					isCaogaoxiang=true;
					Toast.makeText(getApplicationContext(), "邮件发送失败", Toast.LENGTH_SHORT).show();
					break;
				}
				super.handleMessage(msg);
			}

	    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.email_writer);
		init();
	}

	/**
	 * 初始化
	 */
	private void init(){
		mail_to=(EditText) findViewById(R.id.mail_to);
		mail_from=(EditText) findViewById(R.id.mail_from);
		mail_topic=(EditText) findViewById(R.id.mail_topic);
		mail_content=(EditText) findViewById(R.id.content);
		send=(Button) findViewById(R.id.send);
		attachment=(ImageButton) findViewById(R.id.add_att);
		add_lianxiren=(ImageButton) findViewById(R.id.add_lianxiren);
		gridView=(GridView) findViewById(R.id.pre_view);

		mail_from.setText(MyApplication.info.getUserName());
		send.setOnClickListener(this);
		attachment.setOnClickListener(this);
		add_lianxiren.setOnClickListener(this);

		adapter = new GridViewAdapter<Attachment>(this);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new MyOnItemClickListener());

		//判断是否从草稿箱来的
		mailid=getIntent().getIntExtra("mailid", -1);
		if(mailid>-1){
			Uri uri=Uri.parse("content://com.caogaoxiangprovider");
			Cursor c=getContentResolver().query(uri, null, "mailfrom=? and id=?", new String[]{MyApplication.info.getUserName(),mailid+""}, null);
			if(c.moveToNext()){
				mail_to.setText(c.getString(2));
				mail_topic.setText(c.getString(3));
				mail_content.setText(c.getString(4));
			}

			uri=Uri.parse("content://com.attachmentprovider");
			c=getContentResolver().query(uri, null, "mailid=?", new String[]{mailid+""}, null);
			List<Attachment> attachments=new ArrayList<Attachment>();
			while(c.moveToNext()){
				Attachment att=new Attachment(c.getString(2),c.getString(1),c.getLong(3));
				attachments.add(att);
			}

			//显示附件
			if(attachments.size()>0){
				for(Attachment affInfos:attachments){
					adapter.appendToList(affInfos);
					int a = adapter.getList().size();
					int count = (int) Math.ceil(a / 4.0);
					gridView.setLayoutParams(new LayoutParams(
							LayoutParams.MATCH_PARENT,
							(int) (94 * 1.5 * count)));
				}
			}

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.send:
			sendMail();
			break;
		case R.id.add_att:
			addAttachment();
			break;
		case R.id.add_lianxiren:
			Intent intent=new Intent(MailEditActivity.this,MailAddConstact.class);
			startActivityForResult(intent, 2);
			break;
		}

	};

	/**
	 * 添加附件
	 */
	private void addAttachment() {
/*		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("file/");
		startActivityForResult(intent, 1);*/
	    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
	    intent.setType("*/*");
	    intent.addCategory(Intent.CATEGORY_OPENABLE);

	    try {
	        startActivityForResult(Intent.createChooser(intent, "请选择附件"), 1);
	    } catch (android.content.ActivityNotFoundException ex) {
	        Toast.makeText(this, "请安装文件管理器",  Toast.LENGTH_SHORT).show();
	    }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 1:
				Uri uri = null;
				if (data != null) {
					uri = data.getData();
				}

			    String scheme = uri.getScheme();
			    String path = null;
			    if (scheme == null || ContentResolver.SCHEME_FILE.equals(scheme)){
			    	path = uri.getPath();
				}else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
			        Cursor cursor = getContentResolver().query(uri, new String[] {ImageColumns.DATA }, null, null, null);
			        if (null != cursor) {
			            if (cursor.moveToFirst()) {
			                int index = cursor.getColumnIndex( ImageColumns.DATA );
			                if ( index > -1 ) {
			                    path = cursor.getString( index );
			                }
			            }
			            cursor.close();
			        }
			    }

//				String path = uri.getPath();
				Attachment affInfos = Attachment.GetFileInfo(path);
				adapter.appendToList(affInfos);
				int a = adapter.getList().size();
				int count = (int) Math.ceil(a / 4.0);
				gridView.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT,
						(int) (94 * 1.5 * count)));
				break;
			}
		}

		/**
		 * 多个联系人
		 */
		if(requestCode==2){
			List<String> chooseUsers=data.getStringArrayListExtra("chooseUsers");
			StringBuilder str=new StringBuilder();
			for(int i=0;i<chooseUsers.size();i++){
				if(i==chooseUsers.size()-1){
					str.append("<"+chooseUsers.get(i)+">");
				}else{
					str.append("<"+chooseUsers.get(i)+">,");
				}
			}
			mail_to.setText(str.toString());

		}
	}

	/**
	 * 设置邮件数据
	 */
	private void sendMail(){
		final MailInfo info = new MailInfo();
		info.setAttachmentInfos(adapter.getList());
		info.setFromAddress(mail_from.getText().toString().trim());
		info.setSubject(mail_topic.getText().toString().trim());
		info.setContent(mail_content.getText().toString().trim());
/*		MyApplication.info.setAttachmentInfos(adapter.getList());
		MyApplication.info.setFromAddress(mail_from.getText().toString().trim());
		MyApplication.info.setSubject(mail_topic.getText().toString().trim());
		MyApplication.info.setContent(mail_content.getText().toString().trim());*/
		//收件人
		String str=mail_to.getText().toString().trim();
		String []recevers=str.split(",");
		for(int i=0;i<recevers.length;i++){
			if(recevers[i].startsWith("<")&&recevers[i].endsWith(">")){
				recevers[i]=recevers[i].substring(recevers[i].lastIndexOf("<")+1, recevers[i].lastIndexOf(">"));
			}
		}
		/*MyApplication.info.setReceivers(recevers);*/
		info.setReceivers(recevers);


		//发送邮件
		dialog=new ProgressDialog(this);
		dialog.setMessage("正在发送");
		dialog.show();

		/**
		 * 发送
		 */
		new Thread(){
			@Override
			public void run() {
//				boolean flag=util.sendTextMail(MyApplication.info, MyApplication.session);
				final Session sendMailSession = MyApplication.session;
				boolean flag=util.sendTextMail(info, sendMailSession);

				Message msg=new Message();
				if(flag){
					msg.what=SUCCESS;
					handler.sendMessage(msg);
				}else{
					msg.what=FAILED;
					handler.sendMessage(msg);
				}
			}

		}.start();

	}

	/**
	 * 点击事件
	 * @author Administrator
	 *
	 */
	private class MyOnItemClickListener implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?
				> arg0, View arg1,
				final int arg2, long arg3) {
			Attachment infos = (Attachment) adapter.getItem(arg2);
			Builder builder = new Builder(
					MailEditActivity.this);
			builder.setTitle(infos.getFileName());
			builder.setMessage("是否删除当前附件");
			builder.setNegativeButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							adapter.clearPositionList(arg2);
							int a = adapter.getList().size();
							int count = (int) Math.ceil(a / 4.0);
							gridView.setLayoutParams(new LayoutParams(
									LayoutParams.MATCH_PARENT,
									(int) (94 * 1.5 * count)));
						}
					});
			builder.setPositiveButton("取消",null);
			builder.create().show();
		}
	}

	/**
	 * 返回
	 * @param v
	 */
	public void back(View v){
		if(isCaogaoxiang&&mail_to.getText().toString().trim()!=null){
			Builder builder=new Builder(MailEditActivity.this);
			builder.setMessage("是否存入草稿箱");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//保存至数据库
					saveToCaogaoxiang();
				}

			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}

			});
			builder.show();
		}else{
			finish();
		}

	}

	/**
	 * 返回按钮
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(isCaogaoxiang&&mail_to.getText().toString().trim()!=null){
				Builder builder=new Builder(MailEditActivity.this);
				builder.setMessage("是否存入草稿箱");
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//保存至数据库
						saveToCaogaoxiang();
					}

				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}

				});
				builder.show();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 保存至草稿箱
	 */
	private void saveToCaogaoxiang(){
		Uri uri=Uri.parse("content://com.caogaoxiangprovider");
		ContentValues values=new ContentValues();
		values.put("mailfrom", MyApplication.info.getUserName());
		values.put("mailto", mail_to.getText().toString().trim());
		values.put("subject", mail_topic.getText().toString().trim());
		values.put("content", mail_content.getText().toString().trim());
		String url=getContentResolver().insert(uri, values).toString();
		int id=Integer.parseInt(url.substring(url.length()-1));
		//保存附件
		if(adapter.getList().size()>0){
			Uri att_uri=Uri.parse("content://com.attachmentprovider");
			List<Attachment> attachments=adapter.getmList();
			values.clear();
			for(int i=0;i<attachments.size();i++){
				Attachment att=attachments.get(i);
				values.put("filename", att.getFileName());
				values.put("filepath", att.getFilePath());
				values.put("filesize", att.getFileSize());
				values.put("mailid", id);
				getContentResolver().insert(att_uri, values);
			}
		}
		Toast.makeText(MailEditActivity.this, "保存至草稿箱", Toast.LENGTH_SHORT).show();
	};

}
