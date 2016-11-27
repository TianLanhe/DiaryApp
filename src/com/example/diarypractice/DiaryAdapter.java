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
	private int resourceID_diary; // �Ѵ����ռǵĲ���
	private int resourceID_point; // ��δ����ռǵĲ���
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
		// ���������Ҫ���¼��ز���
		// ��һ�֣���һ�μ��ز���
		// �ڶ��֣����沼��������Ҫ��ӵĲ��ֲ�ͬ
		// �п����ϴ��ǵ㲼�ֶ�������ռǲ���
		// Ҳ�п����ϴ����ռǲ��ֶ�����ǵ㲼��
		if (convertView == null
				|| convertView.getId() == R.id.activity_main_listview_diary_layout
				&& diary.getContent().equals("")
				|| convertView.getId() == R.id.activity_main_listview_point_layout
				&& !diary.getContent().equals("")) {

			// ���û���ռ����ݣ�����ص㲼�֣�����viewhold_point
			if (diary.getContent().equals("")) {
				view = LayoutInflater.from(getContext()).inflate(
						resourceID_point, null);
				viewholder_point = new ViewHolder_Point();
				viewholder_point.imageview_point = (ImageView) view
						.findViewById(R.id.listview_point_imageview);
				view.setTag(viewholder_point);
			} else {
				// ������ռ����ݣ���ʾ�����ռǴ��ڣ�����diary���֣�����viewholder_diary
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
		} else {// ������ϴβ�����ͬ������ػ��岼�֣�ʹ��viewholder�Ŀؼ�
			view = convertView;
			if (!diary.getContent().equals(""))
				viewholder_diary = (ViewHolder_Diary) view.getTag();
			else
				viewholder_point = (ViewHolder_Point) view.getTag();
		}
		// �Բ�����Ŀؼ����д���
		// ����ǵ㲼�֣���������ڸı�����ɫ
		if (diary.getContent().equals("")) {
			if (diary.getWeek() == 0 || diary.getWeek() == 6)
				// setColorFilter����ͼƬ��Ⱦɫ���൱�ڼ���һ������
				viewholder_point.imageview_point.setColorFilter(Color.RED);
			else
				viewholder_point.imageview_point.setColorFilter(Color.BLACK);
		} else {// ������ռǲ��֣������������������ݲ��������ڸı�������ɫ
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
