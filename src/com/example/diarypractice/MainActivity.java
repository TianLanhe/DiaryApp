package com.example.diarypractice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

/*********
 * 
 * @author ������
 * @time 2016/9/20
 * 
 */
public class MainActivity extends Activity {
	private ListView listview; // �б�ؼ�
	private ListView view_listview;

	private List<Diary> monthdiarylist = null; // ���µ��ռ���ϸ����
	private List<Diary> diarylist = null; // һ���µ��ռ�����
	private List<View> viewlist; // ��������ͼ����������

	private DiaryAdapter diaryadapter; // listview��������
	private MonthDiaryAdapter monthdiaryadapter; // view_listview��������

	private ImageButton new_button; // �½�/�༭�����ռǵİ�ť
	private Button month_button; // �·ݰ�ť
	private Button year_button; // ��ݰ�ť
	private ImageButton view_button;// �л���һ��ͼ�İ�ť
	private ImageButton setting_button;// ���ð�ť

	private ViewPager viewpager;

	private RadioGroup radiogroup;
	private RadioButton radiobutton[];

	private View diary_list_view; // ��׼�ռ���ͼ
	private View month_diary_list_view; // �����ռ���ͼ

	private String[] month = { "Jan", "Feb", "Mar", "Apr", "May", "June",
			"July", "Aug", "Sept", "Oct", "Nov", "Dec" };

	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// ȡ����Ļ��������
		setContentView(R.layout.activity_main); // ����������

		// viewpager��ʼ��
		viewpager = (ViewPager) findViewById(R.id.activity_main_viewpager);

		// ��ʼ��������viewpager�е�view
		diary_list_view = LayoutInflater.from(this).inflate(
				R.layout.viewpager_main, null);
		month_diary_list_view = LayoutInflater.from(this).inflate(
				R.layout.viewpager_view, null);

		// viewpager��list��ʼ�����������
		viewlist = new ArrayList<View>();
		viewlist.add(month_diary_list_view);
		viewlist.add(diary_list_view);

		listview = (ListView) diary_list_view
				.findViewById(R.id.activity_main_diary_listview);
		new_button = (ImageButton) diary_list_view
				.findViewById(R.id.activity_main_create_button);
		month_button = (Button) diary_list_view
				.findViewById(R.id.activity_main_month_button);
		year_button = (Button) diary_list_view
				.findViewById(R.id.activity_main_year_button);
		view_button = (ImageButton) diary_list_view
				.findViewById(R.id.activity_main_view_button);
		setting_button = (ImageButton) diary_list_view
				.findViewById(R.id.activity_main_setting_button);

		view_listview = (ListView) month_diary_list_view
				.findViewById(R.id.activity_main_view_listview);

		radiogroup = (RadioGroup) month_diary_list_view
				.findViewById(R.id.radg_month_radg);
		radiobutton = new RadioButton[12];
		for (int i = 0; i < 12; ++i) {
			try {
				Field field = R.id.class.getDeclaredField("btn_month_" + i);
				int id = field.getInt(R.id.class);
				radiobutton[i] = (RadioButton) month_diary_list_view
						.findViewById(id);
				radiobutton[i].setText(month[i].substring(0, 3));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		initDiaryList();// ��ȡ���յ����ڲ����ð�ť����ȡ���µ��ռ�

		// ����listview�����÷ָ���Ϊnull�����޷ָ��ߣ�Ҳ����xml��android:divider="@null"
		diaryadapter = new DiaryAdapter(this, R.layout.listview_diary,
				R.layout.listview_point, diarylist);
		listview.setAdapter(diaryadapter);

		initMonthDiary();
		monthdiaryadapter = new MonthDiaryAdapter(this,
				R.layout.listview_detail, monthdiarylist);
		view_listview.setAdapter(monthdiaryadapter);

		// �·�ѡ�񵥻��¼�
		radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				List<Diary> temp;

				if (monthdiarylist == null)
					monthdiarylist = new ArrayList<Diary>();
				else
					monthdiarylist.clear();

				String button_text = ((RadioButton) arg0.findViewById(arg1))
						.getText().toString();
				if (button_text.equals("Jun"))
					button_text = "June";
				else if (button_text.equals("Jul"))
					button_text = "July";
				else if (button_text.equals("Sep"))
					button_text = "Sept";

				// ����ǵ��£���ֱ�����ڴ��ȡ
				if (button_text.equals(month_button.getText().toString())) {
					for (int i = 0; i < diarylist.size(); i++)
						if (!diarylist.get(i).getContent().equals(""))
							monthdiarylist.add(diarylist.get(i));
				} else {// ���򣬵�����ȡ
					try {
						FileInputStream in;
						ObjectInputStream objectin;
						in = openFileInput(year_button.getText() + ""
								+ button_text);
						objectin = new ObjectInputStream(in);

						temp = (List<Diary>) objectin.readObject();

						for (int i = 0; i < temp.size(); i++)
							if (!temp.get(i).getContent().equals(""))
								monthdiarylist.add(temp.get(i));
					} catch (Exception exp) {
						// ����û���ռǣ��б�Ϊ��
					}
				}
				monthdiaryadapter.notifyDataSetChanged();
			}
		});

		// viewpager����������������list������viewpagerָ���ʼ�ı�׼������ͼ
		viewpager.setAdapter(new PagerAdapter() {
			@Override
			public int getCount() {
				return viewlist.size();
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				((ViewPager) container).removeView(viewlist.get(position));
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				((ViewPager) container).addView(viewlist.get(position));
				return viewlist.get(position);
			}
		});
		viewpager.setCurrentItem(1);
		viewpager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int arg0) {
				if (arg0 == 0) {
					int i;
					for (i = 0; i < 12; ++i)
						if (month[i].equals(month_button.getText()))
							break;
					radiobutton[i].setChecked(true);
				}
			}
		});

		// new��ť�����¼�
		new_button.setOnClickListener(new OnClickListener() {
			@Override
			// ��ת����ǰ��ݺ��·ݣ����򿪵�ǰ���ڵ��ռǱ༭����
			public void onClick(View arg0) {
				Calendar calendar = Calendar.getInstance();
				changeDate(calendar.get(Calendar.YEAR),
						calendar.get(Calendar.MONTH));
				listview.setSelection(calendar.get(Calendar.DATE) - 1);// ����listview�����item
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

		// ��ͼ�л���ť�����¼�
		view_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				viewpager.setCurrentItem(0);
			}
		});

		setting_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO
				Toast.makeText(MainActivity.this, "����", Toast.LENGTH_SHORT)
						.show();
			}
		});

		// listview��item�����¼�
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final Diary diary = diarylist.get(position);
				if (diary.getFlag()) {
					final View view1 = LayoutInflater.from(MainActivity.this)
							.inflate(R.layout.alertdialog_unlock, null);
					DialogInterface.OnClickListener positivelistener = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							EditText password = (EditText) view1
									.findViewById(R.id.alertdialog_unlock_password_edittext);
							if (checkPassword(password.getText().toString(),
									diary)) {
								EditActivity.startEditActivityForResult(
										MainActivity.this, diary);
							} else {
								Toast.makeText(MainActivity.this, "�������",
										Toast.LENGTH_SHORT).show();
							}
						}
					};
					DialogInterface.OnClickListener negativelistener = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
						}
					};
					EditActivity.showLockAlertDialog(MainActivity.this,
							"����������", view1, positivelistener, negativelistener);
				} else
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
					if (diarylist.get(long_click_position).getFlag()) {
						str.add("����");
					} else {
						str.add("����");
					}

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
										if (diarylist.get(long_click_position)
												.getFlag()) { // ��������Ҫ����������ܲ鿴
											final View view1 = LayoutInflater
													.from(MainActivity.this)
													.inflate(
															R.layout.alertdialog_unlock,
															null);
											DialogInterface.OnClickListener positivelistener = new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface arg0,
														int arg1) {
													EditText password = (EditText) view1
															.findViewById(R.id.alertdialog_unlock_password_edittext);
													if (checkPassword(
															password.getText()
																	.toString(),
															diarylist
																	.get(long_click_position))) {
														EditActivity
																.startEditActivityForResult(
																		MainActivity.this,
																		diarylist
																				.get(long_click_position));
													} else {
														Toast.makeText(
																MainActivity.this,
																"�������",
																Toast.LENGTH_SHORT)
																.show();
													}
												}
											};
											DialogInterface.OnClickListener negativelistener = new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface arg0,
														int arg1) {
												}
											};
											// ������������Ի���
											EditActivity.showLockAlertDialog(
													MainActivity.this, "����������",
													view1, positivelistener,
													negativelistener);
										} else {
											EditActivity
													.startEditActivityForResult(
															MainActivity.this,
															diarylist
																	.get(long_click_position));
										}
									} else if (position == 1) {// ɾ������
										diarylist.get(long_click_position)
												.setFlag(false);
										diarylist.get(long_click_position)
												.setContent("");// ������diarylist.remove(int
																// index)��ֻ��Ҫ���content����
										diaryadapter.notifyDataSetChanged();// ˢ��listview
									} else if (position == 2) {// �������������
										if (diarylist.get(long_click_position)
												.getFlag()) {
											final View view1 = LayoutInflater
													.from(MainActivity.this)
													.inflate(
															R.layout.alertdialog_unlock,
															null);
											DialogInterface.OnClickListener positivelistener = new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface arg0,
														int arg1) {
													EditText password = (EditText) view1
															.findViewById(R.id.alertdialog_unlock_password_edittext);
													if (checkPassword(
															password.getText()
																	.toString(),
															diarylist
																	.get(long_click_position))) {
														diarylist
																.get(long_click_position)
																.setFlag(false);
														diaryadapter
																.notifyDataSetChanged();
													} else {
														Toast.makeText(
																MainActivity.this,
																"������󣬽���ʧ��",
																Toast.LENGTH_SHORT)
																.show();
													}
												}
											};
											DialogInterface.OnClickListener negativelistener = new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface arg0,
														int arg1) {
												}
											};
											EditActivity.showLockAlertDialog(
													MainActivity.this, "ȡ�����룺",
													view1, positivelistener,
													negativelistener);
										} else {
											final View view1 = LayoutInflater
													.from(MainActivity.this)
													.inflate(
															R.layout.alertdialog_lock,
															null);
											DialogInterface.OnClickListener positivelistener = new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface arg0,
														int arg1) {
													EditText password = (EditText) view1
															.findViewById(R.id.alertdialog_lock_password_edittext);
													EditText password_repeat = (EditText) view1
															.findViewById(R.id.alertdialog_lock_repeat_password_edittext);
													if (EditActivity
															.savePassword(
																	MainActivity.this,
																	password.getText()
																			.toString(),
																	password_repeat
																			.getText()
																			.toString(),
																	diarylist
																			.get(long_click_position))) {
														diarylist
																.get(long_click_position)
																.setFlag(true);
														diaryadapter
																.notifyDataSetChanged();
													} else {
														Toast.makeText(
																MainActivity.this,
																"���������������ʧ��",
																Toast.LENGTH_SHORT)
																.show();
													}
												}
											};
											DialogInterface.OnClickListener negativelistener = new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface arg0,
														int arg1) {
												}
											};
											EditActivity.showLockAlertDialog(
													MainActivity.this, "��������",
													view1, positivelistener,
													negativelistener);
										}
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
				return true;// ���return false����ؼ�����Ӧ�����Ͷ̰������¼���true�򳤰������ض̰���ֻ��Ӧ����
			}
		});
	}

	private void initMonthDiary() {
		if (monthdiarylist == null)
			monthdiarylist = new ArrayList<Diary>();
		else
			monthdiarylist.clear();
		for (int i = 0; i < diarylist.size(); i++)
			if (!diarylist.get(i).getContent().equals(""))
				monthdiarylist.add(diarylist.get(i));
	}

	// �������������Ƿ�����������õ�������ͬ
	private boolean checkPassword(String password, Diary diary) {
		SharedPreferences pref = getSharedPreferences("password", MODE_PRIVATE);
		String correct_password = pref.getString(
				diary.getYear() + "/" + diary.getMonth() + "/"
						+ diary.getDate(), "");
		if (password.equals(correct_password))
			return true;
		else
			return false;
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
				diarylist.get(temp.getDate() - 1).setFlag(temp.getFlag());
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
			} else { // ���ȫ���ռǶ��ǿյģ���ɾ�����µ��ռ��ļ�
				File file = new File(getFilesDir().getPath() + "/"
						+ year_button.getText() + month_button.getText());
				if (file.exists())
					file.delete();
			}
		}
	}

	// ��ʼ�����ڣ�ָʾ��ǰ���²���ʾ�����ռ�����
	private void initDiaryList() {
		Calendar calendar = Calendar.getInstance();
		changeDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
	}

	// �ı�����ʱ�Ĳ���
	@SuppressWarnings("unchecked")
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
					// Toast.makeText(this, "Diary saved failed",
					// Toast.LENGTH_LONG).show();
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

	// ���ݴ�����ַ������ϴ���һ������listview��alertdialog�����ü��ϵķ�ʽ����listview��alertdialog�Թ�֮�����
	@SuppressLint("InflateParams")
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
