package com.example.diarypractice;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ImageView;

public class DiaryAdapter extends ArrayAdapter<Diary> {
	private int resourceID_diary; // 已存在日记的布局
	private int resourceID_point; // 还未添加日记的布局
	private String[] weekday = { "SUN", "MON", "TUE", "WED", "THU", "FRI",
			"SAT" };

	public DiaryAdapter(Context context, int resource, int resource2,
			List<Diary> objects) {
		super(context, resource, objects);
		resourceID_diary = resource;
		resourceID_point = resource2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		Diary diary;
		ViewHolder_Diary viewholder_diary = null;
		ViewHolder_Point viewholder_point = null;
		diary = getItem(position);
		// 三种情况需要重新加载布局
		// 第一种：第一次加载布局
		// 第二种：缓存布局与现在要添加的布局不同
		// 有可能上次是点布局而这次是日记布局
		// 也有可能上次是日记布局而这次是点布局
		if (convertView == null
				|| convertView.getId() == R.id.activity_main_listview_diary_layout
				&& diary.getContent().equals("")
				|| convertView.getId() == R.id.activity_main_listview_point_layout
				&& !diary.getContent().equals("")) {

			// 如果没有日记内容，则加载点布局，设置viewhold_point
			if (diary.getContent().equals("")) {
				view = LayoutInflater.from(getContext()).inflate(
						resourceID_point, null);
				viewholder_point = new ViewHolder_Point();
				viewholder_point.imageview_point = (ImageView) view
						.findViewById(R.id.listview_point_imageview);
				view.setTag(viewholder_point);
			} else {
				// 如果有日记内容，表示当日日记存在，加载diary布局，设置viewholder_diary
				view = LayoutInflater.from(getContext()).inflate(
						resourceID_diary, null);
				viewholder_diary = new ViewHolder_Diary();
				viewholder_diary.textview_content = (TextView) view
						.findViewById(R.id.listview_content_textview);
				viewholder_diary.textview_date = (TextView) view
						.findViewById(R.id.listview_date_textview);
				viewholder_diary.textview_week = (TextView) view
						.findViewById(R.id.listview_week_textview);
				viewholder_diary.imageview_lock = (ImageView) view
						.findViewById(R.id.listview_lock_imageview);
				view.setTag(viewholder_diary);
			}
		} else {// 如果与上次布局相同，则加载缓冲布局，使用viewholder的控件
			view = convertView;
			if (!diary.getContent().equals(""))
				viewholder_diary = (ViewHolder_Diary) view.getTag();
			else
				viewholder_point = (ViewHolder_Point) view.getTag();
		}
		// 对布局里的控件进行处理
		// 如果是点布局，则根据星期改变点的颜色
		if (diary.getContent().equals("")) {
			if (diary.getWeek() == 0 || diary.getWeek() == 6)
				// setColorFilter设置图片渲染色，相当于加了一层遮罩
				viewholder_point.imageview_point.setColorFilter(Color.RED);
			else
				viewholder_point.imageview_point.setColorFilter(Color.BLACK);
		} else {// 如果是日记布局，则设置年月日与内容并根据星期改变日期颜色
			if (diary.getFlag()) {
				viewholder_diary.textview_content.setText("");
				viewholder_diary.imageview_lock.setVisibility(View.VISIBLE);
			} else {
				viewholder_diary.textview_content.setText(diary.getContent());
				viewholder_diary.imageview_lock.setVisibility(View.GONE);
			}

			viewholder_diary.textview_date.setText(String.valueOf(diary
					.getDate()));
			viewholder_diary.textview_week.setText(weekday[diary.getWeek()]);
			if (diary.getWeek() == 0 || diary.getWeek() == 6)
				viewholder_diary.textview_date.setTextColor(Color.RED);
			else
				viewholder_diary.textview_date.setTextColor(Color.BLACK);
		}
		return view;
	}
}

class ViewHolder_Diary {
	TextView textview_week;
	TextView textview_date;
	TextView textview_content;
	ImageView imageview_lock;
}

class ViewHolder_Point {
	ImageView imageview_point;
}
