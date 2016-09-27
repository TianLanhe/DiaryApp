package com.example.diarypractice;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MonthDiaryAdapter extends ArrayAdapter<Diary> {
	private int resourceID;
	private String[] week = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
			"Friday", "Saturday" };
	public MonthDiaryAdapter(Context context, int resource, List<Diary> objects) {
		super(context, resource, objects);
		resourceID=resource;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder viewholder;
		Diary diary=getItem(position);
		if(convertView==null){
			view=LayoutInflater.from(getContext()).inflate(resourceID, null);
			viewholder=new ViewHolder();
			viewholder.textview=(TextView) view.findViewById(R.id.listview_detail_textview);
			viewholder.textview.setTextSize(15);
			viewholder.textview.setGravity(Gravity.LEFT|Gravity.TOP	);
			view.setTag(viewholder);
		}else{
			view=convertView;
			viewholder=(ViewHolder) view.getTag();
		}
		viewholder.textview.setText(diary.getDate()+" "+week[diary.getWeek()]+" / "+diary.getContent());
		if(diary.getWeek()==0 || diary.getWeek()==6){
			String text= viewholder.textview.getText().toString();
			SpannableStringBuilder builder=new SpannableStringBuilder(text);
			ForegroundColorSpan colorspan=new ForegroundColorSpan(Color.RED);
			builder.setSpan(colorspan,text.indexOf(" ")+1,text.indexOf("/",text.indexOf(" ")),SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
			viewholder.textview.setText(builder);
		}
		return view;
	}
}
