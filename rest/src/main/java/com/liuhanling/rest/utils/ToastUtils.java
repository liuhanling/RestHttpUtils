package com.liuhanling.rest.utils;

import android.annotation.SuppressLint;
import android.widget.Toast;

import com.liuhanling.rest.RestHttpUtils;

public class ToastUtils {

    private static Toast mToast;

    @SuppressLint("ShowToast")
    public static void showToast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(RestHttpUtils.getContext(), msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }
}
