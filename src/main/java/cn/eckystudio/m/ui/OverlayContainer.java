package cn.eckystudio.m.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import cn.eckystudio.m.R;

public class OverlayContainer extends FrameLayout {
    ViewGroup mFakeLeftContainer;
    View mLeftView;
    Rect mRectLeftView = new Rect();

    AnimatorSet mAnimatorSetShow;
    AnimatorSet mAnimatorSetHide;

    public OverlayContainer(Context context) {
        this(context,null);
    }

    public OverlayContainer(Context context, AttributeSet attrs) {
        super(context, attrs);

        if(attrs != null) {
            Resources.Theme theme = context.getTheme();

            int base_len = R.styleable.OverlayContainer.length;
            int[] attr_arr = new int[base_len];

            for (int i = 0; i < base_len; i++) {
                attr_arr[i] = R.styleable.OverlayContainer[i];
            }

            TypedArray a = theme.obtainStyledAttributes(attrs, attr_arr, 0, 0);

            int mainId = a.getResourceId(R.styleable.OverlayContainer_mainLayout, 0);
            if(mainId != 0)
                this.addView(View.inflate(context,mainId,null));

            int leftId = a.getResourceId(R.styleable.OverlayContainer_leftLayout, 0);
            if (leftId != 0) {
                mLeftView = View.inflate(context, leftId, null);

                //init view
                initView();
                makeAnimator();
            }
            a.recycle();
        }
    }


    private void initView(){
        int height = Display.getInstance(getContext()).getScreenHeight();
        int width = Display.getInstance(getContext()).getScreenWidth();

        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,MeasureSpec.AT_MOST);
        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(width,MeasureSpec.AT_MOST);
        mLeftView.measure(widthMeasureSpec,heightMeasureSpec);

        mRectLeftView.right = mLeftView.getMeasuredWidth();
        mRectLeftView.bottom = height;

        mFakeLeftContainer = new FrameLayout(getContext());
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width,height);
//        addView(mFakeLeftContainer,params);
        addView(mFakeLeftContainer);

        mFakeLeftContainer.addView(mLeftView);
        mFakeLeftContainer.setBackgroundColor(0xff000000);
        mFakeLeftContainer.setClickable(true);
        mFakeLeftContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideLeftContainer();
            }
        });

        //设置mFakeLeftContainer状态
        mFakeLeftContainer.setVisibility(View.GONE);

        mLeftView.setX(-mRectLeftView.right);//设置当前位置
        //设置leftView的点击位置
//        mLeftView.setLeft(mRectLeftView.left);
//        mLeftView.setTop(mRectLeftView.top);
//        mLeftView.setRight(mRectLeftView.right);
//        mLeftView.setBottom(mRectLeftView.bottom);

        mLeftView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                System.out.println("?=>liangyi mLeftView onclick !!" +  v.getLeft() + v.getTop() + v.getRight() + v.getBottom());
                //设置leftView的点击位置
                v.setLeft(mRectLeftView.left);
                v.setTop(mRectLeftView.top);
                v.setRight(mRectLeftView.right);
                v.setBottom(mRectLeftView.bottom);
            }
        });

        mLeftView.setClickable(true);
//        mLeftView.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("?=>liangyi mLeftView onclick !!" +  v.getLeft() + v.getTop() + v.getRight() + v.getBottom());
//
//
//            }
//        });
    }

    private void makeAnimator(){
        mAnimatorSetShow = new AnimatorSet();
        Animator animatorLeftView = ObjectAnimator.ofFloat(mLeftView,"x",-mRectLeftView.right,0);
//        Animator animatorFakeView = ObjectAnimator.ofFloat(mFakeLeftContainer,"alpha",0,0.5f);
        Animator animatorFakeView = ObjectAnimator.ofArgb(mFakeLeftContainer,"backgroundColor",0x00000000,0x80000000);

        mAnimatorSetShow.playTogether(animatorLeftView,animatorFakeView);

        mAnimatorSetHide = new AnimatorSet();
        animatorLeftView = ObjectAnimator.ofFloat(mLeftView,"x",0,-mRectLeftView.right);
//        animatorFakeView = ObjectAnimator.ofFloat(mFakeLeftContainer,"alpha",mFakeLeftContainer.getAlpha(),0);
        animatorFakeView = ObjectAnimator.ofArgb(mFakeLeftContainer,"backgroundColor",0x80000000,0x00000000);
        mAnimatorSetHide.playTogether(animatorLeftView,animatorFakeView);

        mAnimatorSetHide.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mFakeLeftContainer.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimatorSetShow.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height =  MeasureSpec.getSize(heightMeasureSpec);

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,MeasureSpec.AT_MOST);
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width,MeasureSpec.AT_MOST);

        //设置大小
        ViewGroup.LayoutParams params = mFakeLeftContainer.getLayoutParams();
        params.height = height;
        params.width = width;
        mFakeLeftContainer.setLayoutParams(params);
        mRectLeftView.bottom = height;

//        System.out.println("?=>liangyi onMeasure" + MeasureSpec.getSize(widthMeasureSpec) + "," + MeasureSpec.getSize(heightMeasureSpec) + ","+getMeasuredWidth() + getMeasuredHeight());
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        System.out.println("?=>liangyi onLayout");
        super.onLayout(changed, left, top, right, bottom);
    }

    public void showLeftContainer(){
        mFakeLeftContainer.bringToFront();
        mFakeLeftContainer.setVisibility(View.VISIBLE);
        mAnimatorSetShow.start();
    }

    public void hideLeftContainer(){
        mAnimatorSetHide.cancel();
        mAnimatorSetHide.start();
    }

//    @Override
//    protected void onVisibilityChanged(View changedView, int visibility) {
//        super.onVisibilityChanged(changedView, visibility);
//    }
}
