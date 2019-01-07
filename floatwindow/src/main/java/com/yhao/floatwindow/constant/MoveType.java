package com.yhao.floatwindow.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by yhao on 2017/12/22.
 * https://github.com/yhaolpz
 */

public class MoveType {
    public static final int fixed = 0;
    public static final int inactive = 1;
    public static final int active = 2;
    public static final int slide = 3;
    public static final int back = 4;

    @IntDef({fixed, inactive, active, slide, back})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MOVE_TYPE {
    }
}
