package cn.eckystudio.m.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import cn.eckystudio.m.R;

public class ImageWithTextView extends View {
//	final static int PADDING = 20;
//	final int PADDING_TOP = 54;
	
	int mTextColor,mTextPressedColor;
	ColorStateList mTextColorList;
	Drawable mIcon;
	
	String mText;
	String[] mTextMultiLine;
	
	int mTextSize;
	String mFontFamily;
	
	Paint mPaint = new Paint();
	
	int mContentWidth = 0,mContentHeight = 0;
	
	public ImageWithTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		Theme theme = context.getTheme();
		
		int base_len = R.styleable.ImageWithTextView.length;
		int[] attr_arr = new int[ base_len + 3];
		
		for(int i=0;i<base_len;i++)
		{
			attr_arr[i] = R.styleable.ImageWithTextView[i];
		}
		//@Ecky Leung note: the same zone attrs must be from small to large sort ,Or the res can not be found. Maybe it's a bug for ResourceManager 
		final int TEXT_SIZE_INDEX = base_len;
		final int TEXT_COLOR_LIST_INDEX = base_len+1;
		final int TEXT_FONT_FAMILY_INDEX = base_len+2;
		
		attr_arr[TEXT_SIZE_INDEX] = android.R.attr.textSize; 
		attr_arr[TEXT_COLOR_LIST_INDEX] = android.R.attr.textColor;
		attr_arr[TEXT_FONT_FAMILY_INDEX] = android.R.attr.fontFamily; 
		
		TypedArray a = theme.obtainStyledAttributes(attrs, attr_arr, 0, 0);
				
		mTextColor = a.getColor(R.styleable.ImageWithTextView_textColor, 0xff000000); //a.getAttributeIntValue(R.styleable.ImageWithTextView_textColor, 0xff000000);
		mTextPressedColor = a.getColor(R.styleable.ImageWithTextView_textPressedColor, 0xffeeeeee);//attrs.getAttributeIntValue(R.styleable.ImageWithTextView_textPressedColor, 0xffeeeeee);
		
		int imgResId = a.getResourceId(R.styleable.ImageWithTextView_image, 0);//attrs.getAttributeResourceValue(R.styleable.ImageWithTextView_image, 0);
		if(imgResId != 0)
		{
			mIcon = context.getResources().getDrawable(imgResId, theme);
		}
		
		mText = a.getString(R.styleable.ImageWithTextView_text);//attrs.getAttributeValue(R.styleable.ImageWithTextView_text);
		
		mTextSize = a.getDimensionPixelSize(TEXT_SIZE_INDEX, 56);
		mFontFamily = a.getString(TEXT_FONT_FAMILY_INDEX);
		
		mTextColorList = a.getColorStateList(TEXT_COLOR_LIST_INDEX);
		if(mTextColorList != null && mTextColorList.isStateful())
		{
			mTextColor =  mTextColorList.getColorForState(new int[]{-android.R.attr.state_pressed}, mTextColor);
			mTextPressedColor = mTextColorList.getColorForState(new int[]{android.R.attr.state_pressed}, mTextPressedColor);
		}
		
		a.recycle();
		
		mPaint.setTextSize(mTextSize);
		mPaint.setAntiAlias(true);
		//mPaint.setTypeface(typeface);
		setClickable(true);
		
		mTextMultiLine = mText.split("\n");
	}
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
//		Rect r = new Rect();
//		mPaint.getTextBounds(mText, 0, mText.length(), r);
		
		System.out.println("?=>liangyi widthmode = " + MeasureSpec.getMode(widthMeasureSpec)+ ",size=" + MeasureSpec.getSize(widthMeasureSpec));
		System.out.println("?=>liangyi heightmode = " + MeasureSpec.getMode(heightMeasureSpec)+",size=" + MeasureSpec.getSize(heightMeasureSpec));
		
		int width = mIcon.getIntrinsicWidth();
		int textWidth = textMaxWidth();//r.width();
		width = width > textWidth ? width:textWidth;
		width += 2 * getPaddingLeft();
		
		if(MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY)
		{
			mContentWidth = 0;
			widthMeasureSpec = MeasureSpec.makeMeasureSpec(width,MeasureSpec.getMode(widthMeasureSpec));
		}else
		{
			mContentWidth = width;
		}
		
		FontMetricsInt fmi = mPaint.getFontMetricsInt();
		int height = mIcon.getIntrinsicHeight();
		int textHeight = (fmi.bottom - fmi.top) * mTextMultiLine.length;		
		height += textHeight;
		height += getPaddingTop();
		if(MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY)
		{
			mContentHeight = 0;
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,MeasureSpec.getMode(heightMeasureSpec));	
		}else
		{
			mContentHeight = height;
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.draw(canvas);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		boolean ret = super.onTouchEvent(event);
		switch(event.getAction())
		{
		case MotionEvent.ACTION_UP:
			setPressed(false);
			break;
		case MotionEvent.ACTION_DOWN:
			setPressed(true);
			break;
		}
		changeIconState();
		invalidate();
		return ret;
	}
	
	private void changeIconState()
	{
		if(mIcon != null && mIcon instanceof StateListDrawable)
		{
			if(isPressed())
			{
				((StateListDrawable)mIcon).setState(new int[]{android.R.attr.state_pressed});
			}else
			{
//				((StateListDrawable)mIcon).selectDrawable(android.R.attr.normal);
				((StateListDrawable)mIcon).setState(new int[]{-android.R.attr.state_pressed});
			}
		}
	}
	
	private int textMaxWidth()
	{
		float max = 0,width;
		for(int i=0;i<mTextMultiLine.length;i++)
		{
			width = mPaint.measureText(mTextMultiLine[i]);
			max = width >max ? width : max;
		}
		return (int)(max + 0.999999);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
//		super.onDraw(canvas);
		canvas.save();
		Rect r = new Rect(0,0,0,0);
		if(mIcon != null)
		{
			r.left = (getMeasuredWidth() - mIcon.getIntrinsicWidth()) / 2 + getPaddingLeft();
			r.top = mContentHeight == 0 ?  getPaddingTop():(getPaddingTop() + (getMeasuredHeight() - mContentHeight)/2);
			r.right = r.left + mIcon.getIntrinsicWidth();
			r.bottom = r.top + mIcon.getIntrinsicHeight();
			mIcon.setBounds(r);
			mIcon.draw(canvas);
		}
		
		FontMetricsInt fmi = mPaint.getFontMetricsInt();	
		if(isPressed())
		{
			mPaint.setColor(mTextPressedColor);
		}else
		{
			mPaint.setColor(mTextColor);
		}
		
		int base_top =  r.bottom - fmi.top;
		int textHeight = fmi.descent - fmi.ascent;
		float width;
		for(int i=0;i<mTextMultiLine.length;i++)
		{
			width = mPaint.measureText(mTextMultiLine[i]);
			float x = (getMeasuredWidth() - width)/2 + getPaddingLeft();
			float y = base_top + textHeight*i;
			canvas.translate(x ,y);
			canvas.drawText(mTextMultiLine[i], 0, 0, mPaint);
			canvas.translate(-x,-y);
		}
		
		canvas.restore();
	}
}
