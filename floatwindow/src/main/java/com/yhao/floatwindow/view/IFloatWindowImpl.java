package com.yhao.floatwindow.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;

import com.yhao.floatwindow.base.FloatLifecycle;
import com.yhao.floatwindow.constant.MoveType;
import com.yhao.floatwindow.constant.Screen;
import com.yhao.floatwindow.interfaces.FloatView;
import com.yhao.floatwindow.interfaces.IFloatWindow;
import com.yhao.floatwindow.interfaces.LifecycleListener;
import com.yhao.floatwindow.util.DensityUtil;

/**
 * @author yhao
 * @date 2017/12/22
 * https://github.com/yhaolpz
 */

public class IFloatWindowImpl implements IFloatWindow, LifecycleListener {

    private FloatWindow.BuildFloatWindow mBuildFloatWindow;
    private FloatView mFloatView;
    private ValueAnimator mAnimator;
    private TimeInterpolator mDecelerateInterpolator;
    private float downX, downY, upX, upY;
    private int mSlop;
    private int screenWidth;
    private int screenHeight;
    private boolean isShow;
    private boolean mClick;
    private boolean isHideByUser;
    private boolean isLandscape;

    IFloatWindowImpl(final FloatWindow.BuildFloatWindow buildFloatWindow, Boolean childViewTouchable) {
        mBuildFloatWindow = buildFloatWindow;

        int baseWidth = DensityUtil.getScreenWidth(mBuildFloatWindow.mApplicationContext);
        int baseHeight = DensityUtil.getScreenHeight(mBuildFloatWindow.mApplicationContext);
        screenWidth = baseWidth > baseHeight ? baseHeight : baseWidth;
        screenHeight = baseWidth > baseHeight ? baseWidth : baseHeight;
        isLandscape = baseWidth > baseHeight;

        mSlop = ViewConfiguration.get(mBuildFloatWindow.mApplicationContext).getScaledTouchSlop();
        if (mBuildFloatWindow.mMoveType == MoveType.fixed) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                mFloatView = new FloatPhone(buildFloatWindow.mApplicationContext, mBuildFloatWindow.mPermissionListener, childViewTouchable);
            } else {
                mFloatView = new FloatToast(buildFloatWindow.mApplicationContext, childViewTouchable);
            }
        } else {
            mFloatView = new FloatPhone(buildFloatWindow.mApplicationContext, mBuildFloatWindow.mPermissionListener, childViewTouchable);
            initTouchEvent();
        }
        mFloatView.setSize(mBuildFloatWindow.mWidth, mBuildFloatWindow.mHeight);
        mFloatView.setGravity(mBuildFloatWindow.gravity, mBuildFloatWindow.xOffset, mBuildFloatWindow.yOffset);
        mFloatView.setView(mBuildFloatWindow.mView);
        mFloatView.init();
        new FloatLifecycle(mBuildFloatWindow.mApplicationContext, mBuildFloatWindow.mShow, mBuildFloatWindow.mActivities, this);
    }

    @Override
    public void hideByUser() {
        if (!isShow) {
            return;
        }
        mBuildFloatWindow.mView.setVisibility(View.INVISIBLE);
        isShow = false;
        isHideByUser = true;
        if (mBuildFloatWindow.mViewStateListener != null) {
            mBuildFloatWindow.mViewStateListener.onHideByUser();
        }
    }

    @Override
    public void showByUser() {
        if (isShow) {
            return;
        }
        mBuildFloatWindow.mView.setVisibility(View.VISIBLE);
        isShow = true;
        isHideByUser = false;
        if (mBuildFloatWindow.mViewStateListener != null) {
            mBuildFloatWindow.mViewStateListener.onShowByUesr();
        }
    }

    @Override
    public boolean isShowing() {
        return isShow;
    }

    @Override
    public void dismiss() {
        mFloatView.dismiss();
        isShow = false;
        if (mBuildFloatWindow.mViewStateListener != null) {
            mBuildFloatWindow.mViewStateListener.onDismiss();
        }
    }

    @Override
    public void updateX(int x) {
        checkMoveType();
        mBuildFloatWindow.xOffset = x;
        mFloatView.updateX(x);
    }

    @Override
    public void updateY(int y) {
        checkMoveType();
        mBuildFloatWindow.yOffset = y;
        mFloatView.updateY(y);
    }

    @Override
    public void updateX(int screenType, float ratio) {
        checkMoveType();
        mBuildFloatWindow.xOffset = (int) ((screenType == Screen.width ? screenWidth : screenHeight) * ratio);
        mFloatView.updateX(mBuildFloatWindow.xOffset);
    }

    @Override
    public void updateY(int screenType, float ratio) {
        checkMoveType();
        mBuildFloatWindow.yOffset = (int) ((screenType == Screen.width ? screenWidth : screenHeight) * ratio);
        mFloatView.updateY(mBuildFloatWindow.yOffset);
    }

    @Override
    public int getX() {
        return mFloatView.getX();
    }

    @Override
    public int getY() {
        return mFloatView.getY();
    }

    @Override
    public void onShow() {
        if (isShow || isHideByUser) {
            return;
        }
        mBuildFloatWindow.mView.setVisibility(View.VISIBLE);
        isShow = true;
        if (mBuildFloatWindow.mViewStateListener != null) {
            mBuildFloatWindow.mViewStateListener.onShow();
        }
    }

    @Override
    public void onHide() {
        if (!isShow || isHideByUser) {
            return;
        }
        mBuildFloatWindow.mView.setVisibility(View.INVISIBLE);
        isShow = false;
        if (mBuildFloatWindow.mViewStateListener != null) {
            mBuildFloatWindow.mViewStateListener.onHide();
        }
    }

    @Override
    public void onBackToDesktop() {
        if (!mBuildFloatWindow.mDesktopShow) {
            onHide();
        }
        if (mBuildFloatWindow.mViewStateListener != null) {
            mBuildFloatWindow.mViewStateListener.onBackToDesktop();
        }
    }

    @Override
    public void onPortrait() {
        double ratio = (double) getY() / screenWidth;
        int x = getX() + mBuildFloatWindow.getWidth() / 2 < screenHeight / 2 ? + FloatWindow.mSlideLeftMargin : screenWidth + FloatWindow.mSlideRightMargin;
        mFloatView.updateXY(x, (int) (ratio * screenHeight));
        isLandscape = false;
    }

    @Override
    public void onLandscape() {
        double ratio = (double) getY() / screenHeight;
        int x = getX() + mBuildFloatWindow.getWidth() / 2 < screenWidth / 2 ? + FloatWindow.mSlideLeftMargin : screenHeight + FloatWindow.mSlideRightMargin;
        mFloatView.updateXY(x, (int) (ratio * screenWidth));
        isLandscape = true;
    }

    private void checkMoveType() {
        if (mBuildFloatWindow.mMoveType == MoveType.fixed) {
            throw new IllegalArgumentException("FloatWindow of this tag is not allowed to move!");
        }
    }

    private void initTouchEvent() {
        switch (mBuildFloatWindow.mMoveType) {
            case MoveType.inactive:
                break;
            default:
                mBuildFloatWindow.mView.setOnTouchListener(new View.OnTouchListener() {
                    float lastX, lastY, changeX, changeY;
                    int newX, newY;

                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                downX = event.getRawX();
                                downY = event.getRawY();
                                lastX = event.getRawX();
                                lastY = event.getRawY();
                                cancelAnimator();
                                break;
                            case MotionEvent.ACTION_MOVE:
                                changeX = event.getRawX() - lastX;
                                changeY = event.getRawY() - lastY;
                                newX = (int) (getX() + changeX);
                                newY = (int) (getY() + changeY);
                                mFloatView.updateXY(newX, newY);
                                if (mBuildFloatWindow.mViewStateListener != null) {
                                    mBuildFloatWindow.mViewStateListener.onPositionUpdate(newX, newY);
                                }
                                lastX = event.getRawX();
                                lastY = event.getRawY();
                                break;
                            case MotionEvent.ACTION_UP:
                                upX = event.getRawX();
                                upY = event.getRawY();
                                mClick = (Math.abs(upX - downX) > mSlop) || (Math.abs(upY - downY) > mSlop);
                                onActionUp();
                                return mClick;
                            default:
                                break;
                        }
                        return false;
                    }
                });
        }
    }

    private void onActionUp() {
        switch (mBuildFloatWindow.mMoveType) {
            case MoveType.slide:
                int[] a = new int[2];
                mBuildFloatWindow.mView.getLocationOnScreen(a);
                int startX = a[0];
                int endX;
                if (isLandscape) {
                    endX = a[0] + mBuildFloatWindow.getWidth() / 2 < screenHeight / 2 ? FloatWindow.mSlideLeftMargin : screenHeight + FloatWindow.mSlideRightMargin;
                } else {
                    endX = a[0] + mBuildFloatWindow.getWidth() / 2 < screenWidth / 2 ? FloatWindow.mSlideLeftMargin : screenWidth + FloatWindow.mSlideRightMargin;
                }
                if (startX == endX) {
                    return;
                }
                mAnimator = ObjectAnimator.ofInt(startX, endX);
                mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int x = (int) animation.getAnimatedValue();
                        mFloatView.updateX(x);
                        if (mBuildFloatWindow.mViewStateListener != null) {
                            mBuildFloatWindow.mViewStateListener.onPositionUpdate(x, (int) upY);
                        }
                    }
                });
                startAnimator();
                break;
            case MoveType.back:
                PropertyValuesHolder pvhX = PropertyValuesHolder.ofInt("x", getX(), mBuildFloatWindow.xOffset);
                PropertyValuesHolder pvhY = PropertyValuesHolder.ofInt("y", getY(), mBuildFloatWindow.yOffset);
                mAnimator = ObjectAnimator.ofPropertyValuesHolder(pvhX, pvhY);
                mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int x = (int) animation.getAnimatedValue("x");
                        int y = (int) animation.getAnimatedValue("y");
                        mFloatView.updateXY(x, y);
                        if (mBuildFloatWindow.mViewStateListener != null) {
                            mBuildFloatWindow.mViewStateListener.onPositionUpdate(x, y);
                        }
                    }
                });
                startAnimator();
                break;
            default:
                break;
        }
    }

    private void startAnimator() {
        if (mBuildFloatWindow.mInterpolator == null) {
            mBuildFloatWindow.mInterpolator = mDecelerateInterpolator == null ? mDecelerateInterpolator = new DecelerateInterpolator() : mDecelerateInterpolator;
        }
        mAnimator.setInterpolator(mBuildFloatWindow.mInterpolator);
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimator.removeAllUpdateListeners();
                mAnimator.removeAllListeners();
                mAnimator = null;
                if (mBuildFloatWindow.mViewStateListener != null) {
                    mBuildFloatWindow.mViewStateListener.onMoveAnimEnd();
                }
            }
        });
        mAnimator.setDuration(mBuildFloatWindow.mDuration).start();
        if (mBuildFloatWindow.mViewStateListener != null) {
            mBuildFloatWindow.mViewStateListener.onMoveAnimStart();
        }
    }

    private void cancelAnimator() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
    }
}
