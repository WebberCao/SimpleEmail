package com.webber.simpleemail.activity;
import com.webber.simpleemail.R;
import com.webber.simpleemail.app.MyApplication;
import com.webber.simpleemail.utils.EmailFormatUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.Toast;

public class HomeActivity extends Activity {
	private ExpandableListView expendView;
	private int []group_click=new int[5];
	private long mExitTime=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		final MyExpendAdapter adapter=new MyExpendAdapter();
		
		expendView=(ExpandableListView) findViewById(R.id.list);
		expendView.setGroupIndicator(null);  //设置默认左边箭头图标不显示
		expendView.setAdapter(adapter);

		//一级点击事件
		expendView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
			
				group_click[groupPosition]+=1;
				adapter.notifyDataSetChanged();
				return false;
			}
		});
		
		//二级点击事件
		expendView.setOnChildClickListener(new OnChildClickListener() {	
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				//可在这里做点击事件
				if(groupPosition==0&&childPosition==1){
					Builder builder=new Builder(HomeActivity.this);
					builder.setTitle("添加联系人");
					
					View view=getLayoutInflater().inflate(R.layout.email_add_address, null);
					final EditText name=(EditText) view.findViewById(R.id.name);
					final EditText addr=(EditText) view.findViewById(R.id.address);

					builder.setView(view);
					builder.setPositiveButton("确定", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							insertAddress(name.getText().toString().trim(),addr.getText().toString().trim());
						}
					});
					builder.setNegativeButton("取消", null);
					builder.show();
				}else if(groupPosition==0&&childPosition==0){
					Intent intent=new Intent(HomeActivity.this, MailConstactsActivity.class);
					startActivity(intent);
				}else if(groupPosition==1&&childPosition==0){
					Intent intent=new Intent(HomeActivity.this, MailEditActivity.class);
					startActivity(intent);
				}else if(groupPosition==1&&childPosition==1){
					Intent intent=new Intent(HomeActivity.this, MailCaogaoxiangActivity.class);
					startActivity(intent);
				}else if(groupPosition==2&&childPosition==0){
					Intent intent=new Intent(HomeActivity.this, MailBoxActivity.class);
					intent.putExtra("TYPE", "INBOX");
					intent.putExtra("status", 0);//全部
					startActivity(intent);
				}else if(groupPosition==2&&childPosition==1){
					Intent intent=new Intent(HomeActivity.this, MailBoxActivity.class);
					intent.putExtra("TYPE", "INBOX");
					intent.putExtra("status", 1);//未读
					startActivity(intent);
				}else if(groupPosition==2&&childPosition==2){
					Intent intent=new Intent(HomeActivity.this, MailBoxActivity.class);
					intent.putExtra("TYPE", "INBOX");
					intent.putExtra("status", 2);//已读
					startActivity(intent);
				}
				adapter.notifyDataSetChanged();
				return false;
			}
		});
		
	}
	
	/**
	 * 添加联系人
	 */
	private void insertAddress(String user,String address){
		if(user==null){
			Toast.makeText(HomeActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
		}else{
			if(!EmailFormatUtil.emailFormat(address)){
				Toast.makeText(HomeActivity.this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
			}else{
				Uri uri=Uri.parse("content://com.emailconstantprovider");
				ContentValues values=new ContentValues();
				values.put("mailfrom", MyApplication.info.getUserName());
				values.put("name", user);
				values.put("address", address);
				getContentResolver().insert(uri, values);
				
				Toast.makeText(HomeActivity.this, "添加数据成功", Toast.LENGTH_SHORT).show();
			}
		}
		
		
	}
	
	/**
	 * 适配器
	 * @author Administrator
	 *
	 */
	private class MyExpendAdapter extends BaseExpandableListAdapter{
		
		/**
		 * pic state
		 */
		//int []group_state=new int[]{R.drawable.group_right,R.drawable.group_down};
		
		/**
		 * group title
		 */
		String []group_title=new String[]{"联系人","写邮件","收件箱"};
		
		/**
		 * child text
		 */
		String [][] child_text=new String [][]{
				{"联系人列表","添加联系人"},
				{"新邮件","草稿箱"},
				{"全部邮件","未读邮件","已读邮件"},};
		int [][] child_icons=new int[][]{
				{R.drawable.listlianxiren,R.drawable.tianjia},
				{R.drawable.xieyoujian,R.drawable.caogaoxiang},
				{R.drawable.all,R.drawable.notread,R.drawable.hasread},
		};
        
		/**
		 * 获取一级标签中二级标签的内容
		 */
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return child_text[groupPosition][childPosition];
		}
        
		/**
		 * 获取二级标签ID
		 */
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}
		/**
		 * 对一级标签下的二级标签进行设置
		 */
		@SuppressLint("SimpleDateFormat")
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			convertView=getLayoutInflater().inflate(R.layout.email_child, null);
			TextView tv=(TextView) convertView.findViewById(R.id.tv);
			tv.setText(child_text[groupPosition][childPosition]);
			
			ImageView iv=(ImageView) convertView.findViewById(R.id.child_icon);
			iv.setImageResource(child_icons[groupPosition][childPosition]);
			return convertView;
		}
        
		/**
		 * 一级标签下二级标签的数量
		 */
		@Override
		public int getChildrenCount(int groupPosition) {
			return child_text[groupPosition].length;
		}
        
		/**
		 * 获取一级标签内容
		 */
		@Override
		public Object getGroup(int groupPosition) {
			return group_title[groupPosition];
		}
        
		/**
		 * 一级标签总数
		 */
		@Override
		public int getGroupCount() {
			return group_title.length;
		}
        
		/**
		 * 一级标签ID
		 */
		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}
		/**
		 * 对一级标签进行设置
		 */
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			convertView=getLayoutInflater().inflate(R.layout.email_group, null);
			
			ImageView icon=(ImageView) convertView.findViewById(R.id.icon);
			ImageView iv=(ImageView) convertView.findViewById(R.id.iv);
			TextView tv=(TextView) convertView.findViewById(R.id.iv_title);
			
			iv.setImageResource(R.drawable.group_right);
			tv.setText(group_title[groupPosition]);
			
			if(groupPosition==0){
				icon.setImageResource(R.drawable.constants);
			}else if(groupPosition==1){
				icon.setImageResource(R.drawable.mailto);
			}else if(groupPosition==2){
				icon.setImageResource(R.drawable.mailbox);
			}
            
			if(group_click[groupPosition]%2==0){
				iv.setImageResource(R.drawable.group_right);
			}else{
				iv.setImageResource(R.drawable.group_down);
			}
			
			return convertView;
		}
		/**
		 * 指定位置相应的组视图
		 */
		@Override
		public boolean hasStableIds() {
			return true;
		}
        
		/**
		 *  当选择子节点的时候，调用该方法
		 */
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
		
	}
	
	/**
	 * 返回退出系统
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if((System.currentTimeMillis()-mExitTime)<2000){
				android.os.Process.killProcess(android.os.Process.myPid());
			}else{
				Toast.makeText(HomeActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				mExitTime=System.currentTimeMillis();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
