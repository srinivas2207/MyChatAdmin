package com.shree.mychatadmin.services;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.shree.mychatadmin.util.DatabaseUtil;

/**
 * Created by SrinivasDonapati on 10/19/2016.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService{
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String gcmDeviceToken = FirebaseInstanceId.getInstance().getToken();
        System.out.println("GCM ID =============> " +gcmDeviceToken);
        DatabaseUtil.getInstance().storeGcmToken(gcmDeviceToken);
    }
}
