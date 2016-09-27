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
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
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
	private ListView setting_listview;

	private List<Diary> monthdiarylist=null;
	private List<Diary> diarylist = null; // 一个月的日记内容
	private List<View> content_view_list; // 上面主视图的三个布局
	private List<View> button_view_list; // 下面操作视图的两个布局

	private DiaryAdapter diaryadapter; // listview的适配器
	private MonthDiaryAdapter monthdiaryadapter;

	private ImageButton new_button; // 新建/编辑当日日记的按钮
	private Button month_button; // 月份按钮
	private Button year_button; // 年份按钮
	private ImageButton view_button;// 切换另一视图的按钮
	private ImageButton setting_button;// 设置按钮

	private ViewPager content_viewpager;
	private ViewPager button_viewpager;

	private View diary_list_view; // 标准日记视图
	private View setting_list_view; // 设置视图
	private View month_diary_list_view; // 当月日记视图
	private View button_list_view; // 标准操作视图
	private View month_button_list_view; // 当月日记操作视图

	private String[] month = { "Jan", "Feb", "Mar", "Apr", "May", "June",
			"July", "Aug", "Sept", "Oct", "Nov", "Dec" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 取消屏幕顶标题栏
		setContentView(R.layout.activity_main); // 加载主布局

		// 上下两个viewpager初始化
		content_viewpager = (ViewPager) findViewById(R.id.activity_main_content_viewpager);
		button_viewpager = (ViewPager) findViewById(R.id.activity_main_button_viewpager);

		// 初始化待加入viewpager中的view
		diary_list_view = LayoutInflater.from(this).inflate(
				R.layout.viewpager_diary, null);
		button_list_view = LayoutInflater.from(this).inflate(
				R.layout.viewpager_button, null);
		setting_list_view = LayoutInflater.from(this).inflate(
				R.layout.viewpager_diary, null);
		month_diary_list_view = LayoutInflater.from(this).inflate(
				R.layout.viewpager_diary, null);
		month_button_list_view = LayoutInflater.from(this).inflate(
				R.layout.viewpager_month_button, null);

		// 上下两个viewpager的list初始化并添加内容
		content_view_list = new ArrayList<View>();
		content_view_list.add(month_diary_list_view);
		content_view_list.add(diary_list_view);
		content_view_list.add(setting_list_view);

		button_view_list = new ArrayList<View>();
		button_view_list.add(month_button_list_view);
		button_view_list.add(button_list_view);

		// 上下两个viewpager设置适配器，传入list，设置viewpager指向初始的标准操作视图
		content_viewpager.setAdapter(new MyPagerAdapter(content_view_list));
		button_viewpager.setAdapter(new MyPagerAdapter(button_view_list));
		content_viewpager.setCurrentItem(1);
		button_viewpager.setCurrentItem(1);

		listview = (ListView) diary_list_view
				.findViewById(R.id.activity_main_diary_listview);
		new_button = (ImageButton) button_list_view
				.findViewById(R.id.activity_main_create_button);
		month_button = (Button) button_list_view
				.findViewById(R.id.activity_main_month_button);
		year_button = (Button) button_list_view
				.findViewById(R.id.activity_main_year_button);
		view_button = (ImageButton) button_list_view
				.findViewById(R.id.activity_main_view_button);
		setting_button = (ImageButton) button_list_view
				.findViewById(R.id.activity_main_setting_button);

		view_listview=(ListView) month_diary_list_view.findViewById(R.id.activity_main_diary_listview);
		
		// diarylist = null; //已在成员变量定义时初始化
		initDiaryList();// 读取当日的日期并设置按钮，读取当月的日记

		// 设置listview，设置分割线为null，即无分割线，也可在xml里android:divider="@null"
		diaryadapter = new DiaryAdapter(this, R.layout.listview_diary,
				R.layout.listview_point, diarylist);
		listview.setAdapter(diaryadapter);
		listview.setDivider(null);
		
		getMonthDiary();
		Log.d("myfilter","size:"+monthdiarylist.size());
		monthdiaryadapter=new MonthDiaryAdapter(this,R.layout.listview_detail,monthdiarylist);
		view_listview.setAdapter(monthdiaryadapter);
		view_listview.setDivider(null);

		// new按钮单击事件
		new_button.setOnClickListener(new OnClickListener() {
			@Override
			// 跳转到当前年份和月份，并打开当前日期的日记编辑界面
			public void onClick(View arg0) {
				Calendar calendar = Calendar.getInstance();
				changeDate(calendar.get(Calendar.YEAR),
						calendar.get(Calendar.MONTH));
				content_viewpager.setCurrentItem(1);
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
								content_viewpager.setCurrentItem(1);
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
								content_viewpager.setCurrentItem(1);
								diaryadapter.notifyDataSetChanged();
							}
						});
			}
		});

		//setting按钮单击事件
		setting_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				content_viewpager.setCurrentItem(2);
			}
		});

		//视图切换按钮单击事件
		view_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				content_viewpager.setCurrentItem(0);
				button_viewpager.setCurrentItem(0);
			}
		});

		// listview的item单击事件
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Diary diary = diarylist.get(position);
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
										EditActivity
												.startEditActivityForResult(
														MainActivity.this,
														diarylist
																.get(long_click_position));
									} else if (position == 1) {// 删除操作
										diarylist.get(long_click_position)
												.setContent("");// 不能用diarylist.remove(int
																// index)，只需要清空content即可
										diaryadapter.notifyDataSetChanged();// 刷新listview
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
				// 使用下列语句可以自定义dialog的大小，但适配性很差很差
				// WindowManager.LayoutParams params = dialog.getWindow()
				// .getAttributes();
				// params.width = 300; //设置alertdialog的长度和宽度
				// params.height = 200;
				// dialog.getWindow().setAttributes(params);
				return true;// 如果return false，则控件会响应长按和短按两个事件，true则长按会拦截短按，只响应长按
			}
		});
	}

	private void getMonthDiary() {
		if(monthdiarylist==null){
			monthdiarylist=new ArrayList<Diary>();
		}else{
			monthdiarylist.clear();
		}
		for(int i=0;i<diarylist.size();i++)
			if(!diarylist.get(i).getContent().equals(""))
				monthdiarylist.add(diarylist.get(i));
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

	private void initDiaryList() {
		Calendar calendar = Calendar.getInstance();
		changeDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
	}

	// 改变日期时的操作
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
