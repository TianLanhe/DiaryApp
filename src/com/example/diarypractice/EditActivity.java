package com.example.diarypractice;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
/*********
 * 
 * @author 何振宇
 * @time 2016/9/20
 * 
 */
public class EditActivity extends Activity {
	private Diary diary;// 日记实体类
	private TextView week_textview;
	private TextView month_and_date_textview;
	private TextView year_textview;
	private EditText edittext;
	private Button save_button;// 保存按钮
	private Button time_button;// 时间按钮
	public static final int RETURN_CODE = 1;
	String[] month = { "January", "February", "March", "April", "May", "June",
			"July", "August", "September", "October", "November", "December" };
	String[] week = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
			"Friday", "Saturday" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 取消屏幕顶标题栏
		setContentView(R.layout.activity_edit); // 加载日记详情布局
		initDiary();

		// 保存按钮做的只是将edittext的内容存储到成员变量diary中，在退出时将diary保存到之前的日记中
		save_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String diarytext = edittext.getText().toString();
				diary.setContent(diarytext);
				onBackPressed();
			}
		});
		
		time_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Calendar calendar=Calendar.getInstance();
				int hour=calendar.get(Calendar.HOUR);
				int minute=calendar.get(Calendar.MINUTE);
				String am_pm=calendar.get(Calendar.AM_PM)==0?" am ":" pm ";
				edittext.getText().insert(edittext.getSelectionStart(), " "+hour+":"+minute+am_pm);
			}
		});
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.putExtra("diary_return", diary);
		setResult(RESULT_OK, intent);
		super.onBackPressed();// 必须在onbackpressed调用之前设置好要传回去的数据，否则onActivityResult收不到intent和resultcode
	}

	// 单纯地启动这个activity
	public static void startEditActivity(Context context, Diary diary) {
		Intent intent = new Intent(context, EditActivity.class);
		context.startActivity(intent);
	}

	// 需要传递数据和返回数据的方式启动这个activity
	public static void startEditActivityForResult(Activity context, Diary diary) {
		Intent intent = new Intent(context, EditActivity.class);
		intent.putExtra("editdiary", diary);
		context.startActivityForResult(intent, RETURN_CODE);
	}

	private void initDiary() {
		Intent intent = getIntent();
		diary = (Diary) intent.getSerializableExtra("editdiary");// 取出传过来的Diary对象

		week_textview = (TextView) findViewById(R.id.activity_edit_week_textview);
		month_and_date_textview = (TextView) findViewById(R.id.activity_edit_month_textview);
		year_textview = (TextView) findViewById(R.id.activity_edit_year_textview);
		edittext = (EditText) findViewById(R.id.activity_edit_edittext);
		save_button = (Button) findViewById(R.id.activity_edit_save_button);
		time_button = (Button) findViewById(R.id.activity_edit_time_button);

		week_textview.setText(week[diary.getWeek()]);
		if (diary.getWeek() == 0 || diary.getWeek() == 6)
			week_textview.setTextColor(Color.RED);
		month_and_date_textview.setText(month[diary.getMonth() - 1] + " "
				+ String.valueOf(diary.getDate()));
		year_textview.setText(String.valueOf(diary.getYear()));
		edittext.setText(diary.getContent());
		edittext.setSelection(edittext.getText().length());// 设置edittext的光标指向文本末尾
	}
}
