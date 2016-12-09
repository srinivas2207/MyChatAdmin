package com.shree.mychatadmin.util;

import android.text.TextUtils;

public class UserDetails {
    private String emaialId;
    private String phoneNumber;
    private String userName;
    private int userStatus;
    private String userType;

    public String getEmaialId() {
        return emaialId;
    }

    public void setEmaialId(String emaialId) {
        this.emaialId = emaialId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

}
