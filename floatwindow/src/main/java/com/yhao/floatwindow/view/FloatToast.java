package com.yhao.floatwindow.view;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.yhao.floatwindow.interfaces.FloatView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 自定义 toast 方式，无需申请权限
 * 当前版本暂时用 TYPE_TOAST 代替，后续版本可能会再融入此方式
 */

class FloatToast implements FloatView {


    private Toast toast;

    private Object mTN;
    private Method show;
    private Method hide;

    private int mWidth;
    private int mHeight;
    Boolean mTouchable;


    FloatToast(Context applicationContext,Boolean childViewTouchable) {
        toast = new Toast(applicationContext);
        mTouchable = childViewTouchable;
    }


    @Override
    public void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    @Override
    public void setView(View view) {
        toast.setView(view);
        initTN(mTouchable);
    }

    @Override
    public void setGravity(int gravity, int xOffset, int yOffset) {
        toast.setGravity(gravity, xOffset, yOffset);
    }

    @Override
    public void init() {
        try {
            show.invoke(mTN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        try {
            hide.invoke(mTN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateXY(int x, int y) {

    }

    @Override
    public void updateX(int x) {

    }

    @Override
    public void updateY(int y) {

    }

    @Override
    public int getX() {
        return 0;
    }

    @Override
    public int getY() {
        return 0;
    }


    private void initTN(Boolean childViewTouchable) {
        try {
            Field tnField = toast.getClass().getDeclaredField("mTN");
            tnField.setAccessible(true);
            mTN = tnField.get(toast);
            show = mTN.getClass().getMethod("show");
            hide = mTN.getClass().getMethod("hide");
            Field tnParamsField = mTN.getClass().getDeclaredField("mParams");
            tnParamsField.setAccessible(true);
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) tnParamsField.get(mTN);
            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            if (!childViewTouchable) {
                params.flags += WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            }
            params.width = mWidth;
            params.height = mHeight;
            params.windowAnimations = 0;
            Field tnNextViewField = mTN.getClass().getDeclaredField("mNextView");
            tnNextViewField.setAccessible(true);
            tnNextViewField.set(mTN, toast.getView());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
