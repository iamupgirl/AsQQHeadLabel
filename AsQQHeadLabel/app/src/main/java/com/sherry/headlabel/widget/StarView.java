package com.sherry.headlabel.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.sherry.headlabel.adapter.StarViewAdapter;
import com.sherry.headlabel.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 自定义仿QQ头像标签动画
 *
 * Created by xueli on 2016/8/30.
 */
public class StarView extends ViewGroup implements View.OnClickListener {
    public static String STYLE_CENTER = "STYLE_CENTER";
    public static String STYLE_INSIDE = "STYLE_INSIDE";
    public static String STYLE_OUTSIDE = "STYLE_OUTSIDE";

    private int mCircleColor = Color.WHITE;
    private final int ITEM_ANIM_DELAYD = 120; // 每一个item动画延迟时间
    private final int TRANSLATE_ANIM_DURATION = 1500; // 每一个Item移动动画时间
    private final int ITEM_OFF_ANIM_DURATION = 500; // 每一个Item结束动画时间

    private int mInsideTrackRadius; // 内圆半径
    private int mOutsideTrackRadius; // 外圆半径
    private int mRoundTrackRadius; // 最内圆半径
    private boolean mStartAnim = true;
    private Integer mIsStartedAnim = 0;
    private Paint mPaint; // 画笔
    private View mCenterView; // 中间View
    private List<View> mInsideViewList = new ArrayList<View>(); // 内圆上的view集合
    private List<View> mOutSideViewList = new ArrayList<View>(); // 外圆上的view集合

    private StarViewAdapter mAdapter; // 适配器
    private onStarViewItemClickListener mListener; // 监听器

    public StarView(Context context) {
        super(context);
        /**
         * 在viewgroup初始化的时候，它调用了一个私有方法：initViewGroup，
         * 它里面会有一句setFlags（WILLL_NOT_DRAW,DRAW_MASK）;
         * 相当于调用了setWillNotDraw（true），所以说，对于ViewGroup，他就认为是透明的了，
         * 如果我们想要重写onDraw，就要调用setWillNotDraw（false）。
         */
        setWillNotDraw(false);
        init(null, 0);
    }

    public StarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        init(attrs, 0);
    }

    public StarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWillNotDraw(false);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.StarView, defStyle, 0);
        mCircleColor = a.getColor(
                R.styleable.StarView_circleColor,
                mCircleColor);
        a.recycle();

        // Set up a default TextPaint object
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE); // FILL：实心；STROKE：空心；FILL_AND_STROKE：同时实心和空心，该参数在某些场合会带来不可预期的显示效果
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG); // 消除锯齿
        mPaint.setTextAlign(Paint.Align.LEFT); // 文字对齐方式
        mPaint.setColor(mCircleColor);
        mPaint.setStrokeWidth(4); // 空心线宽

    }

    /**
     * 重写onMeasure方法
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * MeasureSpec:父布局传递给子布局的布局要求，每个MeasureSpec代表了一组宽度和高度的要求
         */
        int viewGroupWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int viewGroupHeight = MeasureSpec.getSize(heightMeasureSpec) - getPaddingBottom() - getPaddingTop();
        int viewCount = getChildCount();
        for (int i = 0; i < viewCount; i++) {
            View view = getChildAt(i);
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            view.measure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.AT_MOST));
        }
        setMeasuredDimension(viewGroupWidth, viewGroupHeight);
    }

    /**
     * 重写onLayout方法
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;
        int centerX = contentWidth / 2;
        int centerY = contentHeight / 2;

        int centerViewDiameter = (int) getResources().getDimension(R.dimen.height_80dp); // 中间圆的直径
        if (count > 0) {
            View view = getChildAt(0);
            int height = view.getMeasuredHeight();
            view.layout(centerX - centerViewDiameter / 2, centerY - height / 2,
                    centerX + centerViewDiameter / 2, centerY + view.getMeasuredHeight() - (height / 2));
            mRoundTrackRadius = (int) (centerViewDiameter * 0.9);
        }
        mInsideTrackRadius = (int) (centerViewDiameter * 1.5);
        mOutsideTrackRadius = (int) (centerViewDiameter * 2.5);
        if (count > 1) {
            layoutInsideRing(centerX, centerY);
        }
        if (count > 6) {
            layoutOutsideRing(contentWidth, contentHeight);
        }
    }

    /**
     * 内圆view布局分布
     *
     * @param centerX
     * @param centerY
     */
    private void layoutInsideRing(int centerX, int centerY) {
        if (mInsideViewList.size() <= 0) {
            return;
        }
        int spaceAngle = 360 / mInsideViewList.size();
        for (int i = 0; i < mInsideViewList.size(); i++) {
            View viewChild = mInsideViewList.get(i);
            int height = viewChild.getMeasuredHeight();
            int width = viewChild.getMeasuredWidth();
            double angle = (i * spaceAngle + 90) % 360 * Math.PI / 180;
            int x = (int) (centerX + mInsideTrackRadius * Math.cos(angle));
            int y = (int) (centerY - mInsideTrackRadius * Math.sin(angle));
            viewChild.layout(x - width / 2, y - height / 2, x + width / 2, y + height / 2);
        }
    }

    /**
     * 外圆view布局分布
     *
     * @param width
     * @param height
     */
    private void layoutOutsideRing(int width, int height) {
        if (mOutSideViewList.size() <= 0) {
            return;
        }
        int centerX = width / 2;
        int centerY = height / 2;
        int viewCount = mOutSideViewList.size();
        int itemNumTop = viewCount / 2 + viewCount % 2;
        int itemNumBottom = viewCount / 2;
        int x1 = width, x3 = centerX + mOutsideTrackRadius;
        int y1, y3 = centerY;
        y1 = centerY - (int) Math.sqrt(mOutsideTrackRadius * mOutsideTrackRadius - (x1 - centerX) * (x1 - centerX));
        double topMinAngle = getAngle(centerX, centerY, x1, y1, x3, y3);
        double topMaxAngle = 180 - topMinAngle;
        double bootomMinAngle = 360 - topMaxAngle;
        double bootomMaxAngle = 360 - topMinAngle;

//        double topSpaceAngle = (topMaxAngle - topMinAngle) * 1.0f / (itemNumTop + 1f);
//        Log.d("topMinAngle=",topMinAngle+"");
//        Log.d("topSpaceAngle=",topSpaceAngle+"");
//        layoutView(0, itemNumTop, topMinAngle, topSpaceAngle);
//        double bootomSpaceAngle = (bootomMaxAngle - bootomMinAngle) * 1.0f / (itemNumBottom + 1);
////        layoutView(itemNumTop, itemNumBottom, bootomMinAngle, bootomSpaceAngle);
//        layoutView(itemNumTop, itemNumBottom);

        int inAngle = 360 / mInsideViewList.size();
        int outAngle = (180 - inAngle) / 2;
        int indexAngle = inAngle / 2;
        int size = mOutSideViewList.size() / 2;
        //上
        layoutView(0, size, outAngle, indexAngle);
        outAngle = outAngle + 180;
        //下
        layoutView(size, mOutSideViewList.size(), outAngle, indexAngle);
    }

    private void layoutView(int initIndex, int endIndex, int startAngle, int indexAngle) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;
        int centerX = contentWidth / 2;
        int centerY = contentHeight / 2;

        int index = 0;
        for (int i = initIndex; i < endIndex; i++) {

            View view = mOutSideViewList.get(i);
            int height = (int) getResources().getDimension(R.dimen.height_60dp);
            int width = view.getMeasuredWidth();
            double angle = (startAngle + (indexAngle * index)) * Math.PI / 180;
            int x = (int) (centerX + mOutsideTrackRadius * Math.cos(angle));
            int y = (int) (centerY - mOutsideTrackRadius * Math.sin(angle));
            view.layout(x - width / 2, y - height / 2, x + width / 2, y + view.getMeasuredHeight() - (height / 2));
            index++;
        }
    }

    private void layoutView(int initIndex, int maxNum) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;
        int centerX = contentWidth / 2;
        int centerY = contentHeight / 2;
//        for (int i = initIndex; i - initIndex < maxNum && i < mOutSideViewList.size(); i++) {
//            int spaceAngle = 360 / mOutSideViewList.size();
//            int inAngle = 360/mInsideViewList.size();
//            int startAngle = inAngle * 2;
//            int endAngle = startAngle + inAngle;
//
//
//            View view = mOutSideViewList.get(i);
////            int height = view.getMeasuredHeight();
//            int height =  (int) getResources().getDimension(R.dimen.height_48dp);
//            int width = view.getMeasuredWidth();
////            double angle = ((i-1) * spaceAngle + 90) % 360 * Math.PI / 180;
//            double angle = startAngle + inAngle *
//            int x = (int) (centerX + mOutsideTrackRadius * Math.cos(angle));
//            int y = (int) (centerY - mOutsideTrackRadius * Math.sin(angle));
////            view.layout(x - width / 2, y - height / 2, x + width / 2, y + height / 2);
//            view.layout(x - width / 2, y - height / 2, x + width / 2,  y + view.getMeasuredHeight() -(height / 2));
//        }

        int index = 0;
        for (int i = initIndex; i < mOutSideViewList.size(); i++) {
            int inAngle = 360 / mInsideViewList.size();
            int startAngle = inAngle * 2;
            int endAngle = startAngle + inAngle;

            View view = mOutSideViewList.get(i);
//            int height = view.getMeasuredHeight();
            int height = (int) getResources().getDimension(R.dimen.height_48dp);
            int width = view.getMeasuredWidth();
//            double angle = ((i-1) * spaceAngle + 90) % 360 * Math.PI / 180;
//            double angle = startAngle + ((inAngle/2) * index);
            double angle = (0) * Math.PI / 180;
            int x = (int) (centerX + mOutsideTrackRadius * Math.cos(angle));
            int y = (int) (centerY - mOutsideTrackRadius * Math.sin(angle));
//            view.layout(x - width / 2, y - height / 2, x + width / 2, y + height / 2);
            //l,t,r,b
            view.layout(x - width / 2, y - height / 2, x + width / 2, y + view.getMeasuredHeight() - (height / 2));
            index++;
        }
    }

    private void layoutView(int initIndex, int maxNum, double initialValue, double spaceAngle) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;
        int centerX = contentWidth / 2;
        int centerY = contentHeight / 2;
        for (int i = initIndex; i - initIndex < maxNum && i < mOutSideViewList.size(); i++) {
            View view = mOutSideViewList.get(i);
            double angle = (initialValue + (i - initIndex + 1) * spaceAngle) * Math.PI / 180;
//            int height = view.getMeasuredHeight();
            int height = (int) getResources().getDimension(R.dimen.height_48dp);
            int width = view.getMeasuredWidth();
            int x = (int) (centerX + mOutsideTrackRadius * Math.cos(angle));
            int y = (int) (centerY - mOutsideTrackRadius * Math.sin(angle));
//            view.layout(x - width / 2, y - height / 2, x + width / 2, y + height / 2);
            view.layout(x - width / 2, y - height / 2, x + width / 2, y + view.getMeasuredHeight() - (height / 2));
        }
    }


    double pi180 = 180 / Math.PI;

    private double getAngle(int x1, int y1, int x2, int y2, int x3, int y3) {
        double _cos1 = getCos(x1, y1, x2, y2, x3, y3);//第一个点为顶点的角的角度的余弦值

        return Math.acos(_cos1) * pi180;
    }


    //获得三个点构成的三角形的 第一个点所在的角度的余弦值
    private double getCos(int x1, int y1, int x2, int y2, int x3, int y3) {
        double length1_2 = getLength(x1, y1, x2, y2);//获取第一个点与第2个点的距离
        double length1_3 = getLength(x1, y1, x3, y3);
        double length2_3 = getLength(x2, y2, x3, y3);

        double res = (Math.pow(length1_2, 2) + Math.pow(length1_3, 2) - Math.pow(length2_3, 2)) / (length1_2 * length1_3 * 2);//cosA=(pow(b,2)+pow(c,2)-pow(a,2))/2*b*c

        return res;
    }


    //获取坐标轴内两个点间的距离
    private double getLength(int x1, int y1, int x2, int y2) {
        double diff_x = Math.abs(x2 - x1);
        double diff_y = Math.abs(y2 - y1);
        double length_pow = Math.pow(diff_x, 2) + Math.pow(diff_y, 2);//两个点在 横纵坐标的差值与两点间的直线 构成直角三角形。length_pow等于该距离的平方
        return Math.sqrt(length_pow);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;
        mPaint.setAlpha(50);
        canvas.drawCircle(contentWidth / 2, contentHeight / 2, mRoundTrackRadius, mPaint);
        mPaint.setAlpha(40);
        canvas.drawCircle(contentWidth / 2, contentHeight / 2, mInsideTrackRadius, mPaint);
        mPaint.setAlpha(30);
        canvas.drawCircle(contentWidth / 2, contentHeight / 2, mOutsideTrackRadius, mPaint);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initAnim();
    }


    private synchronized void initAnim() {
        int viewCount = getChildCount();
        for (int i = 0; i < viewCount; i++) {
            View view = getChildAt(i);
            if (view != null && view.getVisibility() == View.VISIBLE) {
                view.setVisibility(View.INVISIBLE);
                view.setOnClickListener(this);
                Object tag = view.getTag();
                if (tag != null) {
                    if (tag.equals(STYLE_CENTER)) {
                        mCenterView = view;
                    } else if (tag.equals(STYLE_INSIDE)) {
                        mInsideViewList.add(view);
                    } else if (tag.equals(STYLE_OUTSIDE)) {
                        mOutSideViewList.add(view);
                    }
                }
            }
        }
        startAnim();
    }

    public void startAnim() {
        List<View> mViewList = new ArrayList<View>();
        if (mCenterView != null) {
            mViewList.add(mCenterView);
        }
        mViewList.addAll(mInsideViewList);
        mViewList.addAll(mOutSideViewList);
        for (int i = 0; i < mViewList.size(); i++) {
            final int index = i;
            final View view = mViewList.get(i);
            if (view == null) {
                continue;
            }
            synchronized (mIsStartedAnim) {
                mIsStartedAnim++;
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setDisPlayAnimation(index, view);
                }
            }, ITEM_ANIM_DELAYD * i);
        }
    }

    private void setDisPlayAnimation(final int delayed, final View view) {
        mStartAnim = true;
        ScaleAnimation animation = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setFillAfter(true);
        animation.setFillBefore(true);
        animation.setDuration(ITEM_OFF_ANIM_DURATION);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                view.setOnClickListener(StarView.this);
                if (delayed != 0) {
                    view.clearAnimation();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (mIsStartedAnim) {
                                mIsStartedAnim--;
                            }
                            startTranslateAnimation(view);
                        }
                    }, delayed);
                } else {
                    synchronized (mIsStartedAnim) {
                        mIsStartedAnim--;
                    }
                }
            }

            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }

    private void startTranslateAnimation(final View view) {
        final float toX, toY;
        Random random = new Random();
        int randomNum = Math.abs(random.nextInt()) % 8;
        switch (randomNum) {
            case 0:
                toX = 0f;
                toY = 0.1f;
                break;
            case 1:
                toX = 0;
                toY = -0.1f;
                break;
            case 2:
                toX = 0.1f;
                toY = 0;
                break;
            case 3:
                toX = -0.1f;
                toY = 0;
                break;
            case 4:
                toX = 0.1f;
                toY = 0.1f;
                break;
            case 5:
                toX = -0.1f;
                toY = 0.1f;
                break;
            case 6:
                toX = -0.1f;
                toY = -0.1f;
                break;
            case 7:
                toX = 0.1f;
                toY = 0.1f;
                break;
            default:
                toX = 0f;
                toY = 0f;
                break;
        }
        AnimationSet animation = new AnimationSet(true);
        TranslateAnimation animation1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, toX, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, toY);
        animation1.setDuration(TRANSLATE_ANIM_DURATION);
        animation1.setFillAfter(true);
        animation.addAnimation(animation1);
        TranslateAnimation animation2 = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -toX, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -toY);
        animation2.setDuration(TRANSLATE_ANIM_DURATION);
        animation2.setStartOffset(TRANSLATE_ANIM_DURATION);
        animation2.setFillAfter(true);
        animation.addAnimation(animation2);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                if (mStartAnim) {
                    startTranslateAnimation(view);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }


    private void setOffAnimation(final View view) {
        ScaleAnimation animation = new ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setFillAfter(true);
        animation.setFillBefore(true);
        animation.setDuration(ITEM_OFF_ANIM_DURATION);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                synchronized (mIsStartedAnim) {
                    mIsStartedAnim--;
                    if (mIsStartedAnim <= 0) {
                        if (mAdapter != null) {
                            mAdapter.reSetView();
                        }
                    }
                }
                view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }

    public void startChangeItemAnimOut() {
        mStartAnim = false;
        List<View> mViewList = new ArrayList<View>();
        if (mCenterView != null) {
            mViewList.add(mCenterView);
        }
        mViewList.addAll(mInsideViewList);
        mViewList.addAll(mOutSideViewList);
        for (int i = 0; i < mViewList.size(); i++) {
            View view = mViewList.get(i);
            if (view == null) {
                continue;
            }
            synchronized (mIsStartedAnim) {
                mIsStartedAnim++;
            }
            setOffAnimation(view);
        }
    }


    public void startChangeItemAnimIn() {
        List<View> mViewList = new ArrayList<View>();
        if (mCenterView != null) {
            mViewList.add(mCenterView);
        } else {
            mInsideViewList.clear();
            mOutSideViewList.clear();
            int viewCount = getChildCount();
            for (int i = 0; i < viewCount; i++) {
                View view = getChildAt(i);
                Object tag = view.getTag();
                if (tag != null) {
                    if (tag.equals(STYLE_CENTER)) {
                        mCenterView = view;
                    } else if (tag.equals(STYLE_INSIDE)) {
                        mInsideViewList.add(view);
                    } else if (tag.equals(STYLE_OUTSIDE)) {
                        mOutSideViewList.add(view);
                    }
                }
            }
            mViewList.add(mCenterView);
        }
        mViewList.addAll(mInsideViewList);
        mViewList.addAll(mOutSideViewList);
        for (int i = 0; i < mViewList.size(); i++) {
            View view = mViewList.get(i);
            view.setVisibility(View.INVISIBLE);
        }

        for (int i = 0; i < mViewList.size(); i++) {
            View view = mViewList.get(i);
            if (view == null) {
                continue;
            }
            synchronized (mIsStartedAnim) {
                mIsStartedAnim++;
            }
            view.clearAnimation();
            setDisPlayAnimation(i * ITEM_ANIM_DELAYD, view);
        }
    }

    public void stopAnimation() {
        mStartAnim = false;
    }


    @Override
    public void addView(View child) {
        Object tag = child.getTag();
        if (tag != null) {
            child.setVisibility(View.INVISIBLE);
            if (tag.equals(STYLE_CENTER)) {
                mCenterView = child;
            } else if (tag.equals(STYLE_INSIDE)) {
                mInsideViewList.add(child);
            } else if (tag.equals(STYLE_OUTSIDE)) {
                mOutSideViewList.add(child);
            }
        }
        super.addView(child);
    }

    @Override
    public void removeAllViews() {
        super.removeAllViews();
        mCenterView = null;
        mInsideViewList.clear();
        mOutSideViewList.clear();
    }

    @Override
    public void onClick(View v) {
        synchronized (mIsStartedAnim) {
            if (mIsStartedAnim > 0) {
                return;
            }
        }
        if (mListener != null) {
            Object tag = v.getTag();
            if (tag.equals(STYLE_CENTER)) {
                mListener.onItemClick(v, STYLE_CENTER, 0);
                return;
            } else if (tag.equals(STYLE_INSIDE)) {
                for (int i = 0; i < mInsideViewList.size(); i++) {
                    View viewT = mInsideViewList.get(i);
                    if (viewT == v) {
                        mListener.onItemClick(v, STYLE_INSIDE, i);
                        return;
                    }
                }
            } else if (tag.equals(STYLE_OUTSIDE)) {
                for (int i = 0; i < mOutSideViewList.size(); i++) {
                    View viewT = mOutSideViewList.get(i);
                    if (viewT == v) {
                        mListener.onItemClick(v, STYLE_OUTSIDE, i);
                        return;
                    }
                }
            }
        }
    }

    public void setAdapter(StarViewAdapter adapter) {
        this.mAdapter = adapter;
        if (mAdapter != null) {
            mAdapter.setStarView(this);
        }
    }

    public StarViewAdapter getAdapter() {
        return mAdapter;
    }


    public void setOnStarViewItemClickListener(onStarViewItemClickListener listener) {
        this.mListener = listener;
    }

    public interface onStarViewItemClickListener {

        void onItemClick(View v, String style, int index);

    }
}