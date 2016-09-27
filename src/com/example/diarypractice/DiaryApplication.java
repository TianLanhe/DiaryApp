package com.example.diarypractice;

import android.app.Application;

public class DiaryApplication extends Application {
	int backgroundcolor;
	public int getColor(){
		return backgroundcolor;
	}
	public void setColor(int color){
		backgroundcolor=color;
	}
}
