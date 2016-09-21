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
 * @author ������
 * @time 2016/9/20
 * 
 */
public class EditActivity extends Activity {
	private Diary diary;// �ռ�ʵ����
	private TextView week_textview;
	private TextView month_and_date_textview;
	private TextView year_textview;
	private EditText edittext;
	private Button save_button;// ���水ť
	private Button time_button;// ʱ�䰴ť
	public static final int RETURN_CODE = 1;
	String[] month = { "January", "February", "March", "April", "May", "June",
			"July", "August", "September", "October", "November", "December" };
	String[] week = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
			"Friday", "Saturday" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// ȡ����Ļ��������
		setContentView(R.layout.activity_edit); // �����ռ����鲼��
		initDiary();

		// ���水ť����ֻ�ǽ�edittext�����ݴ洢����Ա����diary�У����˳�ʱ��diary���浽֮ǰ���ռ���
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
		super.onBackPressed();// ������onbackpressed����֮ǰ���ú�Ҫ����ȥ�����ݣ�����onActivityResult�ղ���intent��resultcode
	}

	// �������������activity
	public static void startEditActivity(Context context, Diary diary) {
		Intent intent = new Intent(context, EditActivity.class);
		context.startActivity(intent);
	}

	// ��Ҫ�������ݺͷ������ݵķ�ʽ�������activity
	public static void startEditActivityForResult(Activity context, Diary diary) {
		Intent intent = new Intent(context, EditActivity.class);
		intent.putExtra("editdiary", diary);
		context.startActivityForResult(intent, RETURN_CODE);
	}

	private void initDiary() {
		Intent intent = getIntent();
		diary = (Diary) intent.getSerializableExtra("editdiary");// ȡ����������Diary����

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
		edittext.setSelection(edittext.getText().length());// ����edittext�Ĺ��ָ���ı�ĩβ
	}
}
