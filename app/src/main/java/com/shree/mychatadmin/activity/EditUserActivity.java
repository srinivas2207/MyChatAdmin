package com.shree.mychatadmin.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.shree.mychatadmin.R;
import com.shree.mychatadmin.network.NetworkOperations;
import com.shree.mychatadmin.util.ApplicationConstants;
import com.shree.mychatadmin.util.DatabaseUtil;
import com.shree.mychatadmin.util.DbUtilConstants;
import com.shree.mychatadmin.util.UserDetails;
import com.shree.mychatadmin.util.UserType;

import java.util.List;

public class EditUserActivity extends ParentActivity {
    private EditText fullNameET;
    private EditText phoneNumberET;
    private EditText emailET;
    private EditText userTypeET;
    private Button primaryBtn;
    private Button secondaryBtn;

    private int userType = 0;
    private String phoneNumber = null;
    private UserDetails userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        getSupportActionBar().setTitle("User Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent()!=null) {
            phoneNumber = getIntent().getStringExtra(DbUtilConstants.PHONE_NUMBER);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialize();
        loadData();
    }

    public void initialize() {
        fullNameET = (EditText) findViewById(R.id.fullName);
        phoneNumberET = (EditText) findViewById(R.id.phoneNumber);
        emailET = (EditText) findViewById(R.id.email);
        userTypeET = (EditText) findViewById(R.id.userType);

        primaryBtn = (Button) findViewById(R.id.primaryBtn);
        secondaryBtn = (Button) findViewById(R.id.secondaryBtn);

        primaryBtn.setOnClickListener(onClickListener);
        secondaryBtn.setOnClickListener(onClickListener);

        userTypeET.setFocusable(false);
        userTypeET.setClickable(true);
        userTypeET.setOnClickListener(onClickListener);
    }

    private void loadData() {
        if (phoneNumber != null ) {
            userDetails = DatabaseUtil.getInstance().getUserDetails(phoneNumber);
        }

        userTypeET.setTag("");

        if (userDetails != null) {
            userType = userDetails.getUserStatus();
            fullNameET.setText(userDetails.getUserName());
            emailET.setText(userDetails.getEmaialId());
            phoneNumberET.setText(userDetails.getPhoneNumber());
            userTypeET.setText(getUserTypeText(userDetails.getUserType()));
            userTypeET.setTag(userDetails.getUserType());
        } else {
            userType =  ApplicationConstants.NEW_USER;
        }

        if(userType == ApplicationConstants.BLOCKED_USER_2) {
            userType = ApplicationConstants.BLOCKED_USER;
        }

        if (userType != ApplicationConstants.NEW_USER ) {
            fullNameET.setEnabled(false);
            fullNameET.setFocusable(false);

            phoneNumberET.setEnabled(false);
            phoneNumberET.setFocusable(false);

            emailET.setEnabled(false);
            emailET.setFocusable(false);
        }

        primaryBtn.setText("save");
        if (userType == ApplicationConstants.NEW_USER) {
            if(userDetails == null) {
                secondaryBtn.setVisibility(View.GONE);
            } else {
                secondaryBtn.setText("delete");
            }
        }

        if (userType == ApplicationConstants.ACTIVE_USER) {
            secondaryBtn.setVisibility(View.VISIBLE);
            secondaryBtn.setText("block");
        }

        if (userType == ApplicationConstants.BLOCKED_USER) {
            secondaryBtn.setVisibility(View.VISIBLE);
            secondaryBtn.setText("unblock");
        }

        if (userType == ApplicationConstants.REQUEST_USER) {
            primaryBtn.setText("accept");
            secondaryBtn.setVisibility(View.VISIBLE);
            secondaryBtn.setText("reject");
        }
    }

    private String getUserTypeText(String txt) {
        String uTypeStr = "";
        if (!TextUtils.isEmpty(txt)) {
            String[] userTypeIds = txt.split(",");
            List<UserType> userTypeList = DatabaseUtil.getInstance().getAvailableUserTypes();
            for (String userTypeVal : userTypeIds) {
                for (UserType userTypeObj : userTypeList) {
                    int uId = Integer.parseInt(userTypeVal.trim());
                    if (userTypeObj.getId() == uId) {
                        uTypeStr += ( "," + userTypeObj.getType());
                    }
                }
            }
        }
        if (uTypeStr.length() > 0) {
            uTypeStr = uTypeStr.substring(1);
            uTypeStr = uTypeStr.trim();
        }
        return  uTypeStr;
    }
    @Override
    public void notifyActivity(int notificationType) {

    }

    @Override
    public void onRestApiCallBack(String url, boolean isSucess, int responseType) {

        if (url.equalsIgnoreCase(ApplicationConstants.ADD_USER_URL) ||
                url.equalsIgnoreCase(ApplicationConstants.DELETE_USER_URL) ||
                url.equalsIgnoreCase(ApplicationConstants.EDIT_USER_URL) ||
                url.equalsIgnoreCase(ApplicationConstants.BLOCK_USER_URL) ||
                url.equalsIgnoreCase(ApplicationConstants.UNBLOCK_USER_URL) ||
                url.equalsIgnoreCase(ApplicationConstants.ACCEPT_REQUEST) ||
                url.equalsIgnoreCase(ApplicationConstants.REJECT_REQUEST)) {

            cancleProgress();

            if (!isSucess) {
                //Toast
                loadData();
                return;
            } else {
                finish();
            }
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.userType:
                    showUserTypeSpinner((String)userTypeET.getTag());
                    break;

                case R.id.primaryBtn:
                    String uType = (String) userTypeET.getTag();
                    if (userType == ApplicationConstants.REQUEST_USER) {
                        NetworkOperations.getInstance().acceptRequest(phoneNumberET.getText().toString()
                        , uType);
                    } else {
                        String phNum = phoneNumberET.getText().toString();
                        String email = emailET.getText().toString();
                        String fName= fullNameET.getText().toString();
                        phoneNumber = phNum;

                        if (TextUtils.isEmpty(fName)) {
                            fullNameET.setFocusable(true);
                            fullNameET.setError("Provide Full Name !");
                            fullNameET.requestFocus();
                            return;
                        }

                        if (TextUtils.isEmpty(phNum)) {
                            phoneNumberET.setFocusable(true);
                            phoneNumberET.setError("Provide Phone Number !");
                            phoneNumberET.requestFocus();
                            return;
                        }

                        if (TextUtils.isEmpty(email)) {
                            emailET.setFocusable(true);
                            emailET.setError("Provide Email Id !");
                            emailET.requestFocus();
                            return;
                        }

                        if (TextUtils.isEmpty(uType)) {
                            userTypeET.setError("Select User Type !");
                            return;
                        }

                        if (userDetails == null) {
                            if (DatabaseUtil.getInstance().getUserDetails(phNum) != null) {
                                phoneNumberET.setText("");
                                phoneNumberET.setError("User already registered !");
                                return;
                            }
                            NetworkOperations.getInstance().addUser(fName,phNum,email,uType);
                        } else {
                            NetworkOperations.getInstance().editUser(fName,phNum,email,uType);
                        }
                    }
                    showProgress("Saving..");
                    break;

                case R.id.secondaryBtn:
                    phoneNumber = phoneNumberET.getText().toString();
                    showProgress("Saving..");
                    if (userType == ApplicationConstants.REQUEST_USER) {
                        NetworkOperations.getInstance().rejectRequest(phoneNumber);
                    } else if (userType == ApplicationConstants.NEW_USER) {
                        NetworkOperations.getInstance().deleteUser(phoneNumber);
                    } else if (userType == ApplicationConstants.ACTIVE_USER) {
                        NetworkOperations.getInstance().blockUser(phoneNumber);
                    }else if (userType == ApplicationConstants.BLOCKED_USER) {
                        NetworkOperations.getInstance().unBlockUser(phoneNumber);
                    }
                    break;
            }
        }
    };

    private void showUserTypeSpinner(final String selectedUserType) {
        final List<UserType> userTypeList = DatabaseUtil.getInstance().getAvailableUserTypes();
        if (userTypeList == null || userTypeList.size() == 0) {
            //toast
            return;
        }

        final CharSequence[] items = new CharSequence[userTypeList.size()];
        final boolean[] checkedItems = new boolean[userTypeList.size()];

        String[] selectedUserTypeArr = null;
        if (!TextUtils.isEmpty(selectedUserType) ) {
            selectedUserTypeArr = selectedUserType.split(",");
        }
        for (int i=0; i<userTypeList.size(); i++) {
            UserType userType = userTypeList.get(i);
            items[i] = userType.getType();

            if (selectedUserTypeArr != null) {
                for(String selUserType : selectedUserTypeArr) {
                    if (userType.getId() == Integer.parseInt(selUserType.trim())) {
                        checkedItems[i] = true;
                    }
                }
            }
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Select User Type")
                .setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        checkedItems[indexSelected] = isChecked;
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String selUserType = "";
                        for (int i=0; i<checkedItems.length;i++) {
                            if(checkedItems[i]) {
                                selUserType += ("," + userTypeList.get(i).getId());
                            }
                        }
                        if (selUserType.length() > 0) {
                            selUserType = selUserType.substring(1);
                            selUserType = selUserType.trim();
                        }

                        if(selUserType.length() == 0) {
                            Toast.makeText(EditUserActivity.this, "User type need to be selected !" ,Toast.LENGTH_SHORT).show();
                        } else {
                            if(selUserType.contains("1")) {
                                selUserType = "1";
                            }
                            userTypeET.setTag(selUserType);
                            userTypeET.setText(getUserTypeText(selUserType));
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).create();
        dialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}


