package com.example.diarypractice;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class HorizontalListView extends LinearLayout {
	public HorizontalListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.horizontallayout, this);
		for(int i=0;i<12;i++)
			addView(null);
	}
}
