/*
 * Copyright (C) 2017 Freetel RD Center
 * Author:Ecky Leung(liangyi)
 * Creation Date:2017-1-23
 * Function:provide some usual methods about Screen,Layout to use easily
 *
 * Use single instance
 */

package cn.eckystudio.m.ui;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class Display {
	static Display sDisplay;
	DisplayMetrics mDm = new DisplayMetrics();

	static Display getInstance(Context context){
		if(sDisplay == null){
			sDisplay = new Display(context);
		}
		return sDisplay;
	}

	public Display(Context context)
	{
		init(context);
	}

	private void init(Context context){
		WindowManager wm = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE));
		wm.getDefaultDisplay().getMetrics(mDm);
	}

	public int dp2pixel(int sizeInDp) throws Exception
	{
		return (int)(mDm.density * sizeInDp + 0.5);
	}

//	public static int dp2pixel(Context context,int sizeInDp) throws Exception
//	{
//		if(sDm == null) {
//			if(context == null){
//				throw new Exception("context is null!!");
//			}
//			WindowManager wm = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
//			wm.getDefaultDisplay().getMetrics(sDm);
//			sDensity = sDm.density;
//		}
//		return dp2pixel(sizeInDp);
//	}

	public  int getScreenHeight(){
		return mDm.heightPixels;
	}

	public  int getScreenWidth()
	{
		return mDm.widthPixels;
	}
}
