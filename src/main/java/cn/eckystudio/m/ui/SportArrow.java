package cn.eckystudio.m.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class SportArrow extends FrameLayout {

	private final static int DEFAULT_ROTATION_DEG = 30;
	private final static float DEFAULT_SCALE_RADIO = 1.21f;
	private final static int A_LENGTH = 49;//81;
	private final static int A_HEIGHT = 8;
	private final static int COLOR = 0xffffffff;
	private final static int A_PADDING = A_LENGTH /2 + 2;//A_HEIGHT + 1;

	View mLeft;
	View mRight;
	
	AnimatorSet mAnimSetShrink;
	AnimatorSet mAnimSetExpand;
	
	float mShrinkPercent;
	
	public SportArrow(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setWillNotDraw(false);
		
		LayoutParams params = new LayoutParams(A_LENGTH, A_HEIGHT);	
		
		float pivotX,pivotY;
		pivotX = calPivot(A_LENGTH);
		pivotY = calPivot(A_HEIGHT);

		//add left view
		mLeft = new View(context);
		addView(mLeft,params);
//			mLeft.setBackgroundColor(COLOR);
		mLeft.setX(A_PADDING);
		mLeft.setY(A_PADDING);
		mLeft.setPivotX(pivotX);
		mLeft.setPivotY(pivotY);
//			mLeft.setRotation(30);
//			mLeft.setScaleX(scaleX);
	
		//add right view
		mRight = new View(context);
		addView(mRight,params);
//			mRight.setBackgroundColor(COLOR);

		mRight.setX(A_PADDING + A_LENGTH);
		mRight.setY(A_PADDING);
		mRight.setPivotX(pivotX);
		mRight.setPivotY(pivotY);
//			mRight.setRotation(-180);
//			mRight.setScaleX(scaleX);
//			System.out.println("?=>liangyi = " + pivotX + ",y = " + pivotY);
		
		//init animation
		mAnimSetShrink = new AnimatorSet();
		mAnimSetExpand = new AnimatorSet();
		
		ObjectAnimator animLeft,animRight;
		animLeft = ObjectAnimator.ofPropertyValuesHolder(mLeft, 
				PropertyValuesHolder.ofFloat("scaleX", 1f,DEFAULT_SCALE_RADIO),
				PropertyValuesHolder.ofFloat("rotation", 0,-DEFAULT_ROTATION_DEG));
		
		animRight = ObjectAnimator.ofPropertyValuesHolder(mRight, 
				PropertyValuesHolder.ofFloat("scaleX", 1f,DEFAULT_SCALE_RADIO),
				PropertyValuesHolder.ofFloat("rotation", 0,DEFAULT_ROTATION_DEG));
		mAnimSetShrink.playTogether(animLeft,animRight);
		
		animLeft = ObjectAnimator.ofPropertyValuesHolder(mLeft, 
				PropertyValuesHolder.ofFloat("scaleX", DEFAULT_SCALE_RADIO,1f),
				PropertyValuesHolder.ofFloat("rotation",-DEFAULT_ROTATION_DEG, 0));
		animRight = ObjectAnimator.ofPropertyValuesHolder(mRight, 
				PropertyValuesHolder.ofFloat("scaleX", DEFAULT_SCALE_RADIO,1f),
				PropertyValuesHolder.ofFloat("rotation",DEFAULT_ROTATION_DEG , 0));
		mAnimSetExpand.playTogether(animLeft,animRight);	
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int mode = MeasureSpec.getMode(heightMeasureSpec);
		if(mode == MeasureSpec.AT_MOST)
		{
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(getMinHeight(), MeasureSpec.EXACTLY);		
		}
		
		mode = MeasureSpec.getMode(widthMeasureSpec);
		if(mode == MeasureSpec.AT_MOST)
		{
			widthMeasureSpec = MeasureSpec.makeMeasureSpec(getMinWidth(), MeasureSpec.EXACTLY);	
		}
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		BitmapDrawable bg = makeEllipseBackground();
		mLeft.setBackground(bg);
		mRight.setBackground(mirrorHorizontal(bg.getBitmap()));
		
		super.onDraw(canvas);
	}
	
	public void play()
	{
		if(mLeft.getScaleX()>1)
		{
			expand();
		}else
		{
			shrink();
		}
	}
	
	public void playContinue()
	{
		;
	}
	
	public void shrink()
	{		
		mAnimSetShrink.cancel();
		mAnimSetShrink.start();
	}
	
	public void expand()
	{
//			ObjectAnimator scaleLeftAnim = ObjectAnimator.ofFloat(mLeft,"scaleX",1.18f,1f);
//			ObjectAnimator rotateLeftAnim = ObjectAnimator.ofFloat(mLeft, "rotation", 30,0);
//			
//			ObjectAnimator scaleRightAnim = ObjectAnimator.ofFloat(mRight,"scaleX",1.18f,1f);
//			ObjectAnimator rotateRightAnim = ObjectAnimator.ofFloat(mRight, "rotation",-210,-180);
//			
//			AnimatorSet animSet = new AnimatorSet();
//			animSet.playTogether(scaleLeftAnim,rotateLeftAnim,
//					scaleRightAnim,rotateRightAnim);
//			animSet.start();
		mAnimSetExpand.cancel();
		mAnimSetExpand.start();
	}
	
	public void setShrink(float percent)
	{
		float scaleX = 1f + (DEFAULT_SCALE_RADIO - 1) * percent;
		float rotation = - DEFAULT_ROTATION_DEG * percent;
		
		mLeft.setScaleX(scaleX);
		mLeft.setRotation(rotation);
		
		rotation = DEFAULT_ROTATION_DEG * percent;
		mRight.setScaleX(scaleX);
		mRight.setRotation(rotation);
		
		mShrinkPercent = percent;
	}
	private float calPivot(int size)
	{	
		return (float)(size / 2.0f);
	}
	
	public int getMinHeight()
	{
		return A_PADDING *2 + A_HEIGHT;
	}
	
	public int getMinWidth()
	{
		return A_LENGTH *2 + A_PADDING * 2;
	}
	
	private BitmapDrawable makeEllipseBackground()
	{
		Rect r = new Rect();
		mLeft.getHitRect(r);
//			mLeft.getGlobalVisibleRect(r);
		int w = r.right - r.left;
		int h = r.bottom - r.top;
		
		Bitmap bitmap = Bitmap.createBitmap(w,h, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
//			canvas.setBitmap(bitmap);
		
		Paint p = new Paint();
		p.setColor(COLOR);
//			p.setDither(true);
		p.setAntiAlias(true);
		
		RectF rf = new RectF();
		rf.left = 0;
		rf.right = w/2.0f;
		rf.top = 0;
		rf.bottom = h;
		
		canvas.drawRoundRect(rf, 8f, 8f, p);
		
		rf.left = w/3.0f;
		rf.right = w;
		canvas.drawRect(rf,p);
		
		BitmapDrawable d = new BitmapDrawable(getContext().getResources(), bitmap);
		return d;
	}
	
	public BitmapDrawable mirrorHorizontal(Bitmap b)
	{
		int w = b.getWidth();
		int h = b.getHeight();
//			Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
//			Canvas cv = new Canvas(newb);
		Matrix m = new Matrix();
//			m.postScale(1, -1);   //镜像垂直翻转
		m.postScale(-1, 1);   //镜像水平翻转
//			m.postRotate(-90);  //旋转-90度
		Bitmap new2 = Bitmap.createBitmap(b, 0, 0, w, h, m, true);
//			cv.drawBitmap(new2, new Rect(0, 0, new2.getWidth(), new2.getHeight()),new Rect(0, 0, ww, wh), null);
//			return newb;
		return new BitmapDrawable(getContext().getResources(), new2);
	}
}

