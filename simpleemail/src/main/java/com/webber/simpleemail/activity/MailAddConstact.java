package com.webber.simpleemail.activity;

import java.util.ArrayList;
import java.util.List;

import com.webber.simpleemail.R;
import com.webber.simpleemail.app.MyApplication;
import com.webber.simpleemail.bean.EmailUsers;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class MailAddConstact extends Activity {
	private ListView lv;
	private MyAdapter adapter;
	private List<EmailUsers> list;
	private List<String> chooseUsers=new ArrayList<String>();
	private Uri uri=Uri.parse("content://com.emailconstantprovider");
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.email_addconstact);
		//获取所有联系人
		list=getAllConstacts();
		
		lv=(ListView) findViewById(R.id.show_constant);
		adapter=new MyAdapter();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				   EmailUsers user=(EmailUsers) parent.getItemAtPosition(position);
				   CheckBox ck_box=(CheckBox) view.findViewById(R.id.ck_box);
				   if(chooseUsers.contains(user.getAddress())){
					   chooseUsers.remove(user.getAddress());
					   ck_box.setChecked(false);
				   }else{
					   chooseUsers.add(user.getAddress());
					   ck_box.setChecked(true);
				   }
				//Toast.makeText(MailAddConstact.this, position+"", Toast.LENGTH_SHORT).show();
			}
			
		});
		
	}
	
	/**
	 * 获取所有联系人
	 * @return
	 */
	private List<EmailUsers> getAllConstacts(){
		List<EmailUsers> users=new ArrayList<EmailUsers>();
		Cursor c=getContentResolver().query(uri, null, "mailfrom=?", new String[]{MyApplication.info.getUserName()}, null);
		while(c.moveToNext()){
			EmailUsers user=new EmailUsers(c.getInt(0), c.getString(2), c.getString(3));
			users.add(user);
		}
		return users;
	}
	
	/**
	 * 适配器
	 * @author Administrator
	 *
	 */
	private class MyAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View item=View.inflate(MailAddConstact.this, R.layout.email_addconstact_item, null);
			TextView name=(TextView) item.findViewById(R.id.add_name);
			TextView address=(TextView) item.findViewById(R.id.add_address);
			CheckBox ck_box=(CheckBox) item.findViewById(R.id.ck_box);
			
			EmailUsers user=list.get(position);
			name.setText(user.getName());
			address.setText(user.getAddress());
			
            if(chooseUsers.contains(user.getAddress())){
				ck_box.setChecked(true);
			 }else{
				 ck_box.setChecked(false);
			 }
			return item;
		}
		
	}
	
	/**
	 * 返回
	 * @param v
	 */
	public void back(View v){
		Intent data=new Intent();
		data.putStringArrayListExtra("chooseUsers", (ArrayList<String>) chooseUsers);
		setResult(2, data);
		finish();
	}
	
	/**
	 * 确定
	 * @param v
	 */
	public void choose(View v){
		Intent data=new Intent();
		data.putStringArrayListExtra("chooseUsers", (ArrayList<String>) chooseUsers);
		setResult(2, data);
		finish();
	}
}
