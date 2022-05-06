package cn.eckystudio.m.ui;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import cn.eckystudio.m.log.Log;

/**
 * Created by Ecky on 2018/1/11.
 */

public class FloatWindowHelper {
    Context mContext;
    WindowManager mWinMan;

    View mTargetView;
    WindowManager.LayoutParams mLayoutParams;
    boolean mIsShowing = false;

    int mDoubleClickInterval = 100;
    ActionListener mDoubleClickAction;

    ActionListener mLongClickAction;

    ActionListener mTapAction;

    final static int DURATION_TAP = 300;


    int mPosX;
    int mPosY;
    /***
     * 位置类型，用于计算窗口位置
     */
    PostionType mPosType = PostionType.LEFT_ONE_FOURTHS;

    int mScreenWidth;
    int mScreenHeight;

    public FloatWindowHelper(Context context,int layoutId){
        mContext = context;
        mWinMan = (WindowManager) context.getSystemService(/*WindowManager.class*/Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        mTargetView = LayoutInflater.from(mContext).inflate(layoutId,null);

        DisplayMetrics dm = new DisplayMetrics();
        mWinMan.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;

        enableDragFeature();//enable drag feature
    }

    public void show(){
        if(mIsShowing)
            return;


//        if( (mLayoutParmas.flags& WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN) == 0){
//            mLayoutParmas.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
//        }
        //make sure the layout is based on screen
        mLayoutParams.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mLayoutParams.gravity = Gravity.LEFT|Gravity.TOP;//make sure the axis is based on left and top
        mLayoutParams.format = PixelFormat.RGBA_8888;

        mWinMan.addView(mTargetView,mLayoutParams);
        mIsShowing = true;

        //for initializing the position of mTargetView
        mTargetView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                int w = view.getMeasuredWidth();
                int h = view.getMeasuredHeight();

                switch(mPosType){
                    case CENTER:
                        mPosX = (int)((mScreenWidth - w )/2);
                        mPosY = (int)((mScreenHeight - h) /2);
                        break;
                    case LEFT_THREE_FOURTHS:
                        mPosY = (int)(mScreenHeight * 0.75 - h / 2);
                        break;
                    case LEFT_HAFL:
                        mPosY = (int)((mScreenHeight - h) / 2);
                        break;
                    case LEFT_ONE_FOURTHS:
                        mPosY = (int)(mScreenHeight * 0.25 - h / 2);
                        break;
                    default:
                        break;
                }

                mLayoutParams.x = mPosX;
                mLayoutParams.y = mPosY;

                //save width and height
                mLayoutParams.width = w;
                mLayoutParams.height = h;

                mWinMan.updateViewLayout(mTargetView,mLayoutParams);

                view.removeOnLayoutChangeListener(this);
            }
        });
    }

    public void hide(){
        if(!mIsShowing)
            return;

        mWinMan.removeView(mTargetView);
        mIsShowing = false;
    }

    public WindowManager.LayoutParams getLayoutParams(){
        return mLayoutParams;
    }

    public View getTargetView()
    {
        return mTargetView;
    }

    private void enableDragFeature(){
        mTargetView.setOnTouchListener(new View.OnTouchListener() {
            float mOffsetX;
            float mOffsetY;

            long mStartTime;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        //handle drag
                        mOffsetX = motionEvent.getX();
                        mOffsetY = motionEvent.getY();

                        //handle double click event
                        if(mDoubleClickAction != null){
                            long end = System.currentTimeMillis() - mStartTime;
                            if (end < mDoubleClickInterval) {
                                mDoubleClickAction.action(view);
                            }
                        }
                        //save down time
                        mStartTime = System.currentTimeMillis();

                        //handle long click event
                        if(mLongClickAction != null){

                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //drag feature
                        mLayoutParams.x = (int)(motionEvent.getRawX() - mOffsetX);
                        mLayoutParams.y = (int)(motionEvent.getRawY() -  mOffsetY);
//                        if (Math.abs(mLayoutParmas.x) < 3 || Math.abs(mLayoutParmas.y) < 3)
//                            break;

                        mWinMan.updateViewLayout(view, mLayoutParams);
                        //drag feature end
                        break;
                    case MotionEvent.ACTION_UP:
                        //handle tap event
                        if(mTapAction != null) {
                            if ((System.currentTimeMillis() - mStartTime) < DURATION_TAP) {
                                mTapAction.action(mTargetView);
                            }
                        }

                        break;
                    default:
                        break;
                }

                Log.print(FloatWindowHelper.class ,motionEvent.getRawX(),motionEvent.getRawY(),motionEvent.getX(),motionEvent.getY());
                return false;
            }
        });
    }

    public void enableDoubleClickFeature(ActionListener listener){
        mDoubleClickAction = listener;
    }

    public void enableDoubleClickFeature(ActionListener listener,int intervalTime){
        mDoubleClickAction = listener;
        mDoubleClickInterval = intervalTime;
    }

    public void enableLongClickFeature(ActionListener listener){
        mLongClickAction = listener;
    }

    public void enableTapFeature(ActionListener listener){
        mTapAction = listener;
    }

    public static interface ActionListener{
        public void action(View v);
    }

    public enum PostionType{
        CENTER,//正中间
        LEFT_TOP,//左上角
        LEFT_HAFL,//靠左1/2
        LEFT_ONE_FOURTHS,//靠左1/4
        LEFT_THREE_FOURTHS,//靠左四分之三
        RIGHT_HAFL,//靠左1/2
        RIGHT_ONE_FOURTHS,//靠左1/4
        RIGHT_THREE_FOURTHS,//靠左四分之三
        /***
         * 自定义，需要调用setPosition(int x,int y) 进行指定
         */
        SEFF_DEF
    }
}
