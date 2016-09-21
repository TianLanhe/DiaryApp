package com.example.diarypractice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

/*********
 * 
 * @author ������
 * @time 2016/9/20
 * 
 */
public class MainActivity extends Activity {
	private ListView listview; // �б�ؼ�
	public static List<Diary> diarylist = null; // һ���µ��ռ�����
	private DiaryAdapter diaryadapter; // listview��������
	private ImageButton new_button; // �½�/�༭�����ռǵİ�ť
	private Button month_button; // �·ݰ�ť
	private Button year_button; // ��ݰ�ť
	private Button view_button;// �л���һ��ͼ�İ�ť
	private Button setting_button;// ���ð�ť
	private String[] month = { "Jan", "Feb", "Mar", "Apr", "May", "June",
			"July", "Aug", "Sept", "Oct", "Nov", "Dec" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// ȡ����Ļ��������
		setContentView(R.layout.activity_main); // ����������

		listview = (ListView) findViewById(R.id.activity_main_diary_listview);
		initButton();

		// diarylist = null; //���ڳ�Ա��������ʱ��ʼ��
		initDiaryList();// ��ȡ���յ����ڲ����ð�ť����ȡ���µ��ռ�

		diaryadapter = new DiaryAdapter(this, R.layout.listview_diary,
				R.layout.listview_point, diarylist);
		listview.setAdapter(diaryadapter); // ����listview

		// new��ť�����¼�
		new_button.setOnClickListener(new OnClickListener() {
			@Override
			// ��ת����ǰ��ݺ��·ݣ����򿪵�ǰ���ڵ��ռǱ༭����
			public void onClick(View arg0) {
				Calendar calendar = Calendar.getInstance();
				changeDate(calendar.get(Calendar.YEAR),
						calendar.get(Calendar.MONTH));
				listview.setSelection(calendar.get(Calendar.DATE) - 1);// ����listviewָ��ĳһitem
				Diary diary = diarylist.get(calendar.get(Calendar.DATE) - 1);
				EditActivity.startEditActivityForResult(MainActivity.this,
						diary);
			}
		});

		// month�����¼�
		month_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final AlertDialog dialog;
				ListView listview_detail;
				List<Object> objectlist;
				List<String> month = new ArrayList<String>();

				String[] str = { "һ��", "����", "����", "����", "����", "����", "����",
						"����", "����", "ʮ��", "ʮһ��", "ʮ����" };
				for (int i = 0; i < 12; i++)
					month.add(str[i]);

				objectlist = createMyAlertDialog(month);
				listview_detail = (ListView) objectlist.get(0);
				dialog = (AlertDialog) objectlist.get(1);

				listview_detail
						.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int position, long id) {
								dialog.dismiss();
								changeDate(diarylist.get(0).getYear(), position);
								diaryadapter.notifyDataSetChanged();// ˢ��һ��listview
							}
						});
			}
		});

		// year��ť�����¼�
		year_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final AlertDialog dialog;
				ListView listview_detail;
				List<Object> objectlist;
				List<String> year = new ArrayList<String>();

				for (int i = 2010; i < 2030; i++)
					year.add(String.valueOf(i));

				objectlist = createMyAlertDialog(year);
				listview_detail = (ListView) objectlist.get(0);
				dialog = (AlertDialog) objectlist.get(1);

				listview_detail
						.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int position, long id) {
								dialog.dismiss();
								changeDate(position + 2010, diarylist.get(0)
										.getMonth() - 1);
								diaryadapter.notifyDataSetChanged();
							}
						});
			}
		});

		// listview��item�����¼�
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Diary diary = diarylist.get(position);
				EditActivity.startEditActivityForResult(MainActivity.this,
						diary);
			}
		});

		// listview��item�����¼�
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				final int long_click_position = position; // ��ס������item�±�
				final AlertDialog dialog;
				ListView listview_detail;
				List<Object> objectlist;
				List<String> str = new ArrayList<String>();

				if (view.getId() == R.id.activity_main_listview_diary_layout) {
					str.add("�鿴");
					str.add("ɾ��");

					objectlist = createMyAlertDialog(str);
					listview_detail = (ListView) objectlist.get(0);
					dialog = (AlertDialog) objectlist.get(1);

					listview_detail
							.setOnItemClickListener(new OnItemClickListener() {
								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									dialog.dismiss();
									if (position == 0) {// �鿴����
										EditActivity
												.startEditActivityForResult(
														MainActivity.this,
														diarylist
																.get(long_click_position));
									} else if (position == 1) {// ɾ������
										diarylist.get(long_click_position)
												.setContent("");// ������diarylist.remove(int
																// index)��ֻ��Ҫ���content����
										diaryadapter.notifyDataSetChanged();// ˢ��listview
									}
								}
							});
				} else {// ����������Ǹ���
					str.add("���");
					
					objectlist = createMyAlertDialog(str);
					listview_detail = (ListView) objectlist.get(0);
					dialog = (AlertDialog) objectlist.get(1);
					
					listview_detail
							.setOnItemClickListener(new OnItemClickListener() {
								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									dialog.dismiss();
									EditActivity.startEditActivityForResult(
											MainActivity.this,
											diarylist.get(long_click_position));
								}
							});
				}
				
				WindowManager.LayoutParams params = dialog.getWindow()
						.getAttributes();
				params.width = 300;		//����alertdialog�ĳ��ȺͿ��
				params.height = 200;
				dialog.getWindow().setAttributes(params);
				return true;// ���return false����ؼ�����Ӧ�����Ͷ̰������¼���true�򳤰������ض̰���ֻ��Ӧ����
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		switch (requestCode) {
		case EditActivity.RETURN_CODE:
			if (resultCode == RESULT_OK) {
				Diary temp = (Diary) intent
						.getSerializableExtra("diary_return");
				diarylist.get(temp.getDate() - 1).setContent(temp.getContent());
				diaryadapter.notifyDataSetChanged();
			} else
				Toast.makeText(this, "onActivityResult error!",
						Toast.LENGTH_SHORT).show();
			break;
		default:
			Toast.makeText(this, "onActivityResult error!", Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (diarylist != null) { // ����ռ��б�Ϊ�գ���鿴�����Ƿ������ݣ����򱣴浽�ļ�
			Diary temp;
			int i;
			for (i = 0; i < diarylist.size(); i++) {
				temp = diarylist.get(i);
				if (!temp.getContent().equals(""))
					break;
			}
			if (i != diarylist.size()) {
				FileOutputStream out;
				ObjectOutputStream objectout;
				try {
					out = openFileOutput(year_button.getText() + ""
							+ month_button.getText(), Context.MODE_PRIVATE);
					objectout = new ObjectOutputStream(out);
					objectout.writeObject(diarylist);
					objectout.close();
					out.close();
				} catch (Exception exp) {
					Toast.makeText(this, "Diary saved failed",
							Toast.LENGTH_LONG).show();
				}
			} else {	//���ȫ���ռǶ��ǿյģ���ɾ�����µ��ռ��ļ�
				File file = new File(getFilesDir().getPath() + "/"
						+ year_button.getText() + month_button.getText());
				if (file.exists())
					file.delete();
			}
		}
	}

	private void initDiaryList() {
		Calendar calendar = Calendar.getInstance();
		changeDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
	}

	// �ı�����ʱ�Ĳ���
	public void changeDate(int year, int month) {
		// ����ռ��б�Ϊ�գ���鿴�����Ƿ������ݣ����򱣴浽�ļ���û����ɾ���ļ�
		if (diarylist != null) {
			Diary temp;
			int i;
			for (i = 0; i < diarylist.size(); i++) {
				temp = diarylist.get(i);
				if (!temp.getContent().equals(""))
					break;
			}
			// �ռ��б��������ݣ�Ҫ���浽�ļ�
			if (i != diarylist.size()) {
				FileOutputStream out;
				ObjectOutputStream objectout;
				try {
					out = openFileOutput(year_button.getText() + ""
							+ month_button.getText(), Context.MODE_PRIVATE);
					objectout = new ObjectOutputStream(out);
					objectout.writeObject(diarylist);
					objectout.close();
					out.close();
				} catch (Exception exp) {// ���治�������쳣
					Toast.makeText(this, "Diary saved failed",
							Toast.LENGTH_LONG).show();
				}
			} else {// ���û���ռ����ݣ����ñ��棬ɾ�������ļ�
				File file = new File(getFilesDir().getPath() + "/"
						+ year_button.getText() + month_button.getText());
				if (file.exists())
					file.delete();
			}
		}
		// ���ò���ȡ��ʱ����ռ�
		month_button.setText(this.month[month]);
		year_button.setText(String.valueOf(year));
		FileInputStream in;
		ObjectInputStream objectin;
		// ���ļ�����ȡ���������浽�ռ��б��У���û�и��µ��ļ������½�һ���յ��ռ��б�
		try {
			in = openFileInput(year_button.getText() + ""
					+ month_button.getText());
			objectin = new ObjectInputStream(in);
			if (diarylist == null)
				diarylist = (List<Diary>) objectin.readObject();
			else {
				diarylist.clear();
				List<Diary> templist = (List<Diary>) objectin.readObject();
				for (int i = 0; i < templist.size(); i++)
					diarylist.add(templist.get(i));
			}
		} catch (Exception exp) {
			if (diarylist == null)
				diarylist = new ArrayList<Diary>();
			else
				diarylist.clear();
			int day = getDayInMonth(year, month);
			for (int i = 0; i < day; i++)
				diarylist.add(new Diary(year, month + 1, i + 1));
		}
	}

	// ������ݺ��·ݼ��㵱�µ��������·���0��ʼ��
	private int getDayInMonth(int year, int month) {
		int day;
		month += 1;
		if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8
				|| month == 10 || month == 12) {
			day = 31;
		} else if (month == 2) {
			if (year % 400 == 0 || year % 100 != 0 && year % 4 == 0)
				day = 29;
			else
				day = 28;
		} else {
			day = 30;
		}
		return day;
	}

	// ��ʼ����ť
	private void initButton() {
		new_button = (ImageButton) findViewById(R.id.activity_main_create_button);
		month_button = (Button) findViewById(R.id.activity_main_month_button);
		year_button = (Button) findViewById(R.id.activity_main_year_button);
		view_button = (Button) findViewById(R.id.activity_main_view_button);
		setting_button = (Button) findViewById(R.id.activity_main_setting_button);
	}

	// ���ݴ�����ַ������ϴ���һ������listview��alertdialog�����ü��ϵķ�ʽ����listview��alertdialog�Թ�֮�����
	public List<Object> createMyAlertDialog(List<String> strlist) {
		List<Object> objectlist = new ArrayList<Object>();
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		View dialogview = LayoutInflater.from(MainActivity.this).inflate(
				R.layout.alertdialog_detail, null);
		ListView listview_detail = (ListView) dialogview
				.findViewById(R.id.alertdialog_detail_listview);
		StringAdapter stringadapter = new StringAdapter(MainActivity.this,
				R.layout.listview_detail, strlist);
		listview_detail.setAdapter(stringadapter);
		builder.setView(dialogview);
		builder.setCancelable(true);
		AlertDialog dialog = builder.create();
		dialog.show();
		dialog.setCanceledOnTouchOutside(true);
		objectlist.add(listview_detail);
		objectlist.add(dialog);
		return objectlist;
	}
}
