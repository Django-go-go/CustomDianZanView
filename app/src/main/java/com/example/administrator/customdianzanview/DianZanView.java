package com.example.administrator.customdianzanview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 2017/10/16
 * Created by Administrator on 2017/10/15.
 */

public class DianZanView extends FrameLayout {

    private static final String TAG = "DianZanView";

    //数字上下滚动的偏移量
    private static final int TRANSLATION = 50;
    //最大的点赞次数
    private static final int MAXVALUE = 9999;

    //左侧点赞的图片
    private Bitmap mIsCanceledBitmap;
    private Bitmap mIsNotCanceledBitmap;

    private LinearLayout mContentView;
    private ImageView mImageView;
    private TextView mOverTextView;

    //用来存储数字
    private List<Integer> mNumbersList;
    //用来对应数字的TextView
    private List<TextView> mTextViews;
    private int number;
    private int len;

    //是否超过设定的最大值
    private boolean isOver = false;
    private boolean canceled = true;

    private ObjectAnimator animatorX;
    private ObjectAnimator animatorY;
    private ObjectAnimator animatorA;
    private AnimatorSet animatorSet1 = new AnimatorSet();
    private AnimatorSet animatorSet2 = new AnimatorSet();

    public DianZanView(Context context) {
        this(context, null);
    }

    public DianZanView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DianZanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mIsCanceledBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.changeuser);
        mIsNotCanceledBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.recorder);
        LayoutInflater.from(context).inflate(R.layout.layout, this, true);
        mContentView = (LinearLayout) findViewById(R.id.content);
        mImageView = (ImageView) findViewById(R.id.pic);
        mOverTextView = (TextView) findViewById(R.id.over);
        mImageView.setImageBitmap(mIsCanceledBitmap);
        mNumbersList = new ArrayList<>();
        mTextViews = new ArrayList<>();
    }

    /**
     * 创建TextView
     * @return
     */
    private TextView createTextView(){
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        textView.setLayoutParams(lp);
        textView.setTextColor(Color.GRAY);
        return textView;
    }

    /**
     * 点赞时增加的操作
     */
    private void increment(){
        number++;
        if (number > MAXVALUE || number < 0){
            isOver = true;
            return;
        } else {
            isOver = false;
        }
        boolean curlen = number == (int)Math.pow(10, len);
        if (!curlen){
            changeTextView(true);
        } else {
            increTextView();
        }
        Log.i(TAG, "increment: ");
    }

    /**
     * 当超过原有数字长度时的操作
     */
    private void increTextView() {
        mContentView.setTranslationY(0);
        mContentView.setAlpha(1f);
        mContentView.animate().alpha(0f).translationY(-50).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setNumber(number);
                mTextViews.add(0, createTextView());
                mContentView.addView(mTextViews.get(0), 0);
                Log.i(TAG, "onAnimationEnd: 1");
                for (int i = 0; i < len; i++){
                    TextView textView = mTextViews.get(i);
                    int curValue = mNumbersList.get(i);
                    textView.setAlpha(0f);
                    textView.setTranslationY(50f);
                    textView.setText("" + curValue);
                    textView.animate().translationY(0).alpha(1f);
                }

                mContentView.setTranslationY(0);
                mContentView.setAlpha(1f);
            }
        });
        Log.i(TAG, "increTextView: ");
    }

    /**
     * 取消点赞时的操作
     */
    private void decrement(){
        number--;
        if (number < 0 || number > MAXVALUE){
            isOver = true;
            return;
        } else {
            isOver = false;
        }
        boolean curlen = number+1 == (int)Math.pow(10, len-1);
        if (!curlen){
            changeTextView(false);
        } else {
            decreTextView();
        }
        Log.i(TAG, "decrement: ");
    }

    /**
     * 当数字改变时的操作
     * @param isAdd 是否是增加
     */
    private void changeTextView(boolean isAdd) {
        int num = number;
        int remainder;
        int cur;
        int size = mNumbersList.size()-1;
        final List<Integer> locationList = new ArrayList<>();
        final List<Integer> changeList = new ArrayList<>();
        while (num != 0){
            remainder = num % 10;
            cur = mNumbersList.get(size);
            if (remainder != cur){
                locationList.add(size);
                changeList.add(remainder);
            }
            num = (num - num%10) / 10;
            size--;
        }
        setNumber(number);
        Log.i(TAG, "changeTextView: 1");
        if (isAdd){
            animationChangeTextView(locationList, changeList, true);
        } else {
            animationChangeTextView(locationList, changeList, false);
        }
        Log.i(TAG, "changeTextView: 2");
    }

    /**
     * 数字改变时的动画
     * @param locationList 改变的位置集合
     * @param changeList 改变的数字集合
     * @param isUp 是否是增加
     */
    private void animationChangeTextView(List<Integer> locationList, List<Integer> changeList, boolean isUp){
        final int trans;
        if (isUp){
            trans = TRANSLATION;
        } else {
            trans = -TRANSLATION;
        }
        for (int i = 0; i < locationList.size(); i++){
            final int loc = locationList.get(i);
            final int change = changeList.get(i);
            final TextView tv = mTextViews.get(loc);
            tv.setTranslationY(0);//0, 50, -50, 0  0, -50, 50, 0
            tv.setAlpha(1f);
            tv.animate()
                    .translationY(-trans)
                    .alpha(0f)
                    .setInterpolator(new AccelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            tv.setVisibility(INVISIBLE);
                            tv.setText("" + change);
                            tv.setTranslationY(trans);
                            tv.setAlpha(0f);
                            tv.setVisibility(VISIBLE);
                            tv.animate()
                                    .translationY(0)
                                    .alpha(1f)
                                    .setInterpolator(new DecelerateInterpolator())
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                        }
                                    });
                        }
                    });
        }
    }

    /**
     * 当小于原有数字长度时的操作
     */
    private void decreTextView() {
        mContentView.setTranslationY(0);
        mContentView.setAlpha(1f);
        mContentView.animate().alpha(0f).translationY(50).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setNumber(number);
                mContentView.removeView(mTextViews.get(0));
                mTextViews.remove(0);
                for (int i = 0; i < len; i++){
                    TextView textView = mTextViews.get(i);
                    int curValue = mNumbersList.get(i);
                    textView.setAlpha(0f);
                    textView.setTranslationY(-50f);
                    textView.setText("" + curValue);
                    textView.animate().translationY(0).alpha(1f);
                }

                mContentView.setTranslationY(0);
                mContentView.setAlpha(1f);
            }
        });
        Log.i(TAG, "decreTextView: ");
    }

    /**
     * 设定数字和长度
     * @param number
     */
    private void setNumber(int number){
        int num;
        this.number = number;
        if (mNumbersList != null){
            mNumbersList.clear();
        }
        if (number <= 0){
            mNumbersList.add(0);
            len = mNumbersList.size();
            return;
        }
        if (number > MAXVALUE){
            isOver = true;
            num = MAXVALUE;
        } else {
            num = number;
        }
        while (num != 0){
            mNumbersList.add(0, num % 10);
            num = (num - num%10) / 10;
        }
        len = mNumbersList.size();
    }

    /**
     * 图片切换时的动画
     * @param bitmap
     */
    private void animationBitmap(final Bitmap bitmap){
        animatorX = ObjectAnimator.ofFloat(mImageView, "scaleX", 1f, 0.8f);
        animatorY = ObjectAnimator.ofFloat(mImageView, "scaleY", 1f, 0.8f);
        animatorA = ObjectAnimator.ofFloat(mImageView, "alpha", 1f, 0f);
        animatorSet1.playTogether(animatorX, animatorY, animatorA);
        animatorSet1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mImageView.setImageBitmap(bitmap);
                animatorX = ObjectAnimator.ofFloat(mImageView, "scaleX", 0.8f, 1f, 1.2f, 1f);
                animatorY = ObjectAnimator.ofFloat(mImageView, "scaleY", 0.8f, 1f, 1.2f, 1f);
                animatorA = ObjectAnimator.ofFloat(mImageView, "alpha", 0.6f, 1f, 1f, 1f);
                animatorSet2.playTogether(animatorX, animatorY, animatorA);
                animatorSet2.start();
            }
        });
        animatorSet1.start();
    }

    /**
     * 公有的可调用的方法
     */

    /**
     * 初始化数字
     * @param num
     */
    public void initNumber(int num){
        setNumber(num);
        for (int i = 0; i < len; i++){
            mTextViews.add(createTextView());
        }
        for (int j = 0; j < len; j++){
            mTextViews.get(j).setText("" + mNumbersList.get(j));
        }
        for (int k = 0; k < len; k++){
            mContentView.addView(mTextViews.get(k), k);
        }
        if (isOver) {
            mOverTextView.setVisibility(VISIBLE);
        } else {
            mOverTextView.setVisibility(GONE);
        }
    }

    public void setIsCanceledBitmap(Bitmap isCanceledBitmap) {
        mIsCanceledBitmap = isCanceledBitmap;
    }

    public void setIsNotCanceledBitmap(Bitmap isNotCanceledBitmap) {
        mIsNotCanceledBitmap = isNotCanceledBitmap;
    }


    /**
     * 开始点赞的操作
     */
    public void start(){
        canceled = !canceled;
        if (canceled){
            decrement();
            animationBitmap(mIsCanceledBitmap);
        } else {
            increment();
            animationBitmap(mIsNotCanceledBitmap);
        }
        if (isOver) {
            mOverTextView.setVisibility(VISIBLE);
        } else {
            mOverTextView.setVisibility(GONE);
        }
        invalidate();
    }
}
