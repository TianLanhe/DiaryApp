package com.example.diarypractice;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StringAdapter extends ArrayAdapter<String> {
	private int resourceID;

	public StringAdapter(Context context, int resource, List<String> objects) {
		super(context, resource, objects);
		resourceID = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder viewholder;
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(resourceID, null);
			viewholder = new ViewHolder();
			viewholder.textview = (TextView) view
					.findViewById(R.id.listview_detail_textview);
			view.setTag(viewholder);
		} else {
			view = convertView;
			viewholder = (ViewHolder) view.getTag();
		}
		viewholder.textview.setText(getItem(position));
		return view;
	}
}

class ViewHolder {
	TextView textview;
}
