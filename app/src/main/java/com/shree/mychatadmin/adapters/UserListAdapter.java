package com.shree.mychatadmin.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.shree.mychatadmin.R;
import com.shree.mychatadmin.util.ApplicationConstants;
import com.shree.mychatadmin.util.UserDetails;

import java.util.List;

/**
 * Created by SrinivasDonapati on 9/14/2016.
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {

    private List<UserDetails> userList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView userName;
        public TextView emailId;
        public TextView phoneNumber;
        public TextView customMsg;

        public MyViewHolder(View view) {
            super(view);
            userName = (TextView) view.findViewById(R.id.userName);
            emailId = (TextView) view.findViewById(R.id.emailId);
            phoneNumber = (TextView) view.findViewById(R.id.phoneNumber);
            customMsg = (TextView) view.findViewById(R.id.customMsg);
        }

    }

    public UserListAdapter(List<UserDetails> userList) {
        this.userList = userList;
    }

    public void updateList(List<UserDetails> userList){
        this.userList = userList;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UserDetails user = userList.get(position);
        holder.userName.setText(user.getUserName());
        holder.emailId.setText(user.getEmaialId());
        holder.phoneNumber.setText(user.getPhoneNumber());

        if (user.getUserStatus() == ApplicationConstants.BLOCKED_USER_2) {
            holder.customMsg.setVisibility(View.VISIBLE);
            holder.customMsg.setText("SENT");
        } else {
            holder.customMsg.setVisibility(View.GONE);
        }
        holder.userName.setTag(user.getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
