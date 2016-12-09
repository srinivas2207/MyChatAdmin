package com.shree.mychatadmin.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by SrinivasDonapati on 10/27/2016.
 */

public class MyChatProgessDialog extends ProgressDialog {
    private Context context;

    public MyChatProgessDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    @Override
    public void onBackPressed() {
        ((Activity) context).onBackPressed();
    }
}
