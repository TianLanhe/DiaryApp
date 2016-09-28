package com.example.diarypractice;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/*********
 * 
 * @author ������
 * @time 2016/9/20
 * 
 */
public class EditActivity extends Activity {
	private Diary diary;// �ռ�ʵ����
	private TextView title_textview;
	private EditText edittext;
	private Button save_button;// ���水ť
	private Button time_button;// ʱ�䰴ť
	private ToggleButton lock_button; // �Ƿ�������ť
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
				Calendar calendar = Calendar.getInstance();
				int hour = calendar.get(Calendar.HOUR);
				int minute = calendar.get(Calendar.MINUTE);
				String am_pm = calendar.get(Calendar.AM_PM) == 0 ? " am "
						: " pm ";
				edittext.getText().insert(edittext.getSelectionStart(),
						" " + hour + ":" + minute + am_pm);
			}
		});
		
		lock_button.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton button, boolean isChecked) {
				if(isChecked){
					String title="��������";
					final View view=LayoutInflater.from(EditActivity.this).inflate(R.layout.alertdialog_lock, null);
					DialogInterface.OnClickListener positivelistener=new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							EditText password=(EditText) view.findViewById(R.id.alertdialog_lock_password_edittext);
							EditText password_repeat=(EditText) view.findViewById(R.id.alertdialog_lock_repeat_password_edittext);
							if(savePassword(EditActivity.this,password.getText().toString(),password_repeat.getText().toString(),diary)){
								diary.setFlag(true);
								lock_button.setBackgroundResource(R.drawable.lock);
							}else{
								Toast.makeText(EditActivity.this, "���������������ʧ��", Toast.LENGTH_SHORT).show();
								lock_button.setChecked(false);
							}
						}
					};
					DialogInterface.OnClickListener negativelistener=new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							lock_button.setBackgroundResource(R.drawable.unlock);
							lock_button.setChecked(false);
						}
					};
					
					showLockAlertDialog(EditActivity.this,title,view,positivelistener,negativelistener);
				}else{
					button.setBackgroundResource(R.drawable.unlock);
					diary.setFlag(false);
				}
			}
		});

	}

	//������⣬���֣�ȷ����ť��������ȡ����ť������������һ��alertdialog����ʾ
	public static void showLockAlertDialog(Context context,String title,View view,DialogInterface.OnClickListener positivelistener,DialogInterface.OnClickListener negativelistener){
		AlertDialog.Builder builder=new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setCancelable(false);
		builder.setView(view);
		builder.setPositiveButton("ȷ��", positivelistener);
		builder.setNegativeButton("ȡ��", negativelistener);
		builder.show();
	}
	
	public static boolean savePassword(Context context,String password,String password_repeat,Diary diary){
		if(!password.equals("")&&password.equals(password_repeat)){
			SharedPreferences.Editor editor=context.getSharedPreferences("password", MODE_PRIVATE).edit();
			editor.putString(diary.getYear()+"/"+diary.getMonth()+"/"+diary.getDate(), password);
			editor.commit();
			return true;
		}else{
			return false;
		}
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

		title_textview = (TextView) findViewById(R.id.activity_edit_title_textview);
		edittext = (EditText) findViewById(R.id.activity_edit_edittext);
		save_button = (Button) findViewById(R.id.activity_edit_save_button);
		time_button = (Button) findViewById(R.id.activity_edit_time_button);
		lock_button = (ToggleButton) findViewById(R.id.activity_edit_lock_button);

		title_textview.setText(week[diary.getWeek()] + "/"
				+ month[diary.getMonth() - 1] + " "
				+ String.valueOf(diary.getDate()) + "/"
				+ String.valueOf(diary.getYear()));
		if (diary.getWeek() == 0 || diary.getWeek() == 6) {
			String text = title_textview.getText().toString();
			SpannableStringBuilder builder = new SpannableStringBuilder(text);
			ForegroundColorSpan colorspan = new ForegroundColorSpan(Color.RED);
			builder.setSpan(colorspan, 0, text.indexOf("/"),
					SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
			title_textview.setText(builder);
		}
		edittext.setText(diary.getContent());
		edittext.setSelection(edittext.getText().length());// ����edittext�Ĺ��ָ���ı�ĩβ
		if(diary.getFlag()){
			lock_button.setChecked(true);
			lock_button.setBackgroundResource(R.drawable.lock);
		}else{
			lock_button.setChecked(false);
			lock_button.setBackgroundResource(R.drawable.unlock);
		}
	}
}
