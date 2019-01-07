package com.example.yhao.floatwindow;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.yhao.fixedfloatwindow.R;
import com.yhao.floatwindow.view.FloatWindow;
import com.yhao.floatwindow.constant.MoveType;
import com.yhao.floatwindow.interfaces.PermissionListener;
import com.yhao.floatwindow.constant.Screen;
import com.yhao.floatwindow.interfaces.ViewStateListener;

public class A_Activity extends AppCompatActivity {
    private static final String TAG = "Jie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);
        setTitle("A");

        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageResource(R.drawable.icon);

        ImageView imageView1 = new ImageView(getApplicationContext());
        imageView1.setBackgroundColor(Color.BLACK);

        FloatWindow
                .with(getApplicationContext())
                .setView(imageView)
                //设置悬浮控件宽高
                .setWidth(60)
                .setHeight(60)
                .setX(Screen.width, 0.9f)
                .setY(Screen.height, 0.3f)
                .setMoveType(MoveType.slide)
                .setMoveStyle(500, new BounceInterpolator())
                .setFilter(true, A_Activity.class, C_Activity.class)
                .setViewStateListener(mViewStateListener)
                .setPermissionListener(mPermissionListener)
                .setDesktopShow(true)
                .build();

        EditText editText = new EditText(getApplicationContext());
        editText.setBackgroundColor(Color.BLACK);

        FloatWindow
                .with(getApplicationContext())
                .setView(editText)
                //设置悬浮控件宽高
                .setWidth(60)
                .setHeight(60)
                .setX(Screen.width, 0.8f)
                .setY(Screen.height, 0.3f)
                .setMoveType(MoveType.slide)
                .setMoveStyle(500, new BounceInterpolator())
                .setFilter(true, A_Activity.class, C_Activity.class)
                .setViewStateListener(mViewStateListener)
                .setPermissionListener(mPermissionListener)
                .setDesktopShow(true)
//                .setChildViewTouchable(true)
                .setTag("1")
                .build();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "imageViewOnClick");
            }
        });
    }

    public void change(View view) {
        FloatWindow.get().hideByUser();
    }

    public void change1(View view) {
        FloatWindow.get().showByUser();
    }

    public void change2(View view) {
        startActivity(new Intent(A_Activity.this,B_Activity.class));
    }

    public PermissionListener mPermissionListener = new PermissionListener() {
        @Override
        public void onSuccess() {
            Log.d(TAG, "onSuccess");
        }

        @Override
        public void onFail() {
            Log.d(TAG, "onFail");
        }
    };

    public ViewStateListener mViewStateListener = new ViewStateListener() {
        @Override
        public void onPositionUpdate(int x, int y) {
            //Log.d(TAG, "onPositionUpdate: x=" + x + " y=" + y);
        }

        @Override
        public void onShow() {
            Log.d(TAG, "onShow");
        }

        @Override
        public void onHide() {
            Log.d(TAG, "onHide");
        }

        @Override
        public void onShowByUesr() {
            Log.d(TAG, "onShowByUesr");
        }

        @Override
        public void onHideByUser() {
            Log.d(TAG, "onHideByUser");
        }

        @Override
        public void onDismiss() {
            Log.d(TAG, "onDismiss");
        }

        @Override
        public void onMoveAnimStart() {
            Log.d(TAG, "onMoveAnimStart");
        }

        @Override
        public void onMoveAnimEnd() {
            Log.d(TAG, "onMoveAnimEnd");
        }

        @Override
        public void onBackToDesktop() {
            Log.d(TAG, "onBackToDesktop");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FloatWindow.destroy();
        FloatWindow.destroy("1");
    }
}
