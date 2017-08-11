package com.webber.simpleemail.activity;

import java.util.ArrayList;
import java.util.List;

import com.webber.simpleemail.R;
import com.webber.simpleemail.app.MyApplication;
import com.webber.simpleemail.bean.EmailCaogao;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MailCaogaoxiangActivity extends Activity {
	private ListView lv;
	private List<EmailCaogao> allcaogaos;
	private MyAdapter adapter;
	private Uri uri=Uri.parse("content://com.caogaoxiangprovider");
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.email_caogaoxiang);
		allcaogaos=getAllcaogaos();
		lv=(ListView) findViewById(R.id.caogaoxiang);
		
	    adapter=new MyAdapter();
		lv.setAdapter(adapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				EmailCaogao caogao=allcaogaos.get(position);
				Intent intent=new Intent(MailCaogaoxiangActivity.this,MailEditActivity.class);
				intent.putExtra("mailid", caogao.getId());
				startActivity(intent);
				finish();
				
			}
			
		});
	}
	
	/**
	 * 获取所有草稿
	 * @return
	 */
	private List<EmailCaogao> getAllcaogaos(){
		List<EmailCaogao> caogaos=new ArrayList<EmailCaogao>();
		Cursor c=getContentResolver().query(uri, null, "mailfrom=?", new String[]{MyApplication.info.getUserName()}, null);
		while(c.moveToNext()){
			EmailCaogao caogao=new EmailCaogao(c.getInt(0),c.getString(1),c.getString(2),
					           c.getString(3),c.getString(4));
			caogaos.add(caogao);	
		}
		return caogaos;
	};
	
	
	/**
	 * 适配器
	 * @author Administrator
	 *
	 */
	private class MyAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return allcaogaos.size();
		}

		@Override
		public Object getItem(int position) {
			return allcaogaos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View item=View.inflate(MailCaogaoxiangActivity.this, R.layout.email_caogaoxiang_item, null);
			TextView mailto=(TextView) item.findViewById(R.id.tv_mailto);
			TextView mailsubject=(TextView) item.findViewById(R.id.tv_mailsubject);
			
			EmailCaogao caogao=allcaogaos.get(position);
			mailto.setText(caogao.getMailto());
			mailsubject.setText(caogao.getSubject());
			
			return item;
		}
		
	}
	
	/**
	 * 返回
	 * @param v
	 */
	public void back(View v){
		finish();
	}

}
