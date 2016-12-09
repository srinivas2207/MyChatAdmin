package com.shree.mychatadmin.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shree.mychatadmin.R;
import com.shree.mychatadmin.activity.ParentActivity;
import com.shree.mychatadmin.network.NetworkOperations;
import com.shree.mychatadmin.network.RestApiCallBack;
import com.shree.mychatadmin.util.ApplicationConstants;
import com.shree.mychatadmin.util.DatabaseUtil;
import com.shree.mychatadmin.util.UserType;

import java.util.List;


public class UserTypeFragment extends Fragment implements RestApiCallBack {

    private View content_layout;
    private LinearLayout userTypeContainer;
    private List<UserType> userTypeList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        content_layout = (View) inflater.inflate(
                R.layout.usertype_fragment, container,false);
        initializeViews();
        return content_layout;
    }


    private void initializeViews() {
        userTypeContainer = (LinearLayout) content_layout.findViewById(R.id.userTypeContainer);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        loadDataFromDb();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }


    private void loadDataFromDb() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                userTypeList = DatabaseUtil.getInstance().getAvailableUserTypes();
                return null;
            }

            protected void onPostExecute(Boolean result) {
                updateUi();
            }

        }.execute(null, null, null);
    }

    public void updateUi() {
        if (userTypeList != null && userTypeList.size() > 0) {
            userTypeContainer.removeAllViews();

            for (UserType userType : userTypeList) {
                View child = getActivity().getLayoutInflater().inflate(R.layout.usertype_item, null);
                TextView userTypeTV = (TextView) child.findViewById(R.id.userType);
                View deleteBtn =  child.findViewById(R.id.deleteBtn);
                userTypeTV.setText(userType.getType());
                deleteBtn.setTag(userType.getId());
                deleteBtn.setOnClickListener(onClickListener);
                userTypeContainer.addView(child);
            }
        }

    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v instanceof ImageView) {
                int id = Integer.parseInt(v.getTag().toString());
                if (id ==1) {
                    Toast.makeText(getActivity(), "Can't delete this type !", Toast.LENGTH_SHORT).show();
                    return;
                }

                NetworkOperations.getInstance().deleteUserType(id);
                ((ParentActivity) getActivity()).showProgress("Deleting ..");
            }
        }
    };

    @Override
    public void onRestApiCallBack(String url, boolean isSucess, int responseType) {
        if( url.equalsIgnoreCase(ApplicationConstants.ADD_USERTYPE_URL ) ||
        url.equalsIgnoreCase(ApplicationConstants.DELETE_USERTYPE_URL )  ){
            ((ParentActivity) getActivity()).cancleProgress();
            loadDataFromDb();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            loadDataFromDb();
        }
    }
}