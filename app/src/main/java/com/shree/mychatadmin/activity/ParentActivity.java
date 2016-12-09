package com.shree.mychatadmin.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.shree.mychatadmin.R;
import com.shree.mychatadmin.network.NetworkOperations;
import com.shree.mychatadmin.network.RestApiCallBack;
import com.shree.mychatadmin.util.ApplicationConstants;

import java.util.List;

public abstract class ParentActivity extends AppCompatActivity implements RestApiCallBack {

	RestApiReceiver onreRestApiReceiver;
	ProgressDialog progressDialog;

	public View coordinatorLayout = null;
	public abstract void notifyActivity(int notificationType);

	@Override
	protected void onResume() {
		super.onResume();
		// Checking internet availibility
		if (!NetworkOperations.getInstance().checkNetworkConnection() && coordinatorLayout != null) {
			Snackbar.make( coordinatorLayout, "Internet is not connected !", Snackbar.LENGTH_INDEFINITE )
					.setAction("OK", new View.OnClickListener() {
						@Override
						public void onClick(View view) {
						}
					})
					.show();
		}
	}

	@Override
	protected void onStart() {
		onreRestApiReceiver = new RestApiReceiver();
		IntentFilter filter = new IntentFilter(ApplicationConstants.REST_RESPONSE_RECEIVER);
		registerReceiver(onreRestApiReceiver, filter);
		super.onStart();
	}

	@Override
	protected void onStop() {
		if(onreRestApiReceiver!=null) {
			unregisterReceiver(onreRestApiReceiver);
			onreRestApiReceiver = null;
		}

		super.onStop();
	}

	public Fragment getActivieFragment(){
		FragmentManager fragmentManager = getSupportFragmentManager();
		List<Fragment> fragments = fragmentManager.getFragments();
		if(fragments != null){
			for(Fragment fragment : fragments){
				if(fragment != null && fragment.isVisible())
					return fragment;
			}
		}
		return  null;
	}

	/**
	 * Activity rest api callback receiver for notifying components about the rest call response
	 */
	private class RestApiReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String url = intent.getStringExtra(ApplicationConstants.REST_URL);
			if (TextUtils.isEmpty(url)) {
				return;
			}
			boolean isSuccess= intent.getBooleanExtra(ApplicationConstants.REST_SUCCESS, false);
			int responseType = intent.getIntExtra(ApplicationConstants.REST_RESPONSE_TYPE, 0);
			onRestApiCallBack(url, isSuccess, responseType);
		}
	}

	public void showProgress(String message) {
		progressDialog = new MyChatProgessDialog(this, R.style.ProgressDialog);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage(message);
		progressDialog.show();
	}

	public void cancleProgress() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.cancel();;
			progressDialog = null;
		}
	}

	public boolean isProgressVisible() {
		if (progressDialog != null && progressDialog.isShowing()) {
			return true;
		}
		return false;
	}

	private boolean doubleBackToExitPressedOnce = false;
	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			finish();
			return;
		}

		boolean isBusy = false;

		if (isProgressVisible()) {
			isBusy = true;
		}

		if (isBusy) {
			this.doubleBackToExitPressedOnce = true;
			Toast.makeText(this, "Please press BACK again to exit !", Toast.LENGTH_SHORT).show();

			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					doubleBackToExitPressedOnce=false;
				}
			}, 2000);
		} else {
			super.onBackPressed();
		}
	}

}