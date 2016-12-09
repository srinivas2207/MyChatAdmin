package com.shree.mychatadmin.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.shree.mychatadmin.R;
import com.shree.mychatadmin.activity.EditUserActivity;
import com.shree.mychatadmin.adapters.RecyclerTouchListener;
import com.shree.mychatadmin.adapters.UserListAdapter;
import com.shree.mychatadmin.network.NetworkOperations;
import com.shree.mychatadmin.network.RestApiCallBack;
import com.shree.mychatadmin.util.ApplicationConstants;
import com.shree.mychatadmin.util.DatabaseUtil;
import com.shree.mychatadmin.util.DbUtilConstants;
import com.shree.mychatadmin.util.UserDetails;

import java.util.ArrayList;
import java.util.List;


public class UserListFragment extends Fragment implements RestApiCallBack {


    private boolean isUserSelected = false;
    private UserDetails selectedUser = null;

    private SwipeRefreshLayout swipeRefreshLayout;
    private View content_layout;
    private TextView message;
    private RecyclerView recyclerView;
    private UserListAdapter userListAdapter;
    private List<UserDetails> userList;
    private int fragmentType = 1;

    public void setFragmentType(int fragmentType) {
        this.fragmentType = fragmentType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        content_layout = (View) inflater.inflate(
                R.layout.recycle_list, container,false);
        initializeViews();
        return content_layout;
    }


    private void initializeViews() {
        swipeRefreshLayout = (SwipeRefreshLayout)content_layout.findViewById(R.id.swipeRefreshLayout);
        message = (TextView)content_layout.findViewById(R.id.message);
        recyclerView = (RecyclerView) content_layout.findViewById(R.id.recycler_view);
        recyclerTouchListener.setRecyclerView(recyclerView);
        recyclerView.addOnItemTouchListener(recyclerTouchListener);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                NetworkOperations.getInstance().getUsers(fragmentType);
            }
        });
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        loadDataFromDb();
        NetworkOperations.getInstance().getUsers(fragmentType);
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        userListAdapter = null;
    }


    private void loadDataFromDb() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                userList = DatabaseUtil.getInstance().getUsers(fragmentType);
                return null;
            }

            protected void onPostExecute(Boolean result) {
                updateUi();
            }

        }.execute(null, null, null);
    }

    public void updateUi() {
        if (userList == null) {
            userList = new ArrayList<UserDetails>();
        }

        recyclerView.setVisibility(View.VISIBLE);
        message.setVisibility(View.GONE);

        if (userListAdapter == null) {
            userListAdapter = new UserListAdapter(userList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(userListAdapter);
        } else {
            userListAdapter.updateList(userList);
        }

        if (userList.size() == 0) {
            message.setVisibility(View.VISIBLE);
            message.setText("No users !");
        }
    }

    @Override
    public void onRestApiCallBack(String url, boolean isSucess, int responseType) {
        if( (fragmentType == ApplicationConstants.NEW_USER &&
                url.equalsIgnoreCase(ApplicationConstants.GET_USERS_URL + ApplicationConstants.NEW_USER) ) ||
                (fragmentType == ApplicationConstants.ACTIVE_USER &&
                        url.equalsIgnoreCase(ApplicationConstants.GET_USERS_URL + ApplicationConstants.ACTIVE_USER) ) ||
                (fragmentType == ApplicationConstants.BLOCKED_USER &&
                        url.equalsIgnoreCase(ApplicationConstants.GET_USERS_URL + ApplicationConstants.BLOCKED_USER) ) ||
                (fragmentType == ApplicationConstants.REQUEST_USER &&
                        url.equalsIgnoreCase(ApplicationConstants.GET_USERS_URL + ApplicationConstants.REQUEST_USER) )
                ){

            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }

            loadDataFromDb();
        }
    }

    RecyclerTouchListener recyclerTouchListener =new RecyclerTouchListener(getContext(),  new RecyclerTouchListener.ClickListener() {
        @Override
        public void onClick(View view, int position) {
            if (userList == null || position >= userList.size()) {
                return;
            }
            Intent intent = new Intent(getActivity(), EditUserActivity.class);
            intent.putExtra(DbUtilConstants.PHONE_NUMBER, userList.get(position).getPhoneNumber());
            startActivity(intent);
        }

        @Override
        public void onLongClick(View view, int position) {
            Toast.makeText(getActivity(),"Long press selection ", Toast.LENGTH_SHORT).show();
        }
    });

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            loadDataFromDb();
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}