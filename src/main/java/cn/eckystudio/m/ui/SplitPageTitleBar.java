/*
 * Copyright (C) 2017 Ecky Leung
 * Author:Ecky Leung(liangyi)
 * Creation Date:2016-1-23
 * Function:provide some usual methods about Screen,Layout to use easily
 *
 * Use single instance
 */

package cn.eckystudio.m.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import cn.eckystudio.m.R;

public class SplitPageTitleBar extends View {

	private int mViewPagerInitId = -1;
	private Drawable mIndicator = null;
	
	private int mCount;
	private CharSequence[] mTitles;
	private float mPageTitleWidth;
	
	private Paint mPaint;
	private FontMetricsInt mFmInt;
	
	private float mOffsetX = 0;
	
	private ViewPager mViewPager = null;
	
	public SplitPageTitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SplitPageTitleBar,0,R.style.DefaultSplitPageTitleBar);
		mViewPagerInitId = a.getResourceId(R.styleable.SplitPageTitleBar_bindTo, -1);
		mIndicator = a.getDrawable(R.styleable.SplitPageTitleBar_indicator);
		
		a.recycle();
		
//		if(context instanceof Activity)
//		{
//			Activity activity = (Activity)context;
//			activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		}
		
		mPaint = new Paint();
		mPaint.setColor(Color.BLACK);
		mPaint.setTextSize(40);
		mPaint.setAntiAlias(true);
	}
	
	public void bindTo(ViewPager pager)
	{
		pager.setOnPageChangeListener(mListener);
	}
	
	private ViewPager.OnPageChangeListener mListener = new ViewPager.OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			System.out.println("liangyi arg0="+arg0+",arg1="+arg1+",arg2="+arg2);
			float offsetWidth = mPageTitleWidth * arg1;
			mOffsetX = mPageTitleWidth * arg0 + offsetWidth;
			invalidate();
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch(action)
		{
		case MotionEvent.ACTION_UP:
			if(mViewPager != null)
			{
				int i = isInRect(event.getX(),event.getY());
				mViewPager.setCurrentItem(i, true);
			}
			break;
		}
		return true;		
	};
	
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {	
		if(changed)
		{
			//bind to ViewPager
			if(mViewPagerInitId != -1)
			{
				ViewParent parent = getParent();
				if(parent != null && (parent instanceof ViewGroup))
				{
					View view = ((ViewGroup)parent).findViewById(mViewPagerInitId);
					if(view != null && view instanceof ViewPager)
					{
						mViewPager = (ViewPager)view;
						bindTo(mViewPager);
						PagerAdapter adapter = mViewPager.getAdapter();
						mCount = adapter.getCount();
						mTitles = new CharSequence[mCount];
						for(int i=0;i<mCount;i++)
						{
							if(adapter!=null)
							{
								mTitles[i] = adapter.getPageTitle(i);
							}else {
								mTitles = null;
							}						
						}
						mFmInt = mPaint.getFontMetricsInt();
					}
				}
				mViewPagerInitId = -1;
			}		
			mPageTitleWidth = (right - left) / mCount;
		}
		System.out.println("#"+left + "#"+top +"#"+right + "#"+bottom+"#"+changed);
		
		super.onLayout(changed, left, top, right, bottom);	
	};
	
	private int isInRect(float x,float y)
	{
		int i = 1;
	    float xPos;
		
		do {
			xPos = i * mPageTitleWidth;
			if(x < xPos)
				break;
			
			++i;
		} while (true);
		return i-1;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		System.out.println("onDraw");
		super.onDraw(canvas);

		canvas.save();
		if(mCount != 0 && mTitles != null)
		{
			int height = getHeight();		
			float textWidth;
			float hPos,vPos;
			for(int i=0;i<mCount;i++)
			{
				textWidth = mPaint.measureText((String)mTitles[i]);
				vPos = calVPos(height,mFmInt);
				hPos = calHPos(mPageTitleWidth, textWidth, i);
				canvas.drawText((String)mTitles[i], hPos, vPos, mPaint);
			}
					
		}	
		
		Paint paint = new Paint();
		paint.setColor(Color.argb(50, 0, 0, 255));
		RectF rect = new RectF(mOffsetX, 0, mOffsetX + mPageTitleWidth, getHeight());
		canvas.drawRect(rect, paint);
		canvas.restore();
	}
	
	private float calHPos(float containerWidth,float textWidth,int pos)
	{
		float leftDistance = (containerWidth - textWidth)/2;
		return containerWidth * pos + leftDistance;
	}
	
	public static float calVPos(int containerHeight,FontMetricsInt fmi)
	{
//		System.out.println("#" + fmi.top + "#"+fmi.bottom
//				+"#"+fmi.ascent + "#"+fmi.descent + "#"+fmi.leading);
		
		int textHeight = fmi.bottom - fmi.top;
		return (containerHeight - textHeight)/2.0f  - fmi.top;//return baseline pos
	}

}
