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
 * @author 何振宇
 * @time 2016/9/20
 * 
 */
public class MainActivity extends Activity {
	private ListView listview; // 列表控件
	private ListView view_listview;

	private List<Diary> monthdiarylist = null; // 当月的日记详细内容
	private List<Diary> diarylist = null; // 一个月的日记内容
	private List<View> viewlist; // 上面主视图的三个布局

	private DiaryAdapter diaryadapter; // listview的适配器
	private MonthDiaryAdapter monthdiaryadapter; // view_listview的适配器

	private ImageButton new_button; // 新建/编辑当日日记的按钮
	private Button month_button; // 月份按钮
	private Button year_button; // 年份按钮
	private ImageButton view_button;// 切换另一视图的按钮
	private ImageButton setting_button;// 设置按钮

	private ViewPager viewpager;

	private RadioGroup radiogroup;
	private RadioButton radiobutton[];

	private View diary_list_view; // 标准日记视图
	private View month_diary_list_view; // 当月日记视图

	private String[] month = { "Jan", "Feb", "Mar", "Apr", "May", "June",
			"July", "Aug", "Sept", "Oct", "Nov", "Dec" };

	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 取消屏幕顶标题栏
		setContentView(R.layout.activity_main); // 加载主布局

		// viewpager初始化
		viewpager = (ViewPager) findViewById(R.id.activity_main_viewpager);

		// 初始化待加入viewpager中的view
		diary_list_view = LayoutInflater.from(this).inflate(
				R.layout.viewpager_main, null);
		month_diary_list_view = LayoutInflater.from(this).inflate(
				R.layout.viewpager_view, null);

		// viewpager的list初始化并添加内容
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

		initDiaryList();// 读取当日的日期并设置按钮，读取当月的日记

		// 设置listview，设置分割线为null，即无分割线，也可在xml里android:divider="@null"
		diaryadapter = new DiaryAdapter(this, R.layout.listview_diary,
				R.layout.listview_point, diarylist);
		listview.setAdapter(diaryadapter);

		initMonthDiary();
		monthdiaryadapter = new MonthDiaryAdapter(this,
				R.layout.listview_detail, monthdiarylist);
		view_listview.setAdapter(monthdiaryadapter);

		// 月份选择单击事件
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

				// 如果是当月，则直接在内存读取
				if (button_text.equals(month_button.getText().toString())) {
					for (int i = 0; i < diarylist.size(); i++)
						if (!diarylist.get(i).getContent().equals(""))
							monthdiarylist.add(diarylist.get(i));
				} else {// 否则，到外存读取
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
						// 该月没有日记，列表为空
					}
				}
				monthdiaryadapter.notifyDataSetChanged();
			}
		});

		// viewpager设置适配器，传入list，设置viewpager指向初始的标准操作视图
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

		// new按钮单击事件
		new_button.setOnClickListener(new OnClickListener() {
			@Override
			// 跳转到当前年份和月份，并打开当前日期的日记编辑界面
			public void onClick(View arg0) {
				Calendar calendar = Calendar.getInstance();
				changeDate(calendar.get(Calendar.YEAR),
						calendar.get(Calendar.MONTH));
				listview.setSelection(calendar.get(Calendar.DATE) - 1);// 设置listview当天的item
				Diary diary = diarylist.get(calendar.get(Calendar.DATE) - 1);
				EditActivity.startEditActivityForResult(MainActivity.this,
						diary);
			}
		});

		// month单击事件
		month_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final AlertDialog dialog;
				ListView listview_detail;
				List<Object> objectlist;
				List<String> month = new ArrayList<String>();

				String[] str = { "一月", "二月", "三月", "四月", "五月", "六月", "七月",
						"八月", "九月", "十月", "十一月", "十二月" };
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
								diaryadapter.notifyDataSetChanged();// 刷新一下listview
							}
						});
			}
		});

		// year按钮单击事件
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

		// 视图切换按钮单击事件
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
				Toast.makeText(MainActivity.this, "设置", Toast.LENGTH_SHORT)
						.show();
			}
		});

		// listview的item单击事件
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
								Toast.makeText(MainActivity.this, "密码错误",
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
							"请输入密码", view1, positivelistener, negativelistener);
				} else
					EditActivity.startEditActivityForResult(MainActivity.this,
							diary);
			}
		});

		// listview的item长按事件
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				final int long_click_position = position; // 记住长按的item下标
				final AlertDialog dialog;
				ListView listview_detail;
				List<Object> objectlist;
				List<String> str = new ArrayList<String>();

				if (view.getId() == R.id.activity_main_listview_diary_layout) {
					str.add("查看");
					str.add("删除");
					if (diarylist.get(long_click_position).getFlag()) {
						str.add("解锁");
					} else {
						str.add("加锁");
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
									if (position == 0) {// 查看操作
										if (diarylist.get(long_click_position)
												.getFlag()) { // 加了锁，要输入密码才能查看
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
																"密码错误",
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
											// 创建输入密码对话框
											EditActivity.showLockAlertDialog(
													MainActivity.this, "请输入密码",
													view1, positivelistener,
													negativelistener);
										} else {
											EditActivity
													.startEditActivityForResult(
															MainActivity.this,
															diarylist
																	.get(long_click_position));
										}
									} else if (position == 1) {// 删除操作
										diarylist.get(long_click_position)
												.setFlag(false);
										diarylist.get(long_click_position)
												.setContent("");// 不能用diarylist.remove(int
																// index)，只需要清空content即可
										diaryadapter.notifyDataSetChanged();// 刷新listview
									} else if (position == 2) {// 加锁或解锁操作
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
																"密码错误，解锁失败",
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
													MainActivity.this, "取消密码：",
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
																"密码输入错误，设置失败",
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
													MainActivity.this, "设置密码",
													view1, positivelistener,
													negativelistener);
										}
									}
								}
							});
				} else {// 如果长按的是个点
					str.add("添加");

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
				return true;// 如果return false，则控件会响应长按和短按两个事件，true则长按会拦截短按，只响应长按
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

	// 检查输入的密码是否与该月已设置的密码相同
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
		if (diarylist != null) { // 如果日记列表不为空，则查看里面是否有内容，有则保存到文件
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
			} else { // 如果全部日记都是空的，则删除该月的日记文件
				File file = new File(getFilesDir().getPath() + "/"
						+ year_button.getText() + month_button.getText());
				if (file.exists())
					file.delete();
			}
		}
	}

	// 初始化日期，指示当前年月并显示该月日记内容
	private void initDiaryList() {
		Calendar calendar = Calendar.getInstance();
		changeDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
	}

	// 改变日期时的操作
	@SuppressWarnings("unchecked")
	public void changeDate(int year, int month) {
		// 如果日记列表不为空，则查看里面是否有内容，有则保存到文件，没有则删除文件
		if (diarylist != null) {
			Diary temp;
			int i;
			for (i = 0; i < diarylist.size(); i++) {
				temp = diarylist.get(i);
				if (!temp.getContent().equals(""))
					break;
			}
			// 日记列表中有内容，要保存到文件
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
				} catch (Exception exp) {// 保存不了属于异常
					// Toast.makeText(this, "Diary saved failed",
					// Toast.LENGTH_LONG).show();
				}
			} else {// 如果没有日记内容，则不用保存，删掉本地文件
				File file = new File(getFilesDir().getPath() + "/"
						+ year_button.getText() + month_button.getText());
				if (file.exists())
					file.delete();
			}
		}
		// 设置并读取新时间的日记
		month_button.setText(this.month[month]);
		year_button.setText(String.valueOf(year));
		FileInputStream in;
		ObjectInputStream objectin;
		// 打开文件并读取对象流保存到日记列表中，若没有该月的文件，则新建一个空的日记列表
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

	// 根据年份和月份计算当月的天数，月份以0开始算
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

	// 根据传入的字符串集合创建一个含有listview的alertdialog，并用集合的方式返回listview和alertdialog以供之后操作
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
