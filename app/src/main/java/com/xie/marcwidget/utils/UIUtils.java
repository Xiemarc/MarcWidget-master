package com.xie.marcwidget.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 描述：
 * 作者：Marc on 2016/11/11 16:09
 * 邮箱：aliali_ha@yeah.net
 */
public class UIUtils {

    public static Toast mToast;

    /**
     * 解决toast排队现象
     *
     * @param context
     * @param msg
     */
    public static void showToast(Context context, String msg) {
        if (null == mToast) {
            mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        //必须先设置text再设置显示位置、位置颠倒会出大错
        mToast.setText(msg);
        mToast.show();
    }
}
