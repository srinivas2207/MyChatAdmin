package com.shree.mychatadmin.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.shree.mychatadmin.R;
import com.shree.mychatadmin.adapters.ViewPagerAdapter;
import com.shree.mychatadmin.fragments.UserListFragment;
import com.shree.mychatadmin.fragments.UserTypeFragment;
import com.shree.mychatadmin.network.NetworkOperations;
import com.shree.mychatadmin.network.RestApiCallBack;
import com.shree.mychatadmin.util.ApplicationConstants;

public class HomeActivity extends ParentActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private FloatingActionButton addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        addBtn = (FloatingActionButton) findViewById(R.id.addBtn);
        addBtn.setOnClickListener(addBtnClickListener);

        showHomePage();

        NetworkOperations.getInstance().loadUserTypeData();
    }


    public void showHomePage() {
        toolbar.setTitle("MyChat Admin");

        tabLayout.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        UserListFragment userFragment = new UserListFragment();
        userFragment.setFragmentType(ApplicationConstants.NEW_USER);
        viewPagerAdapter.addFragment(userFragment, "NEW");

        userFragment = new UserListFragment();
        userFragment.setFragmentType(ApplicationConstants.ACTIVE_USER);
        viewPagerAdapter.addFragment(userFragment, "ACTIVE");

        userFragment = new UserListFragment();
        userFragment.setFragmentType(ApplicationConstants.BLOCKED_USER);
        viewPagerAdapter.addFragment(userFragment, "BLOCKED");

        userFragment = new UserListFragment();
        userFragment.setFragmentType(ApplicationConstants.REQUEST_USER);
        viewPagerAdapter.addFragment(userFragment, "REQUESTS");

        viewPagerAdapter.addFragment(new UserTypeFragment(), "UserType");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void notifyActivity(int notificationType) {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onRestApiCallBack(String url, boolean isSuccess, int responseType) {
        if (viewPager!=null && viewPagerAdapter!=null) {
            int currentPos = viewPager.getCurrentItem();
            ((RestApiCallBack) viewPagerAdapter.getItem(currentPos)).onRestApiCallBack(url, isSuccess, responseType);
        }
    }

    View.OnClickListener addBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (viewPager!=null && viewPagerAdapter!=null) {
                int currentPos = viewPager.getCurrentItem();
                Fragment fragment =  viewPagerAdapter.getItem(currentPos);
                if (fragment instanceof UserTypeFragment) {
                    showAddUserTypeDialog();
                } else {
                    Intent intent = new Intent(HomeActivity.this, EditUserActivity.class);
                    startActivity(intent);
                }
            }
        }
    };

    public void showAddUserTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.alert_input_layout, null);
        final EditText input = (EditText) mView.findViewById(R.id.userType);

        builder.setView(mView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               String userType = input.getText().toString();
                if (TextUtils.isEmpty(userType)) {

                } else {
                    NetworkOperations.getInstance().addUserType(userType);
                    showProgress("Adding ...");
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
